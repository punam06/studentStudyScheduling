package org.example.view;

import org.example.auth.AuthenticationService;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Dialog for OTP verification during login/registration process.
 */
public class OTPVerificationDialog extends JDialog {
    private JTextField otpField;
    private JButton verifyButton;
    private JButton resendButton;
    private JButton cancelButton;
    private JLabel emailLabel;
    private JLabel instructionLabel;
    private JLabel timerLabel;
    private JLabel statusLabel;

    private AuthenticationService authService;
    private String username;
    private String email;
    private String title;
    private String message;
    private boolean verified;
    private Timer countdownTimer;
    private int remainingSeconds;
    private Runnable onSuccess;

    /**
     * Creates OTP verification dialog for login.
     */
    public OTPVerificationDialog(Window parent, AuthenticationService authService, String username, String email) {
        this(parent, "OTP Verification", "Please enter the OTP sent to " + email, authService, username, null);
    }

    /**
     * Creates OTP verification dialog with custom title and message.
     */
    public OTPVerificationDialog(Window parent, String title, String message, AuthenticationService authService, String username, Runnable onSuccess) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        this.authService = authService;
        this.username = username;
        this.email = authService.getPendingVerificationEmail(username);
        this.title = title;
        this.message = message;
        this.verified = false;
        this.remainingSeconds = 600; // 10 minutes
        this.onSuccess = onSuccess;

        initializeComponents();
        setupLayout();
        setupEventListeners();
        startCountdownTimer();

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        // Email label
        emailLabel = new JLabel("Email: " + (email != null ? email : "N/A"));
        emailLabel.setFont(new Font("Arial", Font.BOLD, 12));

        // Instruction label
        instructionLabel = new JLabel(message != null ? message : "Please enter the OTP sent to your email");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // OTP input field - limit to 6 digits
        otpField = new JTextField(10);
        otpField.setDocument(new NumericDocument(6));
        otpField.setFont(new Font("Arial", Font.BOLD, 16));
        otpField.setHorizontalAlignment(JTextField.CENTER);

        // Timer label
        timerLabel = new JLabel();
        timerLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        timerLabel.setForeground(Color.GRAY);

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Buttons
        verifyButton = new JButton("Verify");
        verifyButton.setBackground(new Color(70, 130, 180));
        verifyButton.setForeground(Color.WHITE);

        resendButton = new JButton("Resend OTP");
        cancelButton = new JButton("Cancel");

        // Initially disable verify button
        verifyButton.setEnabled(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        // Email label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(emailLabel, gbc);

        // Instruction label
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(instructionLabel, gbc);

        // OTP field
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(otpField, gbc);

        // Timer label
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(timerLabel, gbc);

        // Status label
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(statusLabel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(cancelButton);
        buttonPanel.add(resendButton);
        buttonPanel.add(verifyButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        // OTP field listener
        otpField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && otpField.getText().length() == 6) {
                    verifyOTP();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                verifyButton.setEnabled(otpField.getText().length() == 6);
            }
        });

        // Verify button
        verifyButton.addActionListener(e -> verifyOTP());

        // Resend button
        resendButton.addActionListener(e -> resendOTP());

        // Cancel button
        cancelButton.addActionListener(e -> {
            authService.cancelPendingVerification(username);
            dispose();
        });

        // Set default button
        getRootPane().setDefaultButton(verifyButton);
    }

    private void verifyOTP() {
        String otp = otpField.getText().trim();

        if (otp.length() != 6) {
            setStatusMessage("Please enter a 6-digit OTP", Color.RED);
            return;
        }

        setStatusMessage("Verifying...", Color.BLUE);
        verifyButton.setEnabled(false);

        SwingUtilities.invokeLater(() -> {
            try {
                boolean success = authService.completeLogin(username, otp);

                if (success) {
                    verified = true;
                    setStatusMessage("Verification successful!", Color.GREEN);

                    if (onSuccess != null) {
                        onSuccess.run();
                    }

                    Timer delayTimer = new Timer(1500, e -> dispose());
                    delayTimer.setRepeats(false);
                    delayTimer.start();
                } else {
                    setStatusMessage("Invalid or expired OTP", Color.RED);
                    otpField.setText("");
                    otpField.requestFocus();
                }
            } catch (Exception e) {
                setStatusMessage("Verification error: " + e.getMessage(), Color.RED);
            } finally {
                verifyButton.setEnabled(true);
            }
        });
    }

    private void resendOTP() {
        setStatusMessage("Resending OTP...", Color.BLUE);
        resendButton.setEnabled(false);

        SwingUtilities.invokeLater(() -> {
            try {
                boolean success = authService.resendOTP(username);

                if (success) {
                    setStatusMessage("OTP resent successfully!", Color.GREEN);
                    // Reset timer
                    remainingSeconds = 600;
                    updateTimerDisplay();
                } else {
                    setStatusMessage("Failed to resend OTP", Color.RED);
                }
            } catch (Exception e) {
                setStatusMessage("Error resending OTP: " + e.getMessage(), Color.RED);
            } finally {
                // Re-enable resend button after 30 seconds
                Timer enableTimer = new Timer(30000, e -> resendButton.setEnabled(true));
                enableTimer.setRepeats(false);
                enableTimer.start();
            }
        });
    }

    private void startCountdownTimer() {
        countdownTimer = new Timer(1000, e -> {
            remainingSeconds--;
            updateTimerDisplay();

            if (remainingSeconds <= 0) {
                countdownTimer.stop();
                setStatusMessage("OTP has expired. Please request a new one.", Color.RED);
                verifyButton.setEnabled(false);
                otpField.setEnabled(false);
            }
        });
        countdownTimer.start();
        updateTimerDisplay();
    }

    private void updateTimerDisplay() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        timerLabel.setText(String.format("Time remaining: %02d:%02d", minutes, seconds));
    }

    private void setStatusMessage(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    public boolean isVerified() {
        return verified;
    }

    @Override
    public void dispose() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        super.dispose();
    }

    /**
     * Document class to limit OTP input to 6 digits only.
     */
    private static class NumericDocument extends PlainDocument {
        private final int maxLength;

        public NumericDocument(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            if (str == null) return;

            // Only allow digits
            if (str.matches("\\d*") && (getLength() + str.length() <= maxLength)) {
                super.insertString(offset, str, attr);
            }
        }
    }
}
