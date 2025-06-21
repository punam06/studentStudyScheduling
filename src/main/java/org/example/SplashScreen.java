package org.example;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JFrame {

    public SplashScreen() {
        setTitle("Study Squad Synchronizer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Center the window
        setUndecorated(true); // Remove window decorations for a cleaner look

        // Set white background
        getContentPane().setBackground(new Color(240, 240, 255));

        // Create panel with border
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 3));

        // Create title label
        JLabel titleLabel = new JLabel("Study Squad Synchronizer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        // Create subtitle
        JLabel subtitleLabel = new JLabel("Loading application...");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        subtitleLabel.setForeground(new Color(100, 100, 100));

        // Progress bar setup
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(300, 20));
        progressBar.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        progressBar.setBackground(Color.WHITE);
        progressBar.setForeground(new Color(70, 130, 180));

        // Version info
        JLabel versionLabel = new JLabel("Version 1.0");
        versionLabel.setHorizontalAlignment(JLabel.CENTER);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(150, 150, 150));
        versionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Add components to the panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(subtitleLabel, BorderLayout.NORTH);
        centerPanel.add(progressBar, BorderLayout.CENTER);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(versionLabel, BorderLayout.SOUTH);

        // Add the panel to the frame
        add(mainPanel);
    }
}