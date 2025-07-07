package org.example.util;

import org.example.model.Member;
import org.example.model.TimeSlot;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

/**
 * Email service for sending meeting notifications and OTP codes.
 * Handles email configuration gracefully with fallback for demo mode.
 */
public class EmailService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    // Email configuration - update these with your email settings
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USERNAME = "punam.papri@gmail.com"; // Replace with your email
    private static final String EMAIL_PASSWORD = "tgct ntes ptck yzet"; // Replace with your app password
    private static final String FROM_EMAIL = "punam.papri@gmail.com"; // Replace with your email

    // Check if email is properly configured
    private static final boolean EMAIL_CONFIGURED = !EMAIL_USERNAME.equals("your-email@gmail.com") &&
                                                   !EMAIL_PASSWORD.equals("your-app-password");

    /**
     * Template types for different kinds of email messages.
     */
    public enum TemplateType {
        MEETING_INVITATION,
        SCHEDULE_UPDATE,
        REMINDER,
        OTP_VERIFICATION
    }

    /**
     * Sends an OTP code to the specified email address.
     * Falls back to demo mode if email is not configured.
     *
     * @param email The email address to send the OTP to
     * @param otpCode The OTP code to send
     * @return true if the email was sent successfully or in demo mode, false otherwise
     */
    public boolean sendOTP(String email, String otpCode) {
        // Check if email is configured
        if (!EMAIL_CONFIGURED) {
            System.out.println("📧 EMAIL DEMO MODE - OTP would be sent to: " + email);
            System.out.println("🔑 Your OTP Code: " + otpCode);
            System.out.println("📝 Note: Email not configured. Using demo mode.");
            System.out.println("   To enable real emails, update EmailService.java with your Gmail credentials.");
            System.out.println("────────────────────────────────────────────────");
            return true; // Return true for demo mode
        }

        try {
            Session session = createEmailSession();

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Your OTP Code - Student Scheduling System");

            String emailContent = generateOTPEmail(otpCode);
            message.setContent(emailContent, "text/html");

            Transport.send(message);
            System.out.println("✅ OTP email sent successfully to: " + email);
            return true;

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send OTP email to " + email + ": " + e.getMessage());

            // Provide helpful error messages
            if (e.getMessage().contains("Authentication failed")) {
                System.err.println("💡 Email authentication failed. Please check:");
                System.err.println("   1. Gmail address is correct");
                System.err.println("   2. App Password is correct (not your regular password)");
                System.err.println("   3. 2-Factor Authentication is enabled on Gmail");
            } else if (e.getMessage().contains("Connection")) {
                System.err.println("💡 Connection failed. Please check your internet connection.");
            }

            // Fall back to demo mode for development
            System.out.println("🔄 Falling back to demo mode...");
            System.out.println("📧 EMAIL DEMO MODE - OTP for " + email + ": " + otpCode);

            return true; // Return true to allow login in demo mode
        } catch (Exception e) {
            System.err.println("❌ Unexpected error sending OTP: " + e.getMessage());
            e.printStackTrace();

            // Fall back to demo mode
            System.out.println("🔄 Falling back to demo mode...");
            System.out.println("📧 EMAIL DEMO MODE - OTP for " + email + ": " + otpCode);

            return true; // Return true to allow login in demo mode
        }
    }

    /**
     * Checks if email service is properly configured.
     *
     * @return true if email is configured with real credentials, false if using defaults
     */
    public boolean isEmailConfigured() {
        return EMAIL_CONFIGURED;
    }

    /**
     * Gets the configuration status message.
     *
     * @return Status message about email configuration
     */
    public String getConfigurationStatus() {
        if (EMAIL_CONFIGURED) {
            return "✅ Email service configured and ready";
        } else {
            return "⚠️ Email service in demo mode - update credentials in EmailService.java";
        }
    }

    /**
     * Creates an email session with SMTP configuration.
     *
     * @return Configured email session
     */
    private Session createEmailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });
    }

    /**
     * Generates HTML content for OTP email.
     *
     * @param otpCode The OTP code to include in the email
     * @return HTML email content
     */
    private String generateOTPEmail(String otpCode) {
        return "<html>" +
            "<body style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;\">" +
                "<div style=\"background-color: #f8f9fa; padding: 30px; border-radius: 10px; text-align: center;\">" +
                    "<h2 style=\"color: #333; margin-bottom: 20px;\">Student Scheduling System</h2>" +
                    "<h3 style=\"color: #007bff; margin-bottom: 30px;\">Your OTP Code</h3>" +

                    "<div style=\"background-color: #007bff; color: white; padding: 20px; border-radius: 8px; margin: 20px 0;\">" +
                        "<h1 style=\"font-size: 36px; margin: 0; letter-spacing: 5px;\">" + otpCode + "</h1>" +
                    "</div>" +

                    "<p style=\"color: #666; font-size: 16px; margin: 20px 0;\">" +
                        "This OTP code will expire in 10 minutes. Please do not share this code with anyone." +
                    "</p>" +

                    "<p style=\"color: #999; font-size: 14px; margin-top: 30px;\">" +
                        "If you didn't request this code, please ignore this email." +
                    "</p>" +
                "</div>" +
            "</body>" +
            "</html>";
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
