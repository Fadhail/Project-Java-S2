package screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDFShow extends JFrame {
    private JLabel pdfLabel;
    private JScrollPane scrollPane; // Declare scrollPane here
    private int currentPageIndex = 0;
    private int pageCount = 0;
    private PDDocument document;
    private PDFRenderer pdfRenderer;

    public PDFShow(File file) {
        setTitle("PDF Show");
        setSize(620, 920);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Label to display PDF
        pdfLabel = new JLabel();
        pdfLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT); // Center horizontally
        pdfLabel.setAlignmentY(JComponent.CENTER_ALIGNMENT); // Center vertically
        scrollPane = new JScrollPane(pdfLabel); // Initialize scrollPane here, don't re-declare it
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> {
            dispose();
            // Show dashboard window
            Dashboard dashboard = new Dashboard(null); // If user parameter is needed, pass it here
            dashboard.setVisible(true);
        });

        JButton previousButton = new JButton("Previous");
        previousButton.addActionListener(e -> {
            if (currentPageIndex > 0) {
                currentPageIndex--;
                displayPDF(file, currentPageIndex);
            }
        });

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {
            if (currentPageIndex < pageCount - 1) {
                currentPageIndex++;
                displayPDF(file, currentPageIndex);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);

        scrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                displayPDF(file, currentPageIndex);
            }
        });

        try {
            document = PDDocument.load(file);
            pdfRenderer = new PDFRenderer(document);
            pageCount = document.getNumberOfPages();
            displayPDF(file, currentPageIndex);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void displayPDF(File file, int pageIndex) {
        try (PDDocument document = PDDocument.load(file)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            pageCount = document.getNumberOfPages();
            BufferedImage bufferedImage = pdfRenderer.renderImage(pageIndex);

            // Only scale the image if the width and height are non-zero
            if (scrollPane.getViewport().getWidth() > 0 && scrollPane.getViewport().getHeight() > 0) {
                Image scaledImage = bufferedImage.getScaledInstance(scrollPane.getViewport().getWidth(), scrollPane.getViewport().getHeight(), Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(scaledImage);
                pdfLabel.setIcon(icon);
            }
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
