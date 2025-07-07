package org.example.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.time.LocalDateTime;
import org.example.util.EmailService;

/**
 * A simplified OTP service that works without requiring a database connection.
 * Uses in-memory storage for OTP codes.
 */
public class SimpleOTPService {
    // Store OTP info: user email -> [OTP code, expiry time]
    private final Map<String, OTPEntry> otpStorage = new HashMap<>();
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;

    private final EmailService emailService;

    public SimpleOTPService() {
        this.emailService = new EmailService();
    }

    /**
     * Generate a new OTP for the given email and send it via email
     * @param email The user's email address
     * @return The generated OTP code
     */
    public String generateOTP(String email) {
        // Generate random 6-digit OTP
        Random random = new Random();
        StringBuilder otpBuilder = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            otpBuilder.append(random.nextInt(10));
        }

        String otp = otpBuilder.toString();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        // Store the OTP
        otpStorage.put(email, new OTPEntry(otp, expiryTime, false));

        // Send the OTP via email
        boolean emailSent = emailService.sendOTP(email, otp);

        if (emailSent) {
            System.out.println("OTP email sent to: " + email);
        } else {
            System.out.println("WARNING: Failed to send OTP email to: " + email);
        }

        return otp;
    }

    /**
     * Verify the provided OTP against the stored one
     * @param email The user's email
     * @param otpToVerify The OTP to verify
     * @return True if OTP is valid and not expired, false otherwise
     */
    public boolean verifyOTP(String email, String otpToVerify) {
        OTPEntry entry = otpStorage.get(email);

        if (entry == null) {
            System.out.println("No OTP found for email: " + email);
            return false;
        }

        if (entry.isUsed()) {
            System.out.println("OTP already used for email: " + email);
            return false;
        }

        if (LocalDateTime.now().isAfter(entry.getExpiryTime())) {
            System.out.println("OTP expired for email: " + email);
            return false;
        }

        if (entry.getOtpCode().equals(otpToVerify)) {
            // Mark as used
            entry.setUsed(true);
            System.out.println("OTP verified successfully for email: " + email);
            return true;
        }

        System.out.println("Invalid OTP provided for email: " + email);
        return false;
    }

    /**
     * Inner class to hold OTP information
     */
    private static class OTPEntry {
        private final String otpCode;
        private final LocalDateTime expiryTime;
        private boolean used;

        public OTPEntry(String otpCode, LocalDateTime expiryTime, boolean used) {
            this.otpCode = otpCode;
            this.expiryTime = expiryTime;
            this.used = used;
        }

        public String getOtpCode() {
            return otpCode;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }

        public boolean isUsed() {
            return used;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }
    }
}
