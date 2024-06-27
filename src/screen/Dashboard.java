package screen;

import constants.Colors;
import dao.PdfFileDAO;
import dao.UserDAO;
import model.PdfFile;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        this.pdfFileDAO = new PdfFileDAO();

        setSize(1000, 800);
        setResizable(true); // Allow resizing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addGuiComponents();
        setLocationRelativeTo(null);
        setVisible(true);

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            loadTableData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error while loading table data: " + e.getMessage());
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
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Colors.LIGHT_GRAY); // Example color from your constants

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(52, 58, 64));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Bookshelf Buddy");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(52, 58, 64));

        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(76, 175, 80));
        searchButton.setForeground(Color.BLACK);
        searchButton.setPreferredSize(new Dimension(80, 30));
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().toLowerCase();
            try {
                loadFilteredFiles(searchText);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error while fetching files: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        titlePanel.add(searchPanel, BorderLayout.CENTER);

        // Logout Button with Dropdown
        JButton logoutButton = new JButton(user.getName() + " \u25BE");
        logoutButton.setPreferredSize(new Dimension(100, 30));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setOpaque(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setBorderPainted(false);

        JPopupMenu logoutMenu = new JPopupMenu();
        JMenuItem editProfileItem = new JMenuItem("Edit Profile");
        JMenuItem logoutMenuItem = new JMenuItem("Logout");

        editProfileItem.addActionListener(e -> {
            new EditProfile(user, new UserDAO(), this);
            setVisible(false);
        });

        logoutMenuItem.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                new TitleScreenGui().setVisible(true);
                setVisible(false);
            }
        });

        logoutMenu.add(editProfileItem);
        logoutMenu.add(logoutMenuItem);

        logoutButton.addActionListener(e -> {
            logoutMenu.show(logoutButton, 0, logoutButton.getHeight());
        });

        titlePanel.add(logoutButton, BorderLayout.EAST);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Colors.LIGHT_GRAY); // Adjust color
        tableModel = new DefaultTableModel(new Object[]{"ID", "File Name", "Description", "Category", "Uploaded At"}, 0);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow single row selection
        table.setRowHeight(30); // Adjust row height as needed
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Colors.LIGHT_GRAY);

        // Upload Button
        JButton uploadButton = createIconButton("Upload PDF", "upload_icon.png");
        uploadButton.addActionListener(e -> {
            new UploadFile(user.getId()).setVisible(true);
        });

        // Update Button
        JButton updateButton = createIconButton("Update", "update_icon.png");
        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                try {
                    PdfFile selectedFile = pdfFileDAO.getAllFiles(user.getId()).get(selectedRow);
                    new UpdateFile(selectedFile).setVisible(true);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error while fetching files: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a file to update.");
            }
        });

        // Delete Button
        JButton deleteButton = createIconButton("Delete", "delete_icon.png");
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this file?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        PdfFile fileToDelete = pdfFileDAO.getAllFiles(user.getId()).get(selectedRow);
                        deletePdfFile(fileToDelete);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error while deleting file: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a file to delete.");
            }
        });

        // Show Button
        JButton showButton = createIconButton("Show", "show_icon.png");
        showButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String fileName = (String) tableModel.getValueAt(selectedRow, 1); // Assuming file name is in the second column
                File selectedFile = new File(UPLOAD_DIR + fileName);
                if (selectedFile.exists()) {
                    showPDF(selectedFile);
                } else {
                    JOptionPane.showMessageDialog(this, "File not found: " + selectedFile.getAbsolutePath());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a file to show.");
            }
        });

        buttonPanel.add(uploadButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(showButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
    }

    private void loadFilteredFiles(String searchText) throws SQLException {
        List<PdfFile> files = pdfFileDAO.getAllFiles(user.getId());
        List<PdfFile> filteredFiles = files.stream()
                .filter(file -> file.getFileName().toLowerCase().contains(searchText.toLowerCase())
                        || file.getFileDescription().toLowerCase().contains(searchText.toLowerCase())
                        || file.getFileCategory().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());

        displayFilteredFiles(filteredFiles);
    }

    private void displayFilteredFiles(List<PdfFile> filteredFiles) {
        tableModel.setRowCount(0);
        int rowIndex = 1;
        for (PdfFile file : filteredFiles) {
            tableModel.addRow(new Object[]{rowIndex++, file.getFileName(), file.getFileDescription(), file.getFileCategory(), file.getUploadedAt()});
        }
    }

    private void loadTableData() throws SQLException {
        List<PdfFile> files = pdfFileDAO.getAllFiles(user.getId());
        displayFilteredFiles(files);
    }

    private void deletePdfFile(PdfFile fileToDelete) {
        try {
            int fileId = fileToDelete.getId();
            String fileName = fileToDelete.getFileName();
            File file = new File(UPLOAD_DIR + fileName);

            pdfFileDAO.deleteFile(fileId);

            if (file.exists() && !file.delete()) {
                JOptionPane.showMessageDialog(this, "Failed to delete the file from the filesystem.");
            } else {
                loadTableData();
                JOptionPane.showMessageDialog(this, "File deleted successfully.");
            }
        } catch (SQLException | IOException ex) {
            JOptionPane.showMessageDialog(this, "Error while deleting file: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void refreshDashboard() {
        try {
            loadTableData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error while refreshing dashboard: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
