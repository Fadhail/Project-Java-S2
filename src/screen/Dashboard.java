package screen;

import model.User;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {
    private User user;

    public Dashboard(User user) {
//        this.user = user;

        // Call the JFrame constructor
        super("Dashboard");

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
        // Table
        JTable table = new JTable();
        table.setBounds(50, 50, 700, 400);
        add(table);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(300, 500, 200, 50);
        logoutButton.setBackground(constants.Colors.DARK_BLUE);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> {
            // Open the title screen
            new TitleScreenGui().setVisible(true);
            // Hide the dashboard
            setVisible(false);
        });
        add(logoutButton);
    }
}
