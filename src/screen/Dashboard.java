package screen;

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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

public class Dashboard extends JFrame {
    private JLabel pdfLabel;
    private JTable table;
    private DefaultTableModel tableModel;
    private static final String UPLOAD_DIR = "public/pdf/";

    public Dashboard(User user) {
        // Call the JFrame constructor
        super("Dashboard");

        // Set the size of the JFrame
        setSize(1920, 1080);

        // Set layout to null
        setLayout(null);

        // Center the JFrame
        setLocationRelativeTo(null);

        // Disable the resize button
        setResizable(false);

        // Set the default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set background color using from constants.Colors
        getContentPane().setBackground(constants.Colors.LIGHT_BLUE);

        // Add GUI components
        addGuiComponents();

        // Make the frame visible
        setVisible(true);

        // Ensure the upload directory exists
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Load table data
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
        button.setBounds(300, 500, 200, 50);
        button.setBackground(constants.Colors.DARK_BLUE);
        return button;
    }

    private void addGuiComponents() {
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Table to display file information
        tableModel = new DefaultTableModel(new Object[]{"ID", "File Name", "File Description", "File Category", "File Path", "Uploaded At"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 50, 700, 300);
        add(scrollPane);

        // Button to upload PDF
        JButton uploadButton = createIconButton("Upload PDF", "upload_icon.png");
        uploadButton.addActionListener(e -> {
            new UploadFile().setVisible(true);
        });
        buttonPanel.add(uploadButton);

        // Button to delete PDF
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

        // Button to show PDF
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
        buttonPanel.add(showButton);

        // Logout Button
        JButton logoutButton = createIconButton("Logout", "logout_icon.png");
        logoutButton.addActionListener(e -> {
            // Open the title screen
            new TitleScreenGui().setVisible(true);
            // Hide the dashboard
            setVisible(false);
        });
        buttonPanel.add(logoutButton);

        // Set layout to GroupLayout
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(scrollPane)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(uploadButton)
                        .addComponent(deleteButton)
                        .addComponent(showButton)
                        .addComponent(logoutButton)
                )
        );
        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        vGroup.addComponent(scrollPane);
        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(uploadButton)
                .addComponent(deleteButton)
                .addComponent(showButton)
                .addComponent(logoutButton)
        );
        layout.setVerticalGroup(vGroup);

        pack(); // Adjust frame size
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

    private void saveFileInfoToDatabase(String fileName, String fileDescription, String fileCategory, String filePath) throws SQLException {
        PdfFileDAO pdfFileDAO = new PdfFileDAO();
        PdfFile pdfFile = new PdfFile(0, fileName, fileDescription, fileCategory, filePath, null); // ID and uploadedAt will be set by DB
        pdfFileDAO.saveFileInfo(pdfFile);
    }

    private void loadTableData() throws SQLException {
        PdfFileDAO pdfFileDAO = new PdfFileDAO();
        List<PdfFile> files = pdfFileDAO.getAllFiles();
        tableModel.setRowCount(0); // Clear existing data
        for (PdfFile file : files) {
            tableModel.addRow(new Object[]{file.getId(), file.getFileName(), file.getFileDescription(), file.getFileCategory(), file.getFilePath(), file.getUploadedAt()});
        }
    }
}
