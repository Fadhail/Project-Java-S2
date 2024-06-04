package screen;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    public LoginForm() {
        // Call the JFrame constructor
        super("Login Form");

        // Set the size of the JFrame
        setSize(800, 600);

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
    }

    private void addGuiComponents() {
        // Email label and field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 50, 100, 30);
        add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setBounds(150, 50, 200, 30);
        add(emailField);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 100, 100, 30);
        add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 200, 30);
        add(passwordField);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(100, 150, 100, 40);
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
                    // Close current login form
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid email or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(loginButton);

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(220, 150, 100, 40);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the title screen
                new TitleScreenGui().setVisible(true);
                // Hide the login form
                setVisible(false);
            }
        });
        add(cancelButton);
    }
}
