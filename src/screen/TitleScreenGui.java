package screen;

import constants.Colors;
import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TitleScreenGui extends JFrame {

    public TitleScreenGui() {
        super("Title Screen");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add the background image panel
        setContentPane(new BackgroundPanel());

        addGuiComponents();
        setVisible(true);
    }

    private void addGuiComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Title label
        JLabel titleLabel = new JLabel("My Library");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 64));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Colors.WHITE);

        // Description label
        JLabel descriptionLabel = new JLabel("Welcome to My Library");
        descriptionLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionLabel.setForeground(Colors.WHITE);

        // Login Panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setOpaque(false);

        // Email label and field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Colors.WHITE);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(200, 30));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Colors.WHITE);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(200, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login button
        JButton loginButton = createStyledButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(150, 25));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retrieve values from fields
                String email = emailField.getText();
                String password = String.valueOf(passwordField.getPassword());

                // Validate input
                if (email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Email and password are required", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Attempt to log in
                User user = UserDAO.loginUser(email, password);
                if (user != null) {
                    JOptionPane.showMessageDialog(null, "Login successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Open dashboard
                    new Dashboard(user).setVisible(true);
                    // Close current title screen
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid email or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Create Account button
        JButton createAccountButton = createStyledButton("Create Account");
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccountButton.setMaximumSize(new Dimension(150, 25));
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterForm().setVisible(true);
                setVisible(false);
            }
        });

        loginPanel.add(emailLabel);
        loginPanel.add(Box.createVerticalStrut(5));
        loginPanel.add(emailField);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(passwordLabel);
        loginPanel.add(Box.createVerticalStrut(5));
        loginPanel.add(passwordField);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(loginButton);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(createAccountButton);

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(descriptionLabel);
        mainPanel.add(Box.createVerticalStrut(50));
        mainPanel.add(loginPanel);
        mainPanel.add(Box.createVerticalGlue());

        // Center the main panel
        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 50));
        button.setFont(new Font("Serif", Font.PLAIN, 18));
        button.setBackground(constants.Colors.DARK_BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    private class BackgroundPanel extends JPanel {
        private BufferedImage backgroundImage;

        public BackgroundPanel() {
            try {
                backgroundImage = ImageIO.read(new File("public/img/background.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TitleScreenGui();
            }
        });
    }
}
