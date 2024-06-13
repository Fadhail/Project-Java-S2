package screen;

import constants.Colors;
import model.User;
import model.PdfFile;
import dao.PdfFileDAO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class Dashboard extends JFrame {
    private User user;
    private int userId;
    private PdfFileDAO pdfFileDAO;
    private JLabel pdfLabel;
    private JTable table;
    private DefaultTableModel tableModel;
    private static final String UPLOAD_DIR = "public/pdf/";

    public Dashboard(User user) {
        super("Dashboard");

        this.user = user;
        this.userId = user.getId();
        this.pdfFileDAO = new PdfFileDAO();

        setSize(1920, 1080);
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Colors.LIGHT_BLUE);

        addGuiComponents();
        setVisible(true);

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            loadTableData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showPDF(File file) {
        PDFShow pdfShow = new PDFShow(file);
        pdfShow.setVisible(true);
    }

    private JButton createIconButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setBackground(Colors.DARK_BLUE);
        return button;
    }

    private void addGuiComponents() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().toLowerCase();
            List<PdfFile> files;
            try {
                files = pdfFileDAO.getAllFiles(user.getId());
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error while fetching files: " + sqlException.getMessage());
                return;
            }
            List<PdfFile> filteredFiles = files.stream()
                    .filter(file -> file.getFileName().toLowerCase().contains(searchText)
                            || file.getFileDescription().toLowerCase().contains(searchText)
                            || file.getFileCategory().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());

            tableModel.setRowCount(0);
            for (PdfFile file : filteredFiles) {
                tableModel.addRow(new Object[]{file.getId(), file.getFileName(), file.getFileDescription(), file.getFileCategory(), file.getUploadedAt()});
            }
        });

        tableModel = new DefaultTableModel(new Object[]{"ID", "File Name", "File Description", "File Category", "Uploaded At"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton uploadButton = createIconButton("Upload PDF", "upload_icon.png");
        uploadButton.addActionListener(e -> {
            new UploadFile(userId).setVisible(true);
        });

        JButton deleteButton = createIconButton("Hapus", "delete_icon.png");
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(Dashboard.this, "Anda yakin ingin menghapus file ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String filePath = (String) tableModel.getValueAt(selectedRow, 2);
                    File selectedPdfFile = new File(filePath);
                    try {
                        if (selectedPdfFile.exists() && selectedPdfFile.delete()) {
                            PdfFileDAO pdfFileDAO = new PdfFileDAO();
                            int fileId = (int) tableModel.getValueAt(selectedRow, 0);
                            pdfFileDAO.deleteFile(fileId);
                            loadTableData();
                            pdfLabel.setIcon(null);
                        } else {
                            JOptionPane.showMessageDialog(Dashboard.this, "Gagal menghapus file atau file tidak ditemukan");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(Dashboard.this, "Gagal menghapus file: " + ex.getMessage());
                    } catch (SecurityException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(Dashboard.this, "Tidak diizinkan menghapus file: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(Dashboard.this, "Pilih file PDF terlebih dahulu");
            }
        });

        JButton showButton = createIconButton("Tampilkan", "show_icon.png");
        showButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String filePath = (String) tableModel.getValueAt(selectedRow, 2);
                File selectedPdfFile = new File(filePath);
                showPDF(selectedPdfFile);
            } else {
                JOptionPane.showMessageDialog(Dashboard.this, "Pilih file PDF terlebih dahulu");
            }
        });

        JButton logoutButton = createIconButton("Logout", "logout_icon.png");
        logoutButton.addActionListener(e -> {
            new TitleScreenGui().setVisible(true);
            setVisible(false);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(uploadButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(showButton);
        buttonPanel.add(logoutButton);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(searchPanel)
                        .addComponent(scrollPane)
                        .addComponent(buttonPanel)
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(searchPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
                        .addComponent(buttonPanel)
        );

        pack();
    }

    private void displayPDF(File file) {
        try (PDDocument document = PDDocument.load(file)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300);
            ImageIcon icon = new ImageIcon(bufferedImage);
            pdfLabel.setIcon(icon);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadTableData() throws SQLException {
        PdfFileDAO pdfFileDAO = new PdfFileDAO();
        List<PdfFile> files = pdfFileDAO.getAllFiles(user.getId());
        tableModel.setRowCount(0);
        for (PdfFile file : files) {
            tableModel.addRow(new Object[]{file.getId(), file.getFileName(), file.getFileDescription(), file.getFileCategory(), file.getUploadedAt()});
        }
    }

    public void refreshDashboard() {
        try {
            loadTableData();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while refreshing dashboard: " + ex.getMessage());
        }
    }
}
