package org.example;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.example.auth.SimpleAuthService;
import org.example.util.EmailService;
import org.example.view.LoginView;

public class Main {
    private static SimpleAuthService authService;
    private static EmailService emailService;

    public static void main(String[] args) {
        System.out.println("Starting Study Squad Synchronizer application...");

        try {
            // Initialize email service and display configuration status
            emailService = new EmailService();
            System.out.println(emailService.getConfigurationStatus());

            // Initialize simple authentication service (file-based, no database required)
            authService = new SimpleAuthService();

            // Debug: Print current working directory
            System.out.println("Current working directory: " + System.getProperty("user.dir"));
            System.out.println("Java version: " + System.getProperty("java.version"));

            // Set default exception handler to catch uncaught exceptions
            Thread.setDefaultUncaughtExceptionHandler(Main::handleUncaughtException);

            // Use Swing Event Dispatch Thread for UI operations
            SwingUtilities.invokeLater(() -> {
                try {
                    // Show login view
                    LoginView loginView = new LoginView(authService);
                    loginView.setVisible(true);

                    // Set action to perform on successful login
                    loginView.setOnLoginSuccess(() -> {
                        // After successful login, show main application window
                        SwingUtilities.invokeLater(() -> {
                            new MainFrame(authService).setVisible(true);
                        });
                    });
                } catch (Exception e) {
                    handleException("Error starting application UI", e);
                }
            });
        } catch (Exception e) {
            handleException("Error initializing application", e);
        }
    }

    /**
     * Handles uncaught exceptions in any thread
     */
    private static void handleUncaughtException(Thread t, Throwable e) {
        handleException("Uncaught exception in thread " + t.getName(), e);
    }

    /**
     * Handles exceptions with proper logging and user notification
     */
    private static void handleException(String message, Throwable e) {
        // Print stack trace to console for developers
        System.err.println(message);
        e.printStackTrace();

        // Show error to user
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        String errorDetails = sw.toString();
        System.err.println("Error details: " + errorDetails);

        // Show error dialog on event dispatch thread
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                    message + ":\n" + e.getMessage() +
                    "\nPlease check the console for more details.",
                    "Application Error",
                    JOptionPane.ERROR_MESSAGE);
        });
    }
}