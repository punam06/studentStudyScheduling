package org.example.view;

import org.example.auth.AuthenticationService;
import org.example.auth.UserAccount;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Registration form with separate Student and Admin registration options.
 */
public class RegistrationView extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JRadioButton studentRadio;
    private JRadioButton adminRadio;
    private JButton registerButton;
    private JButton cancelButton;
    private JLabel statusLabel;

    private final AuthenticationService authService;
    private Runnable onRegistrationSuccess;

    /**
     * Creates a new registration view with the specified authentication service.
     *
     * @param authService The authentication service to use for registration
     */
    public RegistrationView(AuthenticationService authService) {
        this.authService = authService;
        initializeUI();
    }

    /**
     * Sets the action to run when registration is successful.
     *
     * @param onRegistrationSuccess The action to run
     */
    public void setOnRegistrationSuccess(Runnable onRegistrationSuccess) {
        this.onRegistrationSuccess = onRegistrationSuccess;
    }

    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setTitle("Study Squad Synchronizer - Registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with app name
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel appNameLabel = new JLabel("Create New Account");
        appNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        appNameLabel.setForeground(Color.WHITE);
        headerPanel.add(appNameLabel, BorderLayout.CENTER);

        // Create registration form
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
        formPanel.add(usernameField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Confirm password field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);

        // Account type selection
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Account Type:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        studentRadio = new JRadioButton("Student", true);
        adminRadio = new JRadioButton("Admin");

        ButtonGroup group = new ButtonGroup();
        group.add(studentRadio);
        group.add(adminRadio);

        radioPanel.add(studentRadio);
        radioPanel.add(adminRadio);
        formPanel.add(radioPanel, gbc);

        // Info label about OTP
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html><i>Note: An OTP will be sent to your email address for verification.</i></html>");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(Color.GRAY);
        formPanel.add(infoLabel, gbc);

        // Status label
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        formPanel.add(statusLabel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        cancelButton = new JButton("Cancel");
        registerButton = new JButton("Register");
        registerButton.setBackground(new Color(70, 130, 180));
        registerButton.setForeground(Color.WHITE);

        buttonPanel.add(cancelButton);
        buttonPanel.add(registerButton);

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set up button actions
        registerButton.addActionListener(e -> attemptRegistration());
        cancelButton.addActionListener(e -> dispose());

        // Allow registration on Enter press
        getRootPane().setDefaultButton(registerButton);

        setContentPane(mainPanel);
    }

    /**
     * Attempts to register with the credentials in the form.
     */
    private void attemptRegistration() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validate input
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            setStatusMessage("Please fill in all fields.", Color.RED);
            return;
        }

        if (!password.equals(confirmPassword)) {
            setStatusMessage("Passwords do not match.", Color.RED);
            return;
        }

        if (password.length() < 6) {
            setStatusMessage("Password must be at least 6 characters long.", Color.RED);
            return;
        }

        if (!isValidEmail(email)) {
            setStatusMessage("Please enter a valid email address.", Color.RED);
            return;
        }

        // Determine account type
        UserAccount.UserRole role = studentRadio.isSelected() ?
            UserAccount.UserRole.STUDENT : UserAccount.UserRole.ADMIN;

        setStatusMessage("Creating account...", Color.BLUE);
        registerButton.setEnabled(false);

        // Attempt registration
        SwingUtilities.invokeLater(() -> {
            try {
                boolean success = authService.registerUser(username, email, password, role);

                if (success) {
                    setStatusMessage("Registration successful! Check your email for OTP.", Color.GREEN);

                    // Show OTP verification dialog if needed
                    if (authService.hasPendingVerification(username)) {
                        showOTPVerificationDialog(username);
                    }

                    // Clear form
                    clearForm();

                    if (onRegistrationSuccess != null) {
                        onRegistrationSuccess.run();
                    }
                } else {
                    setStatusMessage("Registration failed. Please try again.", Color.RED);
                }
            } catch (Exception e) {
                setStatusMessage("Registration error: " + e.getMessage(), Color.RED);
            } finally {
                registerButton.setEnabled(true);
            }
        });
    }

    /**
     * Shows OTP verification dialog for completing registration.
     */
    private void showOTPVerificationDialog(String username) {
        String email = authService.getPendingVerificationEmail(username);

        OTPVerificationDialog otpDialog = new OTPVerificationDialog(
            this,
            "Registration Verification",
            "Please enter the OTP sent to " + email,
            authService,
            username,
            () -> {
                JOptionPane.showMessageDialog(this,
                    "Email verification successful! You can now login.",
                    "Registration Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        );

        otpDialog.setVisible(true);
    }

    /**
     * Validates email format.
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }

    /**
     * Sets the status message with the specified color.
     */
    private void setStatusMessage(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    /**
     * Clears all form fields.
     */
    private void clearForm() {
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        studentRadio.setSelected(true);
        setStatusMessage(" ", Color.BLACK);
    }

    /**
     * Shows this registration view.
     */
    public void showRegistrationForm() {
        clearForm();
        setVisible(true);
        toFront();
    }
}
