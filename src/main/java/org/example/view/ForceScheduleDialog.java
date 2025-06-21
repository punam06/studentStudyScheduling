package org.example.view;

import org.example.model.Member;
import org.example.model.TimeSlot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dialog for scheduling a forced meeting when not all members are available.
 */
public class ForceScheduleDialog extends JDialog {
    private TimeSlot selectedTimeSlot;
    private boolean confirmed;

    private JSpinner hourSpinner;
    private JSpinner minuteSpinner;
    private JSpinner durationSpinner;
    private JComboBox<String> priorityComboBox;
    private JTextField subjectField;
    private JTextArea messageArea;

    /**
     * Creates a new dialog for forced scheduling.
     *
     * @param parent The parent frame
     * @param members The list of members in the study group
     * @param date The date for the meeting
     */
    public ForceScheduleDialog(JFrame parent, List<Member> members, LocalDate date) {
        super(parent, "Emergency Scheduling", true);

        this.confirmed = false;

        setSize(500, 450);
        setLocationRelativeTo(parent);

        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create warning panel
        JPanel warningPanel = new JPanel(new BorderLayout());
        JLabel warningLabel = new JLabel("⚠️ Warning: Some members may not be available!");
        warningLabel.setForeground(Color.RED);
        warningLabel.setFont(new Font("Arial", Font.BOLD, 14));
        warningPanel.add(warningLabel, BorderLayout.CENTER);
        warningPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Create inputs panel
        JPanel inputsPanel = new JPanel(new GridLayout(7, 2, 5, 5));

        // Time spinners
        hourSpinner = new JSpinner(new SpinnerNumberModel(14, 0, 23, 1)); // Default to 2 PM
        minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 55, 5));
        durationSpinner = new JSpinner(new SpinnerNumberModel(60, 15, 240, 15)); // In minutes

        // Priority level
        String[] priorities = {"Low", "Medium", "High", "Critical"};
        priorityComboBox = new JComboBox<>(priorities);
        priorityComboBox.setSelectedIndex(2); // Default to High

        // Subject and message
        subjectField = new JTextField("URGENT: Study Session");
        messageArea = new JTextArea(5, 30);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        // Add components to inputs panel
        inputsPanel.add(new JLabel("Date:"));
        inputsPanel.add(new JLabel(date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))));

        inputsPanel.add(new JLabel("Start Time:"));
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePanel.add(hourSpinner);
        timePanel.add(new JLabel(":"));
        timePanel.add(minuteSpinner);
        inputsPanel.add(timePanel);

        inputsPanel.add(new JLabel("Duration (minutes):"));
        inputsPanel.add(durationSpinner);

        inputsPanel.add(new JLabel("Priority Level:"));
        inputsPanel.add(priorityComboBox);

        inputsPanel.add(new JLabel("Subject:"));
        inputsPanel.add(subjectField);

        inputsPanel.add(new JLabel("Message:"));
        JScrollPane messageScroll = new JScrollPane(messageArea);
        messageScroll.setPreferredSize(new Dimension(200, 100));
        inputsPanel.add(messageScroll);

        // Add member availability panel
        JPanel memberPanel = new JPanel(new BorderLayout(5, 5));
        memberPanel.setBorder(BorderFactory.createTitledBorder("Member Availability"));

        DefaultListModel<String> memberListModel = new DefaultListModel<>();
        for (Member member : members) {
            memberListModel.addElement(member.getName() + " (" + member.getEmail() + ")");
        }

        JList<String> memberList = new JList<>(memberListModel);
        memberList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                // In a real implementation, we would check actual availability
                // For this example, we'll randomize it
                if (Math.random() > 0.5) {
                    c.setForeground(Color.GREEN.darker());
                } else {
                    c.setForeground(Color.RED);
                }

                return c;
            }
        });

        JScrollPane memberScroll = new JScrollPane(memberList);
        memberPanel.add(memberScroll, BorderLayout.CENTER);

        // Add buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton scheduleButton = new JButton("Force Schedule");

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = false;
                dispose();
            }
        });

        scheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int hour = (int) hourSpinner.getValue();
                int minute = (int) minuteSpinner.getValue();
                int duration = (int) durationSpinner.getValue();

                LocalTime startTime = LocalTime.of(hour, minute);
                LocalTime endTime = startTime.plusMinutes(duration);

                LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
                LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

                selectedTimeSlot = new TimeSlot(startDateTime, endDateTime);
                confirmed = true;
                dispose();
            }
        });

        buttonsPanel.add(cancelButton);
        buttonsPanel.add(scheduleButton);

        // Add all panels to main panel
        mainPanel.add(warningPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(inputsPanel, BorderLayout.NORTH);
        centerPanel.add(memberPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    /**
     * Checks if the user confirmed the forced scheduling.
     *
     * @return true if confirmed, false otherwise
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Gets the selected time slot for the meeting.
     *
     * @return The selected time slot, or null if not confirmed
     */
    public TimeSlot getSelectedTimeSlot() {
        return selectedTimeSlot;
    }

    /**
     * Gets the subject for the meeting.
     *
     * @return The meeting subject
     */
    public String getSubject() {
        return subjectField.getText();
    }

    /**
     * Gets the message for the meeting invitation.
     *
     * @return The message text
     */
    public String getMessage() {
        return messageArea.getText();
    }

    /**
     * Gets the selected priority level.
     *
     * @return The priority level as a string
     */
    public String getPriority() {
        return (String) priorityComboBox.getSelectedItem();
    }
}
