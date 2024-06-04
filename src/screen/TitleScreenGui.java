package screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TitleScreenGui extends JFrame {
    public TitleScreenGui() {
        // Call the JFrame constructor
        super("Title Screen");

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
        // Title label
        JLabel titleLabel = new JLabel("My Library");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setBounds(300, 50, 200, 50);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(constants.Colors.RED);
        add(titleLabel);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(300, 200, 200, 50);
        loginButton.setBackground(constants.Colors.DARK_BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the login form
                new LoginForm().setVisible(true);
                // Hide the title screen
                setVisible(false);
            }
        });
        add(loginButton);

        // Register button
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(300, 300, 200, 50);
        registerButton.setBackground(constants.Colors.DARK_BLUE);
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the register form
                new RegisterForm().setVisible(true);
                // Hide the title screen
                setVisible(false);
            }
        });
        add(registerButton);
    }
}
