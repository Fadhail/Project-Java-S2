package screen;

import constants.Colors;
import dao.UserDAO;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RegisterForm extends JFrame {
    public RegisterForm() {
        super("Register Form");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        setContentPane(backgroundPanel);

        addGuiComponents();

        setVisible(true);
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

    private void addGuiComponents() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name label and field
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Colors.WHITE);
        JTextField nameField = new JTextField(20);
        nameField.setMaximumSize(new Dimension(200, 30));
        nameLabel.setLabelFor(nameField);

        // Email label and field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Colors.WHITE);
        JTextField emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(200, 30));
        emailLabel.setLabelFor(emailField);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Colors.WHITE);
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(200, 30));
        passwordLabel.setLabelFor(passwordField);

        // Register button
        JButton registerButton = createStyledButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                String password = String.valueOf(passwordField.getPassword());

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterForm.this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!email.matches("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$")) {
                    JOptionPane.showMessageDialog(RegisterForm.this, "Invalid email address", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(RegisterForm.this, "Password must be at least 6 characters long", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                UserDAO userDAO = new UserDAO();
                if (userDAO.registerUser(name, email, password)) {
                    JOptionPane.showMessageDialog(RegisterForm.this, "User registered successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    nameField.setText("");
                    emailField.setText("");
                    passwordField.setText("");
                } else {
                    JOptionPane.showMessageDialog(RegisterForm.this, "User registration failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Cancel button
        JButton cancelButton = createStyledButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TitleScreenGui().setVisible(true);
                setVisible(false);
            }
        });

        contentPanel.add(nameLabel, gbc);
        contentPanel.add(nameField, gbc);
        contentPanel.add(emailLabel, gbc);
        contentPanel.add(emailField, gbc);
        contentPanel.add(passwordLabel, gbc);
        contentPanel.add(passwordField, gbc);
        contentPanel.add(registerButton, gbc);
        contentPanel.add(cancelButton, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 25));
        button.setFont(new Font("Serif", Font.PLAIN, 18));
        button.setBackground(Colors.DARK_BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }
}
