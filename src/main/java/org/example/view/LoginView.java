package org.example.view;

import org.example.auth.AuthenticationService;
import org.example.auth.UserAccount;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Login form for user authentication with OTP verification using email.
 */
public class LoginView extends JFrame {
    private JTextField emailField;
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

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        // Remove auto-filled admin credentials
        formPanel.add(emailField, gbc);

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
        JLabel infoLabel = new JLabel("<html>Default accounts:<br/>Admin: admin@example.com / admin123<br/>Student: student@example.com / student123</html>");
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
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both email and password");
            return;
        }

        // Clear status
        statusLabel.setText(" ");

        // Disable login button to prevent multiple attempts
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        SwingUtilities.invokeLater(() -> {
            boolean loginResult = authService.initiateLogin(email, password);

            if (loginResult) {
                // Check if we need OTP verification or if login is complete
                if (authService.isLoggedIn()) {
                    // Direct login successful (fallback mode or OTP skipped)
                    statusLabel.setText("Login successful!");
                    dispose(); // Close login window

                    if (onLoginSuccess != null) {
                        onLoginSuccess.run();
                    }
                } else if (authService.hasPendingVerification(email)) {
                    // OTP verification needed
                    String userEmail = authService.getPendingVerificationEmail(email);

                    if (userEmail != null) {
                        // Show OTP verification dialog
                        OTPVerificationDialog otpDialog = new OTPVerificationDialog(
                            this, authService, email, userEmail);
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
                statusLabel.setText("Invalid email or password");
                passwordField.setText("");
            }

            // Re-enable login button
            loginButton.setEnabled(true);
            loginButton.setText("Login");
        });
    }

    /**
     * Shows the registration dialog for creating new accounts.
     */
    private void showRegistrationDialog() {
        RegistrationView registrationView = new RegistrationView(authService);
        registrationView.setOnRegistrationSuccess(() -> {
            statusLabel.setText("Registration successful! You can now login.");
            statusLabel.setForeground(new Color(0, 128, 0));
        });
        registrationView.showRegistrationForm();
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
     * Shows a dialog for password recovery with improved UI and no character limitations.
     */
    private void showForgotPasswordDialog() {
        // Create a more spacious dialog with better layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel("Reset Your Password");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Form panel with improved layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email field with larger, more visible text field
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Email Address:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        JTextField emailField = new JTextField(25); // Wider field
        emailField.setFont(new Font("Arial", Font.PLAIN, 14)); // Larger font
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        formPanel.add(emailField, gbc);

        // New password field
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("New Password:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        JPasswordField newPasswordField = new JPasswordField(25);
        newPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        formPanel.add(newPasswordField, gbc);

        // Confirm password field
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        JPasswordField confirmPasswordField = new JPasswordField(25);
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        formPanel.add(confirmPasswordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        JLabel infoLabel = new JLabel("<html><i>Enter your registered email address and choose a new password.<br/>No character limitations apply.</i></html>");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(Color.GRAY);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        // Create custom dialog
        JDialog dialog = new JDialog(this, "Password Recovery", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton resetButton = new JButton("Reset Password");
        resetButton.setBackground(new Color(70, 130, 180));
        resetButton.setForeground(Color.WHITE);

        cancelButton.addActionListener(e -> dialog.dispose());
        resetButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Validation with improved error messages
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter your email address",
                        "Email Required",
                        JOptionPane.WARNING_MESSAGE);
                emailField.requestFocus();
                return;
            }

            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a new password",
                        "Password Required",
                        JOptionPane.WARNING_MESSAGE);
                newPasswordField.requestFocus();
                return;
            }

            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a valid email address",
                        "Invalid Email",
                        JOptionPane.WARNING_MESSAGE);
                emailField.requestFocus();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog,
                        "Passwords do not match. Please try again.",
                        "Password Mismatch",
                        JOptionPane.WARNING_MESSAGE);
                confirmPasswordField.requestFocus();
                return;
            }

            // Remove the 6-character limitation - allow any length password
            if (newPassword.trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Password cannot be empty or contain only spaces",
                        "Invalid Password",
                        JOptionPane.WARNING_MESSAGE);
                newPasswordField.requestFocus();
                return;
            }

            // Show progress
            resetButton.setEnabled(false);
            resetButton.setText("Resetting...");

            // Perform password reset in background
            SwingUtilities.invokeLater(() -> {
                try {
                    boolean success = authService.recoverPassword(email, newPassword);

                    if (success) {
                        JOptionPane.showMessageDialog(dialog,
                                "Password reset successful!\n\nYou can now login with your new password.",
                                "Password Reset Complete",
                                JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();

                        // Clear the login form and show success message
                        emailField.setText("");
                        passwordField.setText("");
                        statusLabel.setText("Password reset successful! You can now login.");
                        statusLabel.setForeground(new Color(0, 128, 0));

                    } else {
                        JOptionPane.showMessageDialog(dialog,
                                "Password reset failed.\n\nPlease check that the email address is registered and try again.",
                                "Password Reset Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "An error occurred during password reset:\n" + ex.getMessage(),
                            "System Error",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    resetButton.setEnabled(true);
                    resetButton.setText("Reset Password");
                }
            });
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(resetButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog properties
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Make email field focused by default
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                emailField.requestFocus();
            }
        });

        dialog.setVisible(true);
    }
}
