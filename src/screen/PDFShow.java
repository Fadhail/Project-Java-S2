package screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDFShow extends JFrame {
    private JLabel pdfLabel;
    private int currentPageIndex = 0;
    private int pageCount = 0;

    public PDFShow(File file) {
        setTitle("PDF Show");
        setSize(450, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Label to display PDF
        pdfLabel = new JLabel();
        JScrollPane scrollPane = new JScrollPane(pdfLabel);
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> {
            dispose(); // Close PDF Show window
            // Show dashboard window
            Dashboard dashboard = new Dashboard(null); // If user parameter is needed, pass it here
            dashboard.setVisible(true);
        });

        JButton previousButton = new JButton("Previous");
        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPageIndex > 0) {
                    currentPageIndex--;
                    displayPDF(file, currentPageIndex);
                }
            }
        });

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPageIndex < pageCount - 1) {
                    currentPageIndex++;
                    displayPDF(file, currentPageIndex);
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);

        displayPDF(file, currentPageIndex);
    }

    private void displayPDF(File file, int pageIndex) {
        try (PDDocument document = PDDocument.load(file)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            pageCount = document.getNumberOfPages();
            BufferedImage bufferedImage = pdfRenderer.renderImage(pageIndex);
            ImageIcon icon = new ImageIcon(bufferedImage);
            pdfLabel.setIcon(icon);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
