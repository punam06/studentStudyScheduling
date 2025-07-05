package org.example.view;

import org.example.auth.AuthenticationService;
import org.example.auth.UserAccount;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Login form for user authentication with OTP verification.
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
        JButton forgotPasswordButton = new JButton("Forgot Password");
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);

        buttonPanel.add(forgotPasswordButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set up button actions
        loginButton.addActionListener(e -> attemptLogin());

        registerButton.addActionListener(e -> showRegistrationDialog());
        forgotPasswordButton.addActionListener(e -> showForgotPasswordDialog());

        // Allow login on Enter press
        getRootPane().setDefaultButton(loginButton);

        setContentPane(mainPanel);
    }

    /**
     * Attempts to login with the credentials in the form.
     * This will initiate the OTP verification process or direct login in fallback mode.
     */
    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password");
            return;
        }

        // Clear status
        statusLabel.setText(" ");

        // Disable login button to prevent multiple attempts
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        SwingUtilities.invokeLater(() -> {
            boolean loginResult = authService.initiateLogin(username, password);

            if (loginResult) {
                // Check if we need OTP verification or if login is complete
                if (authService.isLoggedIn()) {
                    // Direct login successful (fallback mode or OTP skipped)
                    statusLabel.setText("Login successful!");
                    dispose(); // Close login window

                    if (onLoginSuccess != null) {
                        onLoginSuccess.run();
                    }
                } else if (authService.hasPendingVerification(username)) {
                    // OTP verification needed
                    String email = authService.getPendingVerificationEmail(username);

                    if (email != null) {
                        // Show OTP verification dialog
                        OTPVerificationDialog otpDialog = new OTPVerificationDialog(
                            this, authService, username, email);
                        otpDialog.setVisible(true);

                        // Check if login was successful after OTP verification
                        if (otpDialog.isVerified() && authService.isLoggedIn()) {
                            statusLabel.setText("Login successful!");
                            dispose(); // Close login window

                            if (onLoginSuccess != null) {
                                onLoginSuccess.run();
                            }
                        } else {
                            statusLabel.setText("Login cancelled or failed");
                        }
                    } else {
                        statusLabel.setText("Email not found for user");
                    }
                }
            } else {
                statusLabel.setText("Invalid username or password");
                passwordField.setText("");
            }

            // Re-enable login button
            loginButton.setEnabled(true);
            loginButton.setText("Login");
        });
    }

    /**
     * Shows a dialog for registering a new user with email verification.
     */
    private void showRegistrationDialog() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        // Add role selection
        String[] roles = {"Student", "Regular User"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPasswordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Register New User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            int selectedRole = roleComboBox.getSelectedIndex();
            UserAccount.UserRole role = selectedRole == 0 ?
                    UserAccount.UserRole.STUDENT : UserAccount.UserRole.USER;

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Username, email, and password cannot be empty",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid email address",
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

            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this,
                        "Password must be at least 6 characters long",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Attempt to register the user
            boolean success = authService.registerUser(username, email, password, role);

            if (success) {
                // Show OTP verification dialog for new registration
                OTPVerificationDialog otpDialog = new OTPVerificationDialog(
                    this, authService, username, email);
                otpDialog.setVisible(true);

                if (otpDialog.isVerified()) {
                    JOptionPane.showMessageDialog(this,
                            "Registration and email verification successful! You can now login.",
                            "Registration Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
            // Error messages are already handled by the AuthenticationService
        }
    }

    /**
     * Validates an email address format.
     *
     * @param email The email to validate
     * @return true if email format is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }

    /**
     * Shows a dialog for password recovery.
     */
    private void showForgotPasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField emailField = new JTextField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmNewPasswordField = new JPasswordField();

        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("New Password:"));
        panel.add(newPasswordField);
        panel.add(new JLabel("Confirm New Password:"));
        panel.add(confirmNewPasswordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Forgot Password",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String email = emailField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword());
            String confirmNewPassword = new String(confirmNewPasswordField.getPassword());

            if (email.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Email and new password cannot be empty",
                        "Password Recovery Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid email address",
                        "Password Recovery Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmNewPassword)) {
                JOptionPane.showMessageDialog(this,
                        "Passwords do not match",
                        "Password Recovery Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(this,
                        "Password must be at least 6 characters long",
                        "Password Recovery Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Attempt to recover the password
            boolean success = authService.recoverPassword(email, newPassword);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Password reset successful! You can now login with the new password.",
                        "Password Recovery Complete",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to reset password. Please check the email and try again.",
                        "Password Recovery Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
