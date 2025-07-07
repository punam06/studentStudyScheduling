package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.example.auth.AuthenticationService;
import org.example.auth.UserAccount;
import org.example.model.Member;
import org.example.model.StudyGroup;
import org.example.model.TimeSlot;
import org.example.util.EmailService;
import org.example.view.CalendarGrid;
import org.example.view.ForceScheduleDialog;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private JMenuBar menuBar;
    private JPanel calendarPanel;
    private JPanel memberPanel;
    private JStatusBar statusBar;

    private CalendarGrid calendarGrid;
    private DefaultListModel<Member> memberListModel;
    private JList<Member> memberList;

    private StudyGroup studyGroup;
    private EmailService emailService;

    private AuthenticationService authService;

    // Modify constructor to include error handling
    public MainFrame(AuthenticationService authService) {
        try {
            // Create a new study group
            studyGroup = new StudyGroup("My Study Group");

            // Initialize email service
            emailService = new EmailService();

            // Store authentication service
            this.authService = authService;

            // Initialize UI with error handling
            try {
                initializeUI();
                System.out.println("UI initialized successfully");

                // Apply role-based access control
                applyRoleBasedAccess();
                System.out.println("Role-based access control applied");
            } catch (Exception e) {
                System.err.println("Error during UI initialization: " + e.getMessage());
                e.printStackTrace();
                throw e; // Re-throw to be caught by the outer try-catch
            }
        } catch (Exception e) {
            System.err.println("Error in MainFrame constructor: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error initializing application: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Applies role-based access control based on the current user's role.
     */
    private void applyRoleBasedAccess() {
        boolean isAdmin = authService.isCurrentUserAdmin();
        boolean isStudent = authService.isCurrentUserStudent();

        // Get current user information
        if (authService.isLoggedIn()) {
            UserAccount user = authService.getCurrentUser();
            if (user != null) {
                setTitle("Study Squad Synchronizer - " + user.getUsername() +
                        " (" + user.getRole() + ")");
            }
        }

        // Set permissions based on role
        if (isAdmin) {
            // Admins have full access to everything
            statusBar.setMessage("Logged in as Administrator");
        } else if (isStudent) {
            // Students have restricted access
            statusBar.setMessage("Logged in as Student");

            // Disable admin-only features
            disableAdminOnlyFeatures();
        } else {
            // Regular users
            statusBar.setMessage("Logged in as Regular User");

            // Regular users have most features but not admin ones
            disableAdminOnlyFeatures();
        }
    }

    /**
     * Disables features that are only available to administrators.
     */
    private void disableAdminOnlyFeatures() {
        try {
            // Disable Force Schedule button in toolbar (using safer component access)
            if (calendarPanel != null && calendarPanel.getComponentCount() > 0) {
                Component comp = calendarPanel.getComponent(0);
                if (comp instanceof JPanel) {
                    JPanel toolbarPanel = (JPanel)comp;
                    if (toolbarPanel.getComponentCount() > 0) {
                        Component toolbarComp = toolbarPanel.getComponent(0);
                        if (toolbarComp instanceof JToolBar) {
                            JToolBar toolbar = (JToolBar)toolbarComp;
                            // Find button by text
                            for (int i = 0; i < toolbar.getComponentCount(); i++) {
                                Component btnComp = toolbar.getComponent(i);
                                if (btnComp instanceof JButton && "Force Schedule".equals(((JButton)btnComp).getText())) {
                                    btnComp.setEnabled(false);
                                    System.out.println("Successfully disabled Force Schedule button");
                                }
                            }
                        }
                    }
                }
            }

            // Disable menu items safely
            if (menuBar != null) {
                for (int i = 0; i < menuBar.getMenuCount(); i++) {
                    JMenu menu = menuBar.getMenu(i);
                    if (menu != null && "Schedule".equals(menu.getText())) {
                        for (int j = 0; j < menu.getItemCount(); j++) {
                            JMenuItem item = menu.getItem(j);
                            if (item != null && "Force Schedule Meeting".equals(item.getText())) {
                                item.setEnabled(false);
                                System.out.println("Successfully disabled Force Schedule menu item");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error disabling admin features: " + e.getMessage());
            e.printStackTrace();
            // Continue execution even if this fails
        }
    }

    private void initializeUI() {
        setTitle("Study Squad Synchronizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);

        // Create main panel with border layout
        mainPanel = new JPanel(new BorderLayout());

        // Create and set menu bar
        createMenuBar();

        // Create main content panels
        createCalendarPanel();
        createMemberPanel();

        // Create status bar
        statusBar = new JStatusBar();
        statusBar.setMessage("Ready");

        // Add components to main panel
        mainPanel.add(calendarPanel, BorderLayout.CENTER);
        mainPanel.add(memberPanel, BorderLayout.EAST);
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        // Set content pane
        setContentPane(mainPanel);
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New Schedule");
        newItem.addActionListener(e -> createNewSchedule());
        fileMenu.add(newItem);

        JMenuItem openItem = new JMenuItem("Open Schedule");
        openItem.addActionListener(e -> openSchedule());
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("Save Schedule");
        saveItem.addActionListener(e -> saveSchedule());
        fileMenu.add(saveItem);

        fileMenu.addSeparator();
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem addMemberItem = new JMenuItem("Add Member");
        addMemberItem.addActionListener(e -> showAddMemberDialog());
        editMenu.add(addMemberItem);

        JMenuItem removeMemberItem = new JMenuItem("Remove Member");
        removeMemberItem.addActionListener(e -> removeSelectedMember());
        editMenu.add(removeMemberItem);

        editMenu.addSeparator();
        JMenuItem findSlotsItem = new JMenuItem("Find Common Slots");
        findSlotsItem.addActionListener(e -> findCommonSlots());
        editMenu.add(findSlotsItem);

        // Schedule menu (new menu)
        JMenu scheduleMenu = new JMenu("Schedule");
        JMenuItem addScheduleItem = new JMenuItem("Add Schedule");
        addScheduleItem.addActionListener(e -> showAddScheduleDialog());
        scheduleMenu.add(addScheduleItem);

        scheduleMenu.addSeparator();
        JMenuItem scheduleItem = new JMenuItem("Schedule Meeting");
        scheduleItem.addActionListener(e -> scheduleMeeting());
        scheduleMenu.add(scheduleItem);

        JMenuItem forceScheduleItem = new JMenuItem("Force Schedule Meeting");
        forceScheduleItem.addActionListener(e -> showForceScheduleDialog());
        scheduleMenu.add(forceScheduleItem);

        JMenuItem emailItem = new JMenuItem("Send Invitations");
        emailItem.addActionListener(e -> sendInvitations());
        scheduleMenu.add(emailItem);

        // Settings menu
        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem preferencesItem = new JMenuItem("Preferences");
        preferencesItem.addActionListener(e -> showPreferencesDialog());
        settingsMenu.add(preferencesItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        // Account menu (new)
        JMenu accountMenu = new JMenu("Account");

        JMenuItem profileItem = new JMenuItem("View Profile");
        profileItem.addActionListener(e -> showUserProfile());
        accountMenu.add(profileItem);

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());
        accountMenu.add(logoutItem);

        // Add all menus
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(scheduleMenu);
        menuBar.add(settingsMenu);
        menuBar.add(accountMenu); // Add new account menu
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void createCalendarPanel() {
        calendarPanel = new JPanel(new BorderLayout(5, 5));
        calendarPanel.setBorder(BorderFactory.createTitledBorder("Calendar"));

        // Create toolbar for calendar actions
        JToolBar calendarToolBar = new JToolBar();
        calendarToolBar.setFloatable(false);

        JButton prevButton = new JButton("◀ Previous Day");
        JButton todayButton = new JButton("Today");
        JButton nextButton = new JButton("Next Day ▶");
        JButton findSlotsButton = new JButton("Find Common Slots");
        JButton forceScheduleButton = new JButton("Force Schedule");
        JCheckBox emergencyCheckBox = new JCheckBox("Emergency Scheduling");

        calendarToolBar.add(prevButton);
        calendarToolBar.add(todayButton);
        calendarToolBar.add(nextButton);
        calendarToolBar.addSeparator();
        calendarToolBar.add(findSlotsButton);
        calendarToolBar.add(forceScheduleButton);
        calendarToolBar.addSeparator();
        calendarToolBar.add(emergencyCheckBox);

        // Create the calendar grid
        calendarGrid = new CalendarGrid();
        calendarGrid.setDate(LocalDate.now());
        // Time ranges are now pre-configured: 8 AM to 2 PM and 8 PM to 1 AM
        calendarGrid.setTimeSlotInterval(30);

        // Handle toolbar actions
        prevButton.addActionListener(e -> {
            LocalDate currentDate = calendarGrid.getDate();
            calendarGrid.setDate(currentDate.minusDays(1));
            statusBar.setMessage("Showing schedule for " + calendarGrid.getDate());
        });

        todayButton.addActionListener(e -> {
            calendarGrid.setDate(LocalDate.now());
            statusBar.setMessage("Showing schedule for today");
        });

        nextButton.addActionListener(e -> {
            LocalDate currentDate = calendarGrid.getDate();
            calendarGrid.setDate(currentDate.plusDays(1));
            statusBar.setMessage("Showing schedule for " + calendarGrid.getDate());
        });

        findSlotsButton.addActionListener(e -> findCommonSlots());

        forceScheduleButton.addActionListener(e -> showForceScheduleDialog());

        emergencyCheckBox.addActionListener(e -> {
            boolean isSelected = emergencyCheckBox.isSelected();
            studyGroup.setEmergencyScheduling(isSelected);
            statusBar.setMessage(isSelected ?
                "Emergency scheduling enabled" :
                "Emergency scheduling disabled");
        });

        // Add components to calendar panel
        calendarPanel.add(calendarToolBar, BorderLayout.NORTH);
        calendarPanel.add(new JScrollPane(calendarGrid), BorderLayout.CENTER);
    }

    private void createMemberPanel() {
        memberPanel = new JPanel(new BorderLayout(5, 5));
        memberPanel.setBorder(BorderFactory.createTitledBorder("Members"));
        memberPanel.setPreferredSize(new Dimension(300, getHeight()));

        // Create member list model and list
        memberListModel = new DefaultListModel<>();
        memberList = new JList<>(memberListModel);
        memberList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(memberList);

        // Member action buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add");
        JButton removeButton = new JButton("Remove");
        JButton editButton = new JButton("Edit");

        addButton.addActionListener(e -> showAddMemberDialog());
        removeButton.addActionListener(e -> removeSelectedMember());
        editButton.addActionListener(e -> editSelectedMember());

        actionsPanel.add(addButton);
        actionsPanel.add(removeButton);
        actionsPanel.add(editButton);

        // Add components to member panel
        memberPanel.add(scrollPane, BorderLayout.CENTER);
        memberPanel.add(actionsPanel, BorderLayout.SOUTH);
    }

    private void showAddMemberDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Member",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (!name.isEmpty()) {
                Member newMember = new Member(name, email);
                studyGroup.addMember(newMember);
                memberListModel.addElement(newMember);

                // Update calendar grid with new member
                updateCalendarGridMembers();

                statusBar.setMessage("Member added: " + name);
            }
        }
    }

    private void removeSelectedMember() {
        Member selectedMember = memberList.getSelectedValue();

        if (selectedMember != null) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove " + selectedMember.getName() + "?",
                    "Confirm Removal", JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                studyGroup.removeMember(selectedMember);
                memberListModel.removeElement(selectedMember);

                // Update calendar grid with removed member
                updateCalendarGridMembers();

                statusBar.setMessage("Member removed: " + selectedMember.getName());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a member to remove",
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editSelectedMember() {
        Member selectedMember = memberList.getSelectedValue();

        if (selectedMember != null) {
            JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
            JTextField nameField = new JTextField(selectedMember.getName());
            JTextField emailField = new JTextField(selectedMember.getEmail());

            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Email:"));
            panel.add(emailField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Member",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();

                if (!name.isEmpty()) {
                    selectedMember.setName(name);
                    selectedMember.setEmail(email);

                    // Refresh the list
                    memberList.repaint();

                    statusBar.setMessage("Member updated: " + name);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a member to edit",
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateCalendarGridMembers() {
        List<Member> members = studyGroup.getMembers();
        calendarGrid.setMembers(members);
    }

    private void findCommonSlots() {
        if (studyGroup.getMembers().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please add members to the study group first",
                    "No Members", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        calendarGrid.markCommonSlots();
        statusBar.setMessage("Common time slots marked");
    }

    private void createNewSchedule() {
        int result = JOptionPane.showConfirmDialog(this,
                "This will clear the current schedule. Continue?",
                "New Schedule", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            studyGroup = new StudyGroup("New Study Group");
            memberListModel.clear();
            updateCalendarGridMembers();
            statusBar.setMessage("Created new schedule");
        }
    }

    private void openSchedule() {
        // This would be implemented with file handling
        JOptionPane.showMessageDialog(this,
                "Open schedule functionality will be implemented in a future version",
                "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveSchedule() {
        // This would be implemented with file handling
        JOptionPane.showMessageDialog(this,
                "Save schedule functionality will be implemented in a future version",
                "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showPreferencesDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        SpinnerNumberModel startModel = new SpinnerNumberModel(8, 0, 23, 1);
        SpinnerNumberModel endModel = new SpinnerNumberModel(22, 1, 24, 1);
        SpinnerNumberModel minModel = new SpinnerNumberModel(0, 0, 100, 1);

        JSpinner startTimeSpinner = new JSpinner(startModel);
        JSpinner endTimeSpinner = new JSpinner(endModel);
        JSpinner minMembersSpinner = new JSpinner(minModel);

        panel.add(new JLabel("Start Time (hour):"));
        panel.add(startTimeSpinner);
        panel.add(new JLabel("End Time (hour):"));
        panel.add(endTimeSpinner);
        panel.add(new JLabel("Minimum Members (0 = all):"));
        panel.add(minMembersSpinner);

        int result = JOptionPane.showConfirmDialog(this, panel, "Preferences",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int startHour = (int) startTimeSpinner.getValue();
            int endHour = (int) endTimeSpinner.getValue();
            int minMembers = (int) minMembersSpinner.getValue();

            if (startHour >= endHour) {
                JOptionPane.showMessageDialog(this,
                        "Start time must be earlier than end time",
                        "Invalid Times", JOptionPane.ERROR_MESSAGE);
                return;
            }

            studyGroup.setDefaultStartTime(LocalTime.of(startHour, 0));
            studyGroup.setDefaultEndTime(LocalTime.of(endHour, 0));
            studyGroup.setMinimumMembersRequired(minMembers);

            // Note: Calendar grid now uses fixed time ranges (8 AM-2 PM and 8 PM-1 AM)
            // Time range preferences are stored in the study group for other purposes

            statusBar.setMessage("Preferences updated");
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Study Squad Synchronizer\n" +
                "Version 1.0\n\n" +
                "A tool to help study groups find common meeting times.\n" +
                "© 2025 Bangladesh University of Professionals",
                "About Study Squad Synchronizer",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a dialog to schedule a meeting in a selected time slot.
     */
    private void scheduleMeeting() {
        if (studyGroup.getMembers().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please add members to the study group first",
                    "No Members", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Find common time slots first
        findCommonSlots();

        // Create a panel for the meeting input
        JPanel panel = new JPanel(new BorderLayout());

        // Time selection panel
        JPanel timePanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JSpinner hourSpinner = new JSpinner(new SpinnerNumberModel(14, 0, 23, 1)); // Default to 2 PM
        JSpinner minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 55, 5));
        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(60, 15, 240, 15)); // In minutes

        timePanel.add(new JLabel("Hour (0-23):"));
        timePanel.add(hourSpinner);
        timePanel.add(new JLabel("Minute:"));
        timePanel.add(minuteSpinner);
        timePanel.add(new JLabel("Duration (minutes):"));
        timePanel.add(durationSpinner);

        // Meeting details panel
        JPanel detailsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JTextField subjectField = new JTextField("Study Session");
        JTextArea messageArea = new JTextArea(4, 30);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        detailsPanel.add(new JLabel("Subject:"));
        detailsPanel.add(subjectField);
        detailsPanel.add(new JLabel("Message:"));
        detailsPanel.add(new JScrollPane(messageArea));

        // Combine panels
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Select meeting time:"), BorderLayout.NORTH);
        inputPanel.add(timePanel, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(detailsPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Schedule Meeting", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int hour = (int) hourSpinner.getValue();
            int minute = (int) minuteSpinner.getValue();
            int duration = (int) durationSpinner.getValue();
            String subject = subjectField.getText();
            String message = messageArea.getText();

            LocalDate date = calendarGrid.getDate();
            LocalTime startTime = LocalTime.of(hour, minute);
            LocalTime endTime = startTime.plusMinutes(duration);

            LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

            TimeSlot meetingSlot = new TimeSlot(startDateTime, endDateTime);

            // Show a confirmation dialog with meeting details
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Schedule meeting for:\n" +
                    "Date: " + date + "\n" +
                    "Time: " + startTime.format(DateTimeFormatter.ofPattern("HH:mm")) +
                    " - " + endTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "\n" +
                    "Subject: " + subject + "\n\n" +
                    "Would you like to schedule this meeting?",
                    "Confirm Meeting", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Ask if they want to send invitations now
                int sendNow = JOptionPane.showConfirmDialog(this,
                        "Meeting scheduled. Would you like to send email invitations now?",
                        "Send Invitations", JOptionPane.YES_NO_OPTION);

                if (sendNow == JOptionPane.YES_OPTION) {
                    sendInvitations(meetingSlot, subject, message);
                }

                statusBar.setMessage("Meeting scheduled for " + date + " at " +
                        startTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            }
        }
    }

    /**
     * Shows a dialog to send meeting invitations.
     */
    private void sendInvitations() {
        if (studyGroup.getMembers().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please add members to the study group first",
                    "No Members", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create a panel for meeting details
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));

        // Date picker (simplified for this example)
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2020, 2030, 1));
        JSpinner monthSpinner = new JSpinner(new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
        JSpinner daySpinner = new JSpinner(new SpinnerNumberModel(LocalDate.now().getDayOfMonth(), 1, 31, 1));

        // Time spinners
        JSpinner hourSpinner = new JSpinner(new SpinnerNumberModel(14, 0, 23, 1)); // Default to 2 PM
        JSpinner minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 55, 5));
        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(60, 15, 240, 15)); // In minutes

        // Subject and message
        JTextField subjectField = new JTextField("Study Session");
        JTextArea messageArea = new JTextArea(4, 30);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        // Add components to panel
        panel.add(new JLabel("Date (Y-M-D):"));

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(yearSpinner);
        datePanel.add(new JLabel("-"));
        datePanel.add(monthSpinner);
        datePanel.add(new JLabel("-"));
        datePanel.add(daySpinner);
        panel.add(datePanel);

        panel.add(new JLabel("Time (H:M):"));
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timePanel.add(hourSpinner);
        timePanel.add(new JLabel(":"));
        timePanel.add(minuteSpinner);
        panel.add(timePanel);

        panel.add(new JLabel("Duration (minutes):"));
        panel.add(durationSpinner);

        panel.add(new JLabel("Subject:"));
        panel.add(subjectField);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(new JLabel("Message:"), BorderLayout.NORTH);
        messagePanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        // Combine panels
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(messagePanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, mainPanel,
                "Send Meeting Invitations", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int year = (int) yearSpinner.getValue();
            int month = (int) monthSpinner.getValue();
            int day = (int) daySpinner.getValue();
            int hour = (int) hourSpinner.getValue();
            int minute = (int) minuteSpinner.getValue();
            int duration = (int) durationSpinner.getValue();
            String subject = subjectField.getText();
            String message = messageArea.getText();

            try {
                LocalDate date = LocalDate.of(year, month, day);
                LocalTime startTime = LocalTime.of(hour, minute);
                LocalTime endTime = startTime.plusMinutes(duration);

                LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
                LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

                TimeSlot meetingSlot = new TimeSlot(startDateTime, endDateTime);

                // Send invitations
                sendInvitations(meetingSlot, subject, message);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date or time. Please check your input.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Sends meeting invitations to all group members.
     *
     * @param timeSlot The time slot for the meeting
     * @param subject The subject of the meeting
     * @param message Additional message to include
     */
    private void sendInvitations(TimeSlot timeSlot, String subject, String message) {
        List<Member> members = studyGroup.getMembers();

        if (members.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No members to send invitations to",
                    "No Members", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Generate a preview for the first member
        String preview = emailService.generatePreview(
                members.get(0),
                timeSlot,
                subject,
                message,
                EmailService.TemplateType.MEETING_INVITATION
        );

        // Show preview dialog
        JTextArea previewArea = new JTextArea(preview);
        previewArea.setEditable(false);
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(previewArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        int result = JOptionPane.showConfirmDialog(this, scrollPane,
                "Email Preview", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            boolean success = emailService.sendMeetingInvitations(members, timeSlot, subject, message);

            if (success) {
                statusBar.setMessage("Meeting invitations sent to " + members.size() + " members");
                JOptionPane.showMessageDialog(this,
                        "Invitations sent successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                statusBar.setMessage("Failed to send some invitations");
                JOptionPane.showMessageDialog(this,
                        "There was a problem sending some invitations. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Custom status bar class
    private static class JStatusBar extends JPanel {
        private final JLabel statusLabel;

        public JStatusBar() {
            setBorder(BorderFactory.createLoweredBevelBorder());
            setLayout(new BorderLayout());
            statusLabel = new JLabel();
            statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            add(statusLabel, BorderLayout.WEST);
        }

        public void setMessage(String message) {
            statusLabel.setText(message);
        }
    }

    /**
     * Shows the dialog for forcing a schedule even when not all members are available.
     */
    private void showForceScheduleDialog() {
        List<Member> members = studyGroup.getMembers();

        if (members.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please add members to the study group first",
                    "No Members", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ForceScheduleDialog dialog = new ForceScheduleDialog(this, members, calendarGrid.getDate());
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            TimeSlot timeSlot = dialog.getSelectedTimeSlot();
            String subject = dialog.getSubject();
            String message = dialog.getMessage();
            String priority = dialog.getPriority();

            // Update message to include priority
            message = "PRIORITY: " + priority + "\n\n" + message;

            // Show a confirmation dialog with details of the forced schedule
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Force schedule meeting with priority " + priority + ":\n" +
                    "Date: " + timeSlot.getStartTime().toLocalDate() + "\n" +
                    "Time: " + timeSlot.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) +
                    " - " + timeSlot.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n" +
                    "Subject: " + subject + "\n\n" +
                    "This will override any conflicts. Continue?",
                    "Confirm Force Schedule", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(this,
                        "Emergency meeting scheduled!\n" +
                        "Members have been notified of this high-priority session.",
                        "Meeting Scheduled", JOptionPane.INFORMATION_MESSAGE);

                // Offer to send invitations
                int sendEmails = JOptionPane.showConfirmDialog(this,
                        "Would you like to send emergency meeting invitations to all members now?",
                        "Send Invitations", JOptionPane.YES_NO_OPTION);

                if (sendEmails == JOptionPane.YES_OPTION) {
                    // Send invitations with emergency template
                    sendInvitations(timeSlot, subject, message);
                }

                statusBar.setMessage("Emergency meeting scheduled for " +
                        timeSlot.getStartTime().toLocalDate() + " at " +
                        timeSlot.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
        }
    }

    /**
     * Shows the user profile dialog.
     */
    private void showUserProfile() {
        if (authService.isLoggedIn()) {
            UserAccount user = authService.getCurrentUser();
            if (user != null) {
                JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

                panel.add(new JLabel("Username:"));
                panel.add(new JLabel(user.getUsername()));

                panel.add(new JLabel("Role:"));
                panel.add(new JLabel(user.getRole().toString()));

                String accessLevel;
                if (user.isAdmin()) {
                    accessLevel = "Full Access (Administrator)";
                } else if (user.isStudent()) {
                    accessLevel = "Limited Access (Student)";
                } else {
                    accessLevel = "Standard Access (Regular User)";
                }

                panel.add(new JLabel("Access Level:"));
                panel.add(new JLabel(accessLevel));

                JOptionPane.showMessageDialog(this, panel,
                        "User Profile", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Logs out the current user and returns to the login screen.
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            dispose(); // Close this frame

            // Create new login view without circular references
            SwingUtilities.invokeLater(() -> {
                org.example.view.LoginView loginView = new org.example.view.LoginView(authService);
                loginView.setOnLoginSuccess(() -> {
                    // Create a new MainFrame instance with the current auth service
                    SwingUtilities.invokeLater(() -> {
                        MainFrame mainFrame = new MainFrame(authService);
                        mainFrame.setVisible(true);
                    });
                });
                loginView.setVisible(true);
            });
        }
    }

    private void showAddScheduleDialog() {
        // Create a panel for schedule details
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField dateField = new JTextField();
        JTextField timeField = new JTextField();
        JTextField durationField = new JTextField();

        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Time (HH:MM):"));
        panel.add(timeField);
        panel.add(new JLabel("Duration (minutes):"));
        panel.add(durationField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Schedule",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String dateText = dateField.getText().trim();
            String timeText = timeField.getText().trim();
            String durationText = durationField.getText().trim();

            try {
                // Parse date and time
                LocalDate date = LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
                LocalTime time = LocalTime.parse(timeText, DateTimeFormatter.ISO_LOCAL_TIME);
                int duration = Integer.parseInt(durationText);

                // Create time slot
                LocalDateTime startDateTime = LocalDateTime.of(date, time);
                LocalDateTime endDateTime = startDateTime.plusMinutes(duration);
                TimeSlot newSlot = new TimeSlot(startDateTime, endDateTime);

                // Add to study group
                studyGroup.addTimeSlot(newSlot);

                // Update calendar grid
                calendarGrid.addTimeSlot(newSlot);

                statusBar.setMessage("Schedule added for " + date + " " + time);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date, time, or duration. Please check your input.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
