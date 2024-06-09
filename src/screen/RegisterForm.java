package screen;

import dao.UserDAO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterForm extends JFrame {
    public RegisterForm() {
        // Call the JFrame constructor
        super("Register Form");

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
        // Name label and field
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 50, 100, 30);
        add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(150, 50, 200, 30);
        add(nameField);

        // Email label and field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 100, 100, 30);
        add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setBounds(150, 100, 200, 30);
        add(emailField);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 150, 100, 30);
        add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(150, 150, 200, 30);
        add(passwordField);

        // Register button
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(100, 200, 100, 40);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retrieve values from fields
                String name = nameField.getText();
                String email = emailField.getText();
                String password = String.valueOf(passwordField.getPassword());

                // Validate input
                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Register the user
                if (UserDAO.registerUser(name, email, password)) {
                    JOptionPane.showMessageDialog(null, "User registered successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Clear fields after successful registration
                    nameField.setText("");
                    emailField.setText("");
                    passwordField.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "User registration failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the login form
                new LoginForm().setVisible(true);
                // Hide the register form
                setVisible(false);
            }
        });
        add(registerButton);

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(220, 200, 100, 40);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the title screen
                new TitleScreenGui().setVisible(true);
                // Hide the register form
                setVisible(false);
            }
            });
        add(cancelButton);
    }
}
