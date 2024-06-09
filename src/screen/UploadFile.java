package screen;

import dao.PdfFileDAO;
import model.PdfFile;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

public class UploadFile extends JFrame {
    private JTextField fileNameField;
    private JTextField fileDescriptionField;
    private JTextField fileCategoryField;
    private JButton chooseFileButton;
    private JButton uploadButton;
    private File selectedFile;
    private static final String UPLOAD_DIR = "public/pdf/";

    public UploadFile() {
        super("Upload File");

        // Set the size of the JFrame
        setSize(400, 300);

        // Set layout to null
        setLayout(null);

        // Center the JFrame
        setLocationRelativeTo(null);

        // Disable the resize button
        setResizable(false);

        // Set the default close operation
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Initialize components
        JLabel fileNameLabel = new JLabel("File Name:");
        fileNameLabel.setBounds(20, 20, 100, 25);
        add(fileNameLabel);

        fileNameField = new JTextField();
        fileNameField.setBounds(120, 20, 250, 25);
        add(fileNameField);

        JLabel fileDescriptionLabel = new JLabel("Description:");
        fileDescriptionLabel.setBounds(20, 60, 100, 25);
        add(fileDescriptionLabel);

        fileDescriptionField = new JTextField();
        fileDescriptionField.setBounds(120, 60, 250, 25);
        add(fileDescriptionField);

        JLabel fileCategoryLabel = new JLabel("Category:");
        fileCategoryLabel.setBounds(20, 100, 100, 25);
        add(fileCategoryLabel);

        fileCategoryField = new JTextField();
        fileCategoryField.setBounds(120, 100, 250, 25);
        add(fileCategoryField);

        chooseFileButton = new JButton("Choose File");
        chooseFileButton.setBounds(20, 140, 150, 30);
        chooseFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                fileNameField.setText(selectedFile.getName());
            }
        });
        add(chooseFileButton);

        uploadButton = new JButton("Upload");
        uploadButton.setBounds(220, 140, 150, 30);
        uploadButton.addActionListener(e -> {
            if (selectedFile != null) {
                File destinationFile = new File(UPLOAD_DIR + selectedFile.getName());
                try {
                    Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    saveFileInfoToDatabase(selectedFile.getName(), destinationFile.getPath(), fileDescriptionField.getText(), fileCategoryField.getText());
                    JOptionPane.showMessageDialog(this, "File uploaded successfully!");
                    dispose();
                } catch (IOException | SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to upload file.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please choose a file first.");
            }
        });
        add(uploadButton);
    }

    private void saveFileInfoToDatabase(String fileName, String filePath, String fileDescription, String fileCategory) throws SQLException {
        PdfFileDAO pdfFileDAO = new PdfFileDAO();
        PdfFile pdfFile = new PdfFile(0, fileName, fileDescription, fileCategory, filePath, null); // ID and uploadedAt will be set by DB
        pdfFileDAO.saveFileInfo(pdfFile);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UploadFile().setVisible(true));
    }
}
