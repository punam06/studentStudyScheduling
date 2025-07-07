package org.example.view;

import org.example.auth.SimpleAuthService;
import org.example.auth.UserAccount;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Login form for user authentication using simple file-based authentication.
 */
public class LoginView extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;

    private final SimpleAuthService authService;
    private Runnable onLoginSuccess;
    private String currentUsername;

    /**
     * Creates a new login view with the specified authentication service.
     *
     * @param authService The authentication service to use for login
     */
    public LoginView(SimpleAuthService authService) {
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
     * First stage: Validate credentials and initiate OTP verification.
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
        loginButton.setText("Verifying...");

        SwingUtilities.invokeLater(() -> {
            try {
                // First stage: validate credentials
                boolean credentialsValid = authService.initiateLogin(email, password);

                if (credentialsValid) {
                    // Store the email as the username identifier for OTP verification
                    // This is critical because SimpleAuthService uses email as the key for pending verifications
                    currentUsername = email;

                    System.out.println("DEBUG: Setting currentUsername to: " + currentUsername);

                    // Show OTP verification dialog
                    showOTPVerificationDialog();
                } else {
                    statusLabel.setText("Invalid email/username or password");
                    statusLabel.setForeground(Color.RED);
                    passwordField.setText("");

                    // Re-enable login button
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                }
            } catch (Exception ex) {
                statusLabel.setText("Login error: " + ex.getMessage());
                statusLabel.setForeground(Color.RED);
                ex.printStackTrace();

                // Re-enable login button
                loginButton.setEnabled(true);
                loginButton.setText("Login");
            }
        });
    }

    /**
     * Shows the OTP verification dialog for the second stage of login.
     */
    private void showOTPVerificationDialog() {
        String email = authService.getPendingVerificationEmail(currentUsername);

        // Debug logging to help diagnose issues
        System.out.println("DEBUG: Looking for pending verification with key: " + currentUsername);
        System.out.println("DEBUG: Found email: " + email);

        if (email == null) {
            // Fallback: try using the current username directly as email
            if (currentUsername != null && currentUsername.contains("@")) {
                email = currentUsername;
                System.out.println("DEBUG: Using fallback email: " + email);
            } else {
                statusLabel.setText("Error: No pending verification found for " + currentUsername);
                statusLabel.setForeground(Color.RED);
                loginButton.setEnabled(true);
                loginButton.setText("Login");
                return;
            }
        }

        // Create custom OTP verification dialog
        JDialog otpDialog = new JDialog(this, "OTP Verification", true);
        otpDialog.setLayout(new BorderLayout(10, 10));
        otpDialog.setSize(450, 300);
        otpDialog.setLocationRelativeTo(this);

        // Ensure dialog appears on top
        otpDialog.setAlwaysOnTop(true);
        otpDialog.toFront();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create instruction panel
        JPanel instructionPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        JLabel emailLabel = new JLabel("Email: " + email);
        emailLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel instructionLabel = new JLabel("<html>Please enter the OTP code sent to your email.<br>" +
                                         "Check your email inbox or console output for the code.</html>");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel tipLabel = new JLabel("<html><i>Tip: The OTP code is also displayed in the console for testing purposes.</i></html>");
        tipLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        tipLabel.setForeground(Color.GRAY);

        instructionPanel.add(emailLabel);
        instructionPanel.add(instructionLabel);
        instructionPanel.add(tipLabel);

        // Create OTP input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField otpField = new JTextField(10);
        otpField.setFont(new Font("Arial", Font.BOLD, 16));
        otpField.setHorizontalAlignment(JTextField.CENTER);

        // Add document filter to only allow numbers and limit to 6 digits
        otpField.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                if (str == null) return;
                if ((getLength() + str.length()) <= 6 && str.matches("\\d*")) {
                    super.insertString(offs, str, a);
                }
            }
        });

        inputPanel.add(new JLabel("OTP Code: "));
        inputPanel.add(otpField);

        // Create status label
        JLabel otpStatusLabel = new JLabel(" ");
        otpStatusLabel.setHorizontalAlignment(JLabel.CENTER);
        otpStatusLabel.setFont(new Font("Arial", Font.BOLD, 12));

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton verifyButton = new JButton("Verify");
        verifyButton.setBackground(new Color(70, 130, 180));
        verifyButton.setForeground(Color.WHITE);

        buttonPanel.add(cancelButton);
        buttonPanel.add(verifyButton);

        // Add components to main panel
        mainPanel.add(instructionPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(otpStatusLabel, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(otpStatusLabel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        otpDialog.setContentPane(mainPanel);

        // Set up button actions
        cancelButton.addActionListener(e -> {
            otpDialog.dispose();
            loginButton.setEnabled(true);
            loginButton.setText("Login");
        });

        final String finalEmail = email; // For use in lambda
        verifyButton.addActionListener(e -> {
            String otpCode = otpField.getText().trim();
            if (otpCode.isEmpty()) {
                otpStatusLabel.setText("Please enter the OTP code");
                otpStatusLabel.setForeground(Color.RED);
                return;
            }

            if (otpCode.length() != 6) {
                otpStatusLabel.setText("OTP must be 6 digits");
                otpStatusLabel.setForeground(Color.RED);
                return;
            }

            // Disable verify button
            verifyButton.setEnabled(false);
            verifyButton.setText("Verifying...");

            // Verify OTP - try with currentUsername first, then with email
            boolean otpVerified = authService.completeLogin(currentUsername, otpCode);

            if (!otpVerified && !currentUsername.equals(finalEmail)) {
                // Try with email if username verification failed
                otpVerified = authService.completeLogin(finalEmail, otpCode);
            }

            if (otpVerified) {
                otpStatusLabel.setText("OTP verified successfully!");
                otpStatusLabel.setForeground(new Color(0, 128, 0));

                // Close dialog after a short delay
                Timer timer = new Timer(1500, ev -> {
                    otpDialog.dispose();

                    // Successful login - run success callback
                    statusLabel.setText("Login successful!");
                    statusLabel.setForeground(new Color(0, 128, 0));
                    dispose(); // Close login window

                    if (onLoginSuccess != null) {
                        onLoginSuccess.run();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                otpStatusLabel.setText("Invalid OTP code. Please try again.");
                otpStatusLabel.setForeground(Color.RED);
                verifyButton.setEnabled(true);
                verifyButton.setText("Verify");
                otpField.selectAll(); // Select all text for easy replacement
            }
        });

        // Allow Enter key to verify OTP
        otpField.addActionListener(e -> verifyButton.doClick());

        // Set default button and focus
        otpDialog.getRootPane().setDefaultButton(verifyButton);

        // Show dialog and ensure it gets focus
        otpDialog.setVisible(true);
        otpField.requestFocusInWindow();
    }

    /**
     * Shows the registration dialog for creating new accounts.
     */
    private void showRegistrationDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Register New User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Validation
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = authService.register(username, email, password);
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful! You can now login.", "Registration Complete", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("Registration successful! You can now login.");
                statusLabel.setForeground(new Color(0, 128, 0));
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Username or email may already exist.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Shows a simplified password recovery dialog.
     */
    private void showForgotPasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField emailField = new JTextField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("New Password:"));
        panel.add(newPasswordField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Reset Password",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String email = emailField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (email.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email and password are required", "Reset Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match", "Reset Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Implement actual password reset functionality
            boolean resetSuccess = authService.resetPassword(email, newPassword);

            if (resetSuccess) {
                JOptionPane.showMessageDialog(this,
                        "Password has been reset successfully. You can now login with your new password.",
                        "Password Reset Successful", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("Password reset successful! You can now login.");
                statusLabel.setForeground(new Color(0, 128, 0));
            } else {
                JOptionPane.showMessageDialog(this,
                        "Password reset failed. Please verify that the email is registered in the system.",
                        "Reset Error", JOptionPane.ERROR_MESSAGE);
            }
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
}
