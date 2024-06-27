package screen;

import dao.PdfFileDAO;
import model.PdfFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;

public class UpdateFile extends JFrame {
    private PdfFile pdfFile;
    private PdfFileDAO pdfFileDAO;
    private JTextField fileNameField;
    private JTextArea fileDescriptionField;
    private JTextField fileCategoryField;
    private JButton chooseFileButton;
    private File selectedFile;

    public UpdateFile(PdfFile pdfFile) {
        super("Update File");

        this.pdfFile = pdfFile;
        this.pdfFileDAO = new PdfFileDAO();

        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addGuiComponents();

        setVisible(true); // Set visible after components are added
    }

    private void addGuiComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 2));

        panel.add(new JLabel("File Name:"));
        fileNameField = new JTextField(pdfFile.getFileName());
        panel.add(fileNameField);

        panel.add(new JLabel("File Description:"));
        fileDescriptionField = new JTextArea(pdfFile.getFileDescription());
        panel.add(new JScrollPane(fileDescriptionField));

        panel.add(new JLabel("File Category:"));
        fileCategoryField = new JTextField(pdfFile.getFileCategory());
        panel.add(fileCategoryField);

        chooseFileButton = new JButton("Choose File");
        chooseFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                fileNameField.setText(selectedFile.getName());
            }
        });
        panel.add(chooseFileButton);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    saveFile();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(UpdateFile.this, "Error while updating file: " + ex.getMessage());
                }
            }
        });
        panel.add(saveButton);

        add(panel);
    }

    private void saveFile() throws SQLException {
        if (selectedFile != null) {
            // Update file name if a new file is selected
            pdfFile.setFileName(selectedFile.getName());
        }
        pdfFile.setFileDescription(fileDescriptionField.getText());
        pdfFile.setFileCategory(fileCategoryField.getText());

        pdfFileDAO.updateFile(pdfFile);

        JOptionPane.showMessageDialog(this, "File updated successfully");
        dispose();
    }
}
