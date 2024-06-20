package screen;

import constants.Colors;
import model.User;
import model.PdfFile;
import dao.PdfFileDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

        setSize(1000, 800);
        setLocationRelativeTo(null);
        setResizable(true); // Allow resizing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                refreshDashboard();
            }
        });
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
        // Initialize pdfLabel
        pdfLabel = new JLabel();
        pdfLabel.setHorizontalAlignment(JLabel.CENTER);

        // Search Panel
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
            int rowIndex = 1; // Start index from 1
            for (PdfFile file : filteredFiles) {
                tableModel.addRow(new Object[]{rowIndex++, file.getFileName(), file.getFileDescription(), file.getFileCategory(), file.getUploadedAt()});
            }
        });

        // Show Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "File Name", "File Description", "File Category", "Uploaded At"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Upload Button
        JButton uploadButton = createIconButton("Upload PDF", "upload_icon.png");
        uploadButton.addActionListener(e -> {
            new UploadFile(userId).setVisible(true);
        });

        // Delete Button
        JButton deleteButton = createIconButton("Hapus", "delete_icon.png");
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(Dashboard.this, "Anda yakin ingin menghapus file ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        // Fetch the actual file ID using the row index
                        PdfFile file = pdfFileDAO.getAllFiles(user.getId()).get(selectedRow);
                        int fileId = file.getId();
                        String fileName = file.getFileName();
                        File fileToDelete = new File(UPLOAD_DIR + fileName);

                        // Delete from database
                        pdfFileDAO.deleteFile(fileId);

                        // Delete the file from the file system
                        if (fileToDelete.exists() && !fileToDelete.delete()) {
                            JOptionPane.showMessageDialog(Dashboard.this, "Failed to delete the file from the filesystem.");
                        }

                        // Refresh the table data
                        loadTableData();
                        pdfLabel.setIcon(null);
                    } catch (SQLException | IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(Dashboard.this, "Error while deleting file: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(Dashboard.this, "Pilih file PDF terlebih dahulu");
            }
        });

        // Show Button
        JButton showButton = createIconButton("Tampilkan", "show_icon.png");
        showButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String fileName = (String) tableModel.getValueAt(selectedRow, 1); // Ensure this column is the file name or adjust accordingly
                File selectedPdfFile = new File(UPLOAD_DIR + fileName); // Ensure the file path is correctly constructed
                if (selectedPdfFile.exists()) {
                    showPDF(selectedPdfFile);
                } else {
                    JOptionPane.showMessageDialog(Dashboard.this, "File not found: " + selectedPdfFile.getAbsolutePath());
                }
            } else {
                JOptionPane.showMessageDialog(Dashboard.this, "Please select a PDF file first");
            }
        });

        // Logout Button
        JButton logoutButton = createIconButton("Logout", "logout_icon.png");
        logoutButton.addActionListener(e -> {
            new TitleScreenGui().setVisible(true);
            setVisible(false);
        });

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(uploadButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(showButton);
        buttonPanel.add(logoutButton);

        // Set Layout
        setLayout(new BorderLayout());

        // Add Components to Layout
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(pdfLabel, BorderLayout.EAST); // Add pdfLabel to layout
    }

    private void loadTableData() throws SQLException {
        PdfFileDAO pdfFileDAO = new PdfFileDAO();
        List<PdfFile> files = pdfFileDAO.getAllFiles(user.getId());
        tableModel.setRowCount(0);
        int rowIndex = 1; // Start index from 1
        for (PdfFile file : files) {
            tableModel.addRow(new Object[]{rowIndex++, file.getFileName(), file.getFileDescription(), file.getFileCategory(), file.getUploadedAt()});
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
