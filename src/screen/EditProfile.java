package screen;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditProfile extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private User user;
    private UserDAO userDAO;
    private Dashboard dashboard;

    public EditProfile(User user, UserDAO userDAO, Dashboard dashboard) {
        super("Edit Profile");

        this.user = user;
        this.userDAO = userDAO;
        this.dashboard = dashboard;

        setSize(400, 200); // Adjusted height for better fit
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addGuiComponents();

        setVisible(true);
    }

    private void addGuiComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around components

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField(20);
        usernameField.setText(user.getName());
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        emailField = new JTextField(20);
        emailField.setText(user.getEmail());
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        passwordField = new JPasswordField(20);
        passwordField.setText(user.getPassword());
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Span across two columns
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Button panel for better button alignment
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProfile();
            }
        });
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToDashboard();
            }
        });
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, gbc);

        add(panel);
    }

    private void saveProfile() {
        user.setName(usernameField.getText());
        user.setEmail(emailField.getText());
        user.setPassword(new String(passwordField.getPassword()));
        if (userDAO.updateUser(user)) {
            JOptionPane.showMessageDialog(this, "Profile updated successfully");
            dashboard.refreshDashboard();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error updating profile");
        }
        new Dashboard(user).setVisible(true);
    }

    private void returnToDashboard() {
        dispose();
        dashboard.setVisible(true); // Show dashboard
    }
}
