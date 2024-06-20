package screen;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDFShow extends JFrame {
    private JLabel pdfLabel;
    private int currentPageIndex = 0;
    private int pageCount = 0;
    private PDDocument document;
    private PDFRenderer pdfRenderer;

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
        previousButton.addActionListener(e -> {
            if (currentPageIndex > 0) {
                currentPageIndex--;
                displayPDF(currentPageIndex);
            }
        });

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {
            if (currentPageIndex < pageCount - 1) {
                currentPageIndex++;
                displayPDF(currentPageIndex);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);

        try {
            document = PDDocument.load(file);
            pdfRenderer = new PDFRenderer(document);
            pageCount = document.getNumberOfPages();
            displayPDF(currentPageIndex);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void displayPDF(int pageIndex) {
        try {
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageIndex, 300);
            ImageIcon icon = new ImageIcon(bufferedImage);
            pdfLabel.setIcon(icon);
            pdfLabel.revalidate();
            pdfLabel.repaint();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        try {
            if (document != null) {
                document.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
