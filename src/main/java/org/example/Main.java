package org.example;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.example.auth.AuthenticationService;
import org.example.view.LoginView;

public class Main {
    private static AuthenticationService authService;

    public static void main(String[] args) {
        System.out.println("Starting Study Squad Synchronizer application...");

        // Initialize authentication service
        authService = new AuthenticationService();
        System.out.println("Authentication service initialized with default accounts:");
        System.out.println("- Admin account: username='admin', password='admin123'");
        System.out.println("- Student account: username='student', password='student123'");

        // Set default exception handler to catch uncaught exceptions
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.err.println("Uncaught exception in thread " + t.getName());
            e.printStackTrace();
            showErrorDialog(e);
        });

        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Showing splash screen...");

                // Show splash screen
                SplashScreen splashScreen = new SplashScreen();
                splashScreen.setVisible(true);

                // Create one-time timer that fires only once
                Timer timer = new Timer(2000, null);
                timer.setRepeats(false); // Important: prevent repeated firing

                // Add action listener to handle timer event
                timer.addActionListener(e -> {
                    splashScreen.dispose();
                    System.out.println("Splash screen closed, showing login view...");

                    try {
                        // Show login view
                        LoginView loginView = new LoginView(authService);
                        loginView.setOnLoginSuccess(() -> {
                            System.out.println("Login successful, showing main application...");

                            // Create and show MainFrame on the Event Dispatch Thread
                            SwingUtilities.invokeLater(() -> {
                                try {
                                    System.out.println("Creating MainFrame instance...");
                                    MainFrame mainFrame = new MainFrame(authService);
                                    System.out.println("MainFrame created, making it visible...");
                                    mainFrame.setVisible(true);
                                    System.out.println("MainFrame should now be visible");
                                } catch (Throwable ex) {
                                    System.err.println("ERROR creating or showing MainFrame:");
                                    ex.printStackTrace();
                                    showErrorDialog(ex);
                                }
                            });
                        });
                        loginView.setVisible(true);
                    } catch (Throwable ex) {
                        System.err.println("ERROR in timer callback:");
                        ex.printStackTrace();
                        showErrorDialog(ex);
                    }
                });

                // Start the timer
                timer.start();
            } catch (Throwable ex) {
                System.err.println("ERROR in GUI initialization:");
                ex.printStackTrace();
                showErrorDialog(ex);
            }
        });
    }

    /**
     * Shows an error dialog with exception details
     */
    private static void showErrorDialog(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        String message = "An error occurred: " + e.getMessage() +
                         "\n\nPlease report this error to the developers." +
                         "\n\nStack trace (for developers):\n" + stackTrace.substring(0, Math.min(stackTrace.length(), 500)) + "...";

        JOptionPane.showMessageDialog(null, message,
                                     "Application Error", JOptionPane.ERROR_MESSAGE);
    }
}