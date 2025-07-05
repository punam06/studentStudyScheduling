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
 * Dialog for OTP verification during login process.
 */
public class OTPVerificationDialog extends JDialog {
    private JTextField otpField;
    private JButton verifyButton;
    private JButton resendButton;
    private JButton cancelButton;
    private JLabel emailLabel;
    private JLabel instructionLabel;
    private JLabel timerLabel;

    private AuthenticationService authService;
    private String username;
    private String email;
    private boolean verified;
    private Timer countdownTimer;
    private int remainingSeconds;

    public OTPVerificationDialog(Window parent, AuthenticationService authService, String username, String email) {
        super(parent, "OTP Verification", ModalityType.APPLICATION_MODAL);
        this.authService = authService;
        this.username = username;
        this.email = email;
        this.verified = false;
        this.remainingSeconds = 600; // 10 minutes

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
        instructionLabel = new JLabel("Enter the 6-digit OTP code sent to your email:");
        instructionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        emailLabel = new JLabel("Email: " + maskEmail(email));
        emailLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        emailLabel.setForeground(Color.GRAY);

        otpField = new JTextField(6);
        otpField.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        otpField.setHorizontalAlignment(JTextField.CENTER);
        otpField.setDocument(new OTPDocument(6)); // Limit to 6 digits

        verifyButton = new JButton("Verify");
        verifyButton.setPreferredSize(new Dimension(100, 30));
        verifyButton.setEnabled(false);

        resendButton = new JButton("Resend OTP");
        resendButton.setPreferredSize(new Dimension(100, 30));

        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 30));

        timerLabel = new JLabel("Time remaining: 10:00");
        timerLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        timerLabel.setForeground(Color.BLUE);
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("OTP Verification");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        mainPanel.add(titleLabel, gbc);

        // Instructions
        gbc.gridy = 1;
        mainPanel.add(instructionLabel, gbc);

        // Email
        gbc.gridy = 2;
        mainPanel.add(emailLabel, gbc);

        // OTP Field
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(otpField, gbc);

        // Timer
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(timerLabel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(verifyButton);
        buttonPanel.add(resendButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        // OTP field listeners
        otpField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Only allow digits
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && verifyButton.isEnabled()) {
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
    }

    private void verifyOTP() {
        String otp = otpField.getText().trim();

        if (otp.length() != 6) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid 6-digit OTP code.",
                "Invalid OTP",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        verifyButton.setEnabled(false);
        verifyButton.setText("Verifying...");

        // Verify OTP in background thread
        SwingUtilities.invokeLater(() -> {
            boolean success = authService.completeLogin(username, otp);

            if (success) {
                verified = true;
                JOptionPane.showMessageDialog(this,
                    "OTP verified successfully! You are now logged in.",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid or expired OTP code. Please try again.",
                    "Verification Failed",
                    JOptionPane.ERROR_MESSAGE);
                otpField.setText("");
                verifyButton.setEnabled(true);
                verifyButton.setText("Verify");
            }
        });
    }

    private void resendOTP() {
        resendButton.setEnabled(false);
        resendButton.setText("Sending...");

        SwingUtilities.invokeLater(() -> {
            boolean success = authService.resendOTP(username);

            if (success) {
                JOptionPane.showMessageDialog(this,
                    "OTP has been resent to your email address.",
                    "OTP Resent",
                    JOptionPane.INFORMATION_MESSAGE);

                // Reset timer
                remainingSeconds = 600;
                startCountdownTimer();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to resend OTP. Please try again later.",
                    "Resend Failed",
                    JOptionPane.ERROR_MESSAGE);
            }

            resendButton.setEnabled(true);
            resendButton.setText("Resend OTP");
        });
    }

    private void startCountdownTimer() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        countdownTimer = new Timer(1000, e -> {
            remainingSeconds--;

            if (remainingSeconds <= 0) {
                countdownTimer.stop();
                timerLabel.setText("OTP has expired");
                timerLabel.setForeground(Color.RED);
                verifyButton.setEnabled(false);
                otpField.setEnabled(false);
            } else {
                int minutes = remainingSeconds / 60;
                int seconds = remainingSeconds % 60;
                timerLabel.setText(String.format("Time remaining: %d:%02d", minutes, seconds));

                if (remainingSeconds <= 60) {
                    timerLabel.setForeground(Color.RED);
                }
            }
        });

        countdownTimer.start();
    }

    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "N/A";
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return email;
        }

        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (username.length() <= 2) {
            return username.charAt(0) + "*" + domain;
        }

        return username.substring(0, 2) + "*".repeat(username.length() - 2) + domain;
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
     * Custom document class to limit OTP field to digits only and maximum length.
     */
    private static class OTPDocument extends PlainDocument {
        private int maxLength;

        public OTPDocument(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            if (str == null) return;

            // Filter out non-digits
            StringBuilder filtered = new StringBuilder();
            for (char c : str.toCharArray()) {
                if (Character.isDigit(c)) {
                    filtered.append(c);
                }
            }

            // Check length limit
            if (getLength() + filtered.length() <= maxLength) {
                super.insertString(offset, filtered.toString(), attr);
            }
        }
    }
}
