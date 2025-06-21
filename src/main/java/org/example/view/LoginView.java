package org.example.view;

import org.example.auth.AuthenticationService;
import org.example.auth.UserAccount;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Login form for user authentication.
 */
public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;

    private final AuthenticationService authService;
    private Runnable onLoginSuccess;

    /**
     * Creates a new login view with the specified authentication service.
     *
     * @param authService The authentication service to use for login
     */
    public LoginView(AuthenticationService authService) {
        this.authService = authService;

        initializeUI();
    }

    /**
     * Sets the action to run when login is successful.
     *
     * @param onLoginSuccess The action to run
     */
    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setTitle("Study Squad Synchronizer - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with app name and icon
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel appNameLabel = new JLabel("Study Squad Synchronizer");
        appNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        appNameLabel.setForeground(Color.WHITE);
        headerPanel.add(appNameLabel, BorderLayout.CENTER);

        // Create login form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        // Remove auto-filled admin credentials
        formPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        // Remove auto-filled admin password
        formPanel.add(passwordField, gbc);

        // Info label with default credentials
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html>Default accounts:<br/>Admin: admin / admin123<br/>Student: student / student123</html>");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(Color.GRAY);
        formPanel.add(infoLabel, gbc);

        // Status label
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        formPanel.add(statusLabel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        registerButton = new JButton("Register");
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);

        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set up button actions
        loginButton.addActionListener(e -> attemptLogin());

        registerButton.addActionListener(e -> showRegistrationDialog());

        // Allow login on Enter press
        getRootPane().setDefaultButton(loginButton);

        setContentPane(mainPanel);
    }

    /**
     * Attempts to login with the credentials in the form.
     */
    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password");
            return;
        }

        boolean success = authService.login(username, password);

        if (success) {
            dispose(); // Close login window

            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            statusLabel.setText("Invalid username or password");
            passwordField.setText("");
        }
    }

    /**
     * Shows a dialog for registering a new user.
     */
    private void showRegistrationDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        // Add role selection
        String[] roles = {"Student", "Regular User"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPasswordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Register New User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            int selectedRole = roleComboBox.getSelectedIndex();
            UserAccount.UserRole role = selectedRole == 0 ?
                    UserAccount.UserRole.STUDENT : UserAccount.UserRole.USER;

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Username and password cannot be empty",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this,
                        "Passwords do not match",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = authService.registerUser(username, password, role);

            if (success) {
                String roleText = role == UserAccount.UserRole.STUDENT ? "student" : "user";
                JOptionPane.showMessageDialog(this,
                        "New " + roleText + " registered successfully. You can now login.",
                        "Registration Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Username already exists",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
