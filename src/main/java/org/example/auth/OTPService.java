package org.example.auth;

import org.example.database.DatabaseConfig;
import org.example.util.EmailService;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Service for handling OTP (One-Time Password) generation and verification.
 */
public class OTPService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;
    private final EmailService emailService;

    public OTPService() {
        this.emailService = new EmailService();
    }

    /**
     * Generates and sends an OTP to the user's email.
     *
     * @param userId The user ID
     * @param email The email address to send OTP to
     * @return true if OTP was generated and sent successfully, false otherwise
     */
    public boolean generateAndSendOTP(int userId, String email) {
        // Generate random 6-digit OTP
        String otp = generateOTP();

        // Store OTP in database
        if (storeOTP(userId, otp)) {
            // Send OTP via email
            return emailService.sendOTP(email, otp);
        }

        return false;
    }

    /**
     * Verifies an OTP for a user.
     *
     * @param userId The user ID
     * @param otp The OTP to verify
     * @return true if OTP is valid and not expired, false otherwise
     */
    public boolean verifyOTP(int userId, String otp) {
        String sql = """
            SELECT id FROM otp_tokens 
            WHERE user_id = ? AND otp_code = ? AND expires_at > NOW() AND is_used = FALSE
            ORDER BY created_at DESC LIMIT 1
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, otp);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int otpId = rs.getInt("id");
                    // Mark OTP as used
                    markOTPAsUsed(otpId);
                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error verifying OTP: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Generates a random OTP.
     *
     * @return 6-digit OTP string
     */
    private String generateOTP() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }

    /**
     * Stores an OTP in the database.
     *
     * @param userId The user ID
     * @param otp The OTP code
     * @return true if stored successfully, false otherwise
     */
    private boolean storeOTP(int userId, String otp) {
        // First, invalidate any existing unused OTPs for this user
        invalidateExistingOTPs(userId);

        String sql = "INSERT INTO otp_tokens (user_id, otp_code, expires_at) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, otp);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES)));

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error storing OTP: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Invalidates all existing unused OTPs for a user.
     *
     * @param userId The user ID
     */
    private void invalidateExistingOTPs(int userId) {
        String sql = "UPDATE otp_tokens SET is_used = TRUE WHERE user_id = ? AND is_used = FALSE";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error invalidating existing OTPs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Marks an OTP as used.
     *
     * @param otpId The OTP ID
     */
    private void markOTPAsUsed(int otpId) {
        String sql = "UPDATE otp_tokens SET is_used = TRUE WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, otpId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error marking OTP as used: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cleans up expired OTPs from the database.
     */
    public void cleanupExpiredOTPs() {
        String sql = "DELETE FROM otp_tokens WHERE expires_at < NOW()";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int deletedCount = stmt.executeUpdate();
            if (deletedCount > 0) {
                System.out.println("Cleaned up " + deletedCount + " expired OTPs");
            }

        } catch (SQLException e) {
            System.err.println("Error cleaning up expired OTPs: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
