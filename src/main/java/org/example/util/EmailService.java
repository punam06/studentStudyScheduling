package org.example.util;

import org.example.model.Member;
import org.example.model.TimeSlot;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * A simulated email service for sending meeting notifications.
 */
public class EmailService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    /**
     * Template types for different kinds of email messages.
     */
    public enum TemplateType {
        MEETING_INVITATION,
        SCHEDULE_UPDATE,
        REMINDER
    }

    /**
     * Sends meeting invitations to all specified members.
     *
     * @param members The members to send invitations to
     * @param timeSlot The scheduled meeting time
     * @param subject The meeting subject
     * @param message Additional message to include
     * @return true if the invitations were sent successfully
     */
    public boolean sendMeetingInvitations(List<Member> members, TimeSlot timeSlot,
            String subject, String message) {
        for (Member member : members) {
            String emailContent = generateEmailContent(
                    member,
                    timeSlot,
                    subject,
                    message,
                    TemplateType.MEETING_INVITATION
            );

            // In a real application, this would send an actual email
            // For simulation purposes, we'll just print it to the console
            System.out.println("Sending email to: " + member.getEmail());
            System.out.println("Content: " + emailContent);
            System.out.println("-------------------------------------");
        }

        return true;
    }

    /**
     * Generates a preview of an invitation email.
     *
     * @param member The member to send the invitation to
     * @param timeSlot The scheduled meeting time
     * @param subject The meeting subject
     * @param message Additional message to include
     * @param templateType The type of email template to use
     * @return The generated email content
     */
    public String generatePreview(Member member, TimeSlot timeSlot,
            String subject, String message, TemplateType templateType) {
        return generateEmailContent(member, timeSlot, subject, message, templateType);
    }

    /**
     * Generates the content for an email based on the template type.
     *
     * @param member The member to send the email to
     * @param timeSlot The scheduled meeting time
     * @param subject The subject of the email
     * @param message Additional message to include
     * @param templateType The type of email template to use
     * @return The generated email content
     */
    private String generateEmailContent(Member member, TimeSlot timeSlot,
            String subject, String message, TemplateType templateType) {
        StringBuilder sb = new StringBuilder();

        switch (templateType) {
            case MEETING_INVITATION:
                sb.append("Dear ").append(member.getName()).append(",\n\n");
                sb.append("You are invited to attend a study group meeting.\n\n");
                sb.append("Subject: ").append(subject).append("\n");
                sb.append("Date: ").append(timeSlot.getStartTime().format(DATE_FORMATTER)).append("\n");
                sb.append("Time: ").append(timeSlot.getStartTime().format(TIME_FORMATTER))
                  .append(" - ").append(timeSlot.getEndTime().format(TIME_FORMATTER)).append("\n\n");

                if (message != null && !message.isEmpty()) {
                    sb.append("Message: ").append(message).append("\n\n");
                }

                sb.append("Please confirm your attendance.\n\n");
                sb.append("Regards,\nStudy Squad Synchronizer");
                break;

            case SCHEDULE_UPDATE:
                sb.append("Dear ").append(member.getName()).append(",\n\n");
                sb.append("The schedule for your study group has been updated.\n\n");
                sb.append("Subject: ").append(subject).append("\n");
                sb.append("New Meeting Time: ").append(timeSlot.getStartTime().format(DATE_FORMATTER))
                  .append(" at ").append(timeSlot.getStartTime().format(TIME_FORMATTER))
                  .append(" - ").append(timeSlot.getEndTime().format(TIME_FORMATTER)).append("\n\n");

                if (message != null && !message.isEmpty()) {
                    sb.append("Message: ").append(message).append("\n\n");
                }

                sb.append("Regards,\nStudy Squad Synchronizer");
                break;

            case REMINDER:
                sb.append("Dear ").append(member.getName()).append(",\n\n");
                sb.append("This is a reminder for your upcoming study group meeting.\n\n");
                sb.append("Subject: ").append(subject).append("\n");
                sb.append("Date: ").append(timeSlot.getStartTime().format(DATE_FORMATTER)).append("\n");
                sb.append("Time: ").append(timeSlot.getStartTime().format(TIME_FORMATTER))
                  .append(" - ").append(timeSlot.getEndTime().format(TIME_FORMATTER)).append("\n\n");

                if (message != null && !message.isEmpty()) {
                    sb.append("Message: ").append(message).append("\n\n");
                }

                sb.append("We're looking forward to seeing you!\n\n");
                sb.append("Regards,\nStudy Squad Synchronizer");
                break;
        }

        return sb.toString();
    }
}
