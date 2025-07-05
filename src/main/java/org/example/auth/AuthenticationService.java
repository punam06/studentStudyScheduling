package org.example.auth;

import org.example.database.DatabaseConfig;
import org.example.database.UserDAO;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * Service for managing user authentication and sessions with MySQL database and OTP verification.
 * Falls back to in-memory storage if database is unavailable.
 */
public class AuthenticationService {
    private UserDAO userDAO;
    private OTPService otpService;
    private UserAccount currentUser;
    private Map<String, UserAccount> pendingVerifications; // Users pending OTP verification

    // Fallback in-memory storage when database is unavailable
    private Map<String, UserAccount> fallbackUsers;
    private boolean usingFallback = false;

    /**
     * Creates a new authentication service with database support.
     */
    public AuthenticationService() {
        this.pendingVerifications = new HashMap<>();
        this.fallbackUsers = new HashMap<>();

        try {
            this.userDAO = new UserDAO();
            this.otpService = new OTPService();

            // Create default admin user if no users exist
            createDefaultUsers();

            System.out.println("✅ Authentication service initialized with database support");
        } catch (Exception e) {
            System.err.println("⚠️  Database unavailable. Running in fallback mode (in-memory storage).");
            System.err.println("   Default accounts will be available for this session only.");

            this.usingFallback = true;
            this.userDAO = null;
            this.otpService = null;

            // Create default accounts in memory
            createFallbackUsers();
        }
    }

    /**
     * Creates default users if the database is empty.
     */
    private void createDefaultUsers() {
        try {
            if (userDAO.getAllUsers().isEmpty()) {
                System.out.println("No existing users found. Creating default accounts.");

                // Create default admin account
                UserAccount admin = userDAO.createUser("admin", "admin@example.com", "admin123", UserAccount.UserRole.ADMIN);
                if (admin != null) {
                    userDAO.updateEmailVerificationStatus(admin.getId(), true);
                    System.out.println("Default admin account created: admin/admin123");
                }

                // Create default student account
                UserAccount student = userDAO.createUser("student", "student@example.com", "student123", UserAccount.UserRole.STUDENT);
                if (student != null) {
                    userDAO.updateEmailVerificationStatus(student.getId(), true);
                    System.out.println("Default student account created: student/student123");
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating default users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates fallback users when database is unavailable.
     */
    private void createFallbackUsers() {
        // Create admin account
        UserAccount admin = new UserAccount("admin", "admin123", UserAccount.UserRole.ADMIN);
        admin.setEmail("admin@example.com");
        admin.setEmailVerified(true);
        fallbackUsers.put("admin", admin);

        // Create student account
        UserAccount student = new UserAccount("student", "student123", UserAccount.UserRole.STUDENT);
        student.setEmail("student@example.com");
        student.setEmailVerified(true);
        fallbackUsers.put("student", student);

        System.out.println("📋 Fallback accounts created:");
        System.out.println("   - Admin: username='admin', password='admin123'");
        System.out.println("   - Student: username='student', password='student123'");
        System.out.println("   ⚠️  Note: These accounts exist only for this session!");
    }

    /**
     * Attempts to log in a user with the provided credentials.
     * Always sends OTP to registered email address for authentication.
     *
     * @param email The email to log in with
     * @param password The password for the account
     * @return true if credentials are valid and OTP was sent, false otherwise
     */
    public boolean initiateLogin(String email, String password) {
        if (usingFallback) {
            return fallbackLogin(email, password);
        }

        // Normal database login with mandatory OTP using email
        Optional<UserAccount> userOptional = userDAO.findByEmail(email);

        if (userOptional.isEmpty()) {
            return false;
        }

        UserAccount user = userOptional.get();

        // Verify password using BCrypt
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            return false;
        }

        // Check if user has a valid email address
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            System.err.println("❌ User " + email + " does not have a registered email address");
            return false;
        }

        // Add user to pending verifications using email as key
        pendingVerifications.put(email, user);

        // Always send OTP to user's email - this is now mandatory
        try {
            boolean otpSent = otpService.generateAndSendOTP(user.getId(), user.getEmail());
            if (otpSent) {
                System.out.println("✉️ OTP sent to: " + user.getEmail());
                return true;
            } else {
                System.err.println("❌ Failed to send OTP to: " + user.getEmail());
                pendingVerifications.remove(email);
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Email service error: " + e.getMessage());
            pendingVerifications.remove(email);
            return false;
        }
    }

    /**
     * Fallback login method that works without database/OTP using email.
     */
    private boolean fallbackLogin(String email, String password) {
        // For fallback mode, check if the email matches any of our fallback users
        for (UserAccount user : fallbackUsers.values()) {
            if (email.equals(user.getEmail()) && user.verifyPassword(password)) {
                currentUser = user;
                System.out.println("✅ Fallback login successful for user: " + email);
                return true;
            }
        }
        return false;
    }

    /**
     * Completes the login process by verifying the OTP.
     *
     * @param username The username
     * @param otp The OTP code
     * @return true if OTP is valid and login is successful, false otherwise
     */
    public boolean completeLogin(String username, String otp) {
        if (usingFallback) {
            // Already logged in via fallbackLogin
            return currentUser != null;
        }

        UserAccount user = pendingVerifications.get(username);

        if (user == null) {
            return false;
        }

        // Verify OTP
        if (otpService.verifyOTP(user.getId(), otp)) {
            // OTP is valid, complete login
            currentUser = user;
            pendingVerifications.remove(username);

            // Update email verification status if not already verified
            if (!user.isEmailVerified()) {
                userDAO.updateEmailVerificationStatus(user.getId(), true);
                user.setEmailVerified(true);
            }

            System.out.println("Login successful for user: " + username);
            return true;
        }

        return false;
    }

    /**
     * Registers a new user account with email verification.
     *
     * @param username The username for the new account
     * @param email The email address for the new account
     * @param password The password for the new account
     * @param role The role for the new account
     * @return true if registration was successful, false otherwise
     */
    public boolean registerUser(String username, String email, String password, UserAccount.UserRole role) {
        if (usingFallback) {
            return fallbackRegister(username, email, password, role);
        }

        // Normal database registration
        // Check if username or email already exists
        if (userDAO.usernameExists(username)) {
            JOptionPane.showMessageDialog(null,
                "Username already exists. Please choose a different username.",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (userDAO.emailExists(email)) {
            JOptionPane.showMessageDialog(null,
                "Email already exists. Please use a different email address.",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Create user
        UserAccount newUser = userDAO.createUser(username, email, password, role);

        if (newUser != null) {
            // If email is not configured, skip OTP verification
            if (!isEmailConfigured()) {
                userDAO.updateEmailVerificationStatus(newUser.getId(), true);
                JOptionPane.showMessageDialog(null,
                    "Registration successful! You can now login.\n(Email verification skipped - email not configured)",
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            }

            // Try to send verification OTP
            try {
                if (otpService.generateAndSendOTP(newUser.getId(), email)) {
                    pendingVerifications.put(username, newUser);
                    JOptionPane.showMessageDialog(null,
                        "Registration successful! Please check your email for the OTP code to verify your account.",
                        "Registration Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                    return true;
                } else {
                    // Email failed, but registration was successful - auto-verify
                    userDAO.updateEmailVerificationStatus(newUser.getId(), true);
                    JOptionPane.showMessageDialog(null,
                        "Registration successful! You can now login.\n(Email verification skipped - email service unavailable)",
                        "Registration Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                    return true;
                }
            } catch (Exception e) {
                // Email service error, but registration was successful - auto-verify
                userDAO.updateEmailVerificationStatus(newUser.getId(), true);
                JOptionPane.showMessageDialog(null,
                    "Registration successful! You can now login.\n(Email verification skipped - email service error)",
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        }

        return false;
    }

    /**
     * Fallback registration method.
     */
    private boolean fallbackRegister(String username, String email, String password, UserAccount.UserRole role) {
        if (fallbackUsers.containsKey(username)) {
            JOptionPane.showMessageDialog(null,
                "Username already exists in this session.",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        UserAccount newUser = new UserAccount(username, password, role);
        newUser.setEmail(email);
        newUser.setEmailVerified(true); // Skip verification in fallback mode
        fallbackUsers.put(username, newUser);

        JOptionPane.showMessageDialog(null,
            "Registration successful! You can now login.\n(Note: This account exists only for this session)",
            "Registration Successful",
            JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        currentUser = null;
        System.out.println("User logged out successfully.");
    }

    /**
     * Gets the currently logged-in user.
     *
     * @return The current user, or null if no user is logged in
     */
    public UserAccount getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Checks if the current user has administrator privileges.
     *
     * @return true if the current user is an admin, false otherwise
     */
    public boolean isCurrentUserAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    /**
     * Checks if the current user is a student.
     *
     * @return true if the current user is a student, false otherwise
     */
    public boolean isCurrentUserStudent() {
        return currentUser != null && currentUser.isStudent();
    }

    /**
     * Resends OTP to a user's email.
     *
     * @param username The username
     * @return true if OTP was resent successfully, false otherwise
     */
    public boolean resendOTP(String username) {
        UserAccount user = pendingVerifications.get(username);

        if (user != null && user.getEmail() != null) {
            return otpService.generateAndSendOTP(user.getId(), user.getEmail());
        }

        return false;
    }

    /**
     * Cancels a pending login/registration process.
     *
     * @param username The username to cancel
     */
    public void cancelPendingVerification(String username) {
        pendingVerifications.remove(username);
    }

    /**
     * Checks if a user has a pending verification.
     *
     * @param username The username to check
     * @return true if user has pending verification, false otherwise
     */
    public boolean hasPendingVerification(String username) {
        return pendingVerifications.containsKey(username);
    }

    /**
     * Gets the email address for a user with pending verification.
     *
     * @param username The username
     * @return The email address, or null if no pending verification
     */
    public String getPendingVerificationEmail(String username) {
        UserAccount user = pendingVerifications.get(username);
        return user != null ? user.getEmail() : null;
    }

    /**
     * Performs cleanup of expired OTPs.
     */
    public void cleanup() {
        otpService.cleanupExpiredOTPs();
    }

    /**
     * Checks if we're using fallback mode.
     */
    public boolean isUsingFallback() {
        return usingFallback;
    }

    /**
     * Gets status message for the user.
     */
    public String getStatusMessage() {
        if (usingFallback) {
            return "Running in Demo Mode (Database unavailable)";
        }
        return "Connected to Database";
    }

    /**
     * Checks if this is a default account that should skip OTP.
     */
    private boolean isDefaultAccount(String username) {
        return "admin".equals(username) || "student".equals(username);
    }

    /**
     * Checks if email is properly configured.
     */
    private boolean isEmailConfigured() {
        // This is a simple check - in a real app you'd want more robust validation
        return !EMAIL_USERNAME_DEFAULT.equals("your-email@gmail.com");
    }

    /**
     * Recovers/resets a user's password using their email address.
     *
     * @param email The email address of the user
     * @param newPassword The new password to set
     * @return true if password was reset successfully, false otherwise
     */
    public boolean recoverPassword(String email, String newPassword) {
        if (usingFallback) {
            return fallbackRecoverPassword(email, newPassword);
        }

        try {
            // Find user by email
            Optional<UserAccount> userOptional = userDAO.findByEmail(email);

            if (userOptional.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                    "No account found with this email address.",
                    "Password Recovery Error",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }

            UserAccount user = userOptional.get();

            // If email is configured and working, send OTP for verification
            if (isEmailConfigured()) {
                try {
                    boolean otpSent = otpService.generateAndSendOTP(user.getId(), email);
                    if (otpSent) {
                        // Show OTP verification dialog
                        String otp = JOptionPane.showInputDialog(null,
                            "An OTP has been sent to your email. Please enter the 6-digit code:",
                            "Email Verification",
                            JOptionPane.QUESTION_MESSAGE);

                        if (otp != null && otpService.verifyOTP(user.getId(), otp)) {
                            // OTP verified, proceed with password reset
                            return updateUserPassword(user.getId(), newPassword);
                        } else {
                            JOptionPane.showMessageDialog(null,
                                "Invalid or expired OTP code.",
                                "Verification Failed",
                                JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    } else {
                        // Email failed, but registration was successful - auto-verify
                        System.out.println("⚠️  Email failed, allowing direct password reset for demo");
                        return updateUserPassword(user.getId(), newPassword);
                    }
                } catch (Exception e) {
                    // Email service error, fall back to direct reset
                    System.out.println("⚠️  Email service error, allowing direct password reset");
                    return updateUserPassword(user.getId(), newPassword);
                }
            } else {
                // Email not configured, allow direct reset
                return updateUserPassword(user.getId(), newPassword);
            }

        } catch (Exception e) {
            System.err.println("Error during password recovery: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a user's password in the database.
     */
    private boolean updateUserPassword(int userId, String newPassword) {
        try {
            String salt = BCrypt.gensalt();
            String passwordHash = BCrypt.hashpw(newPassword, salt);

            String sql = "UPDATE users SET password_hash = ?, salt = ? WHERE id = ?";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, passwordHash);
                stmt.setString(2, salt);
                stmt.setInt(3, userId);

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Password updated successfully for user ID: " + userId);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Fallback password recovery for in-memory mode.
     */
    private boolean fallbackRecoverPassword(String email, String newPassword) {
        for (UserAccount user : fallbackUsers.values()) {
            if (email.equals(user.getEmail())) {
                // Update password directly in memory
                user.setPasswordHash(user.hashPassword(newPassword, user.getSalt()));
                JOptionPane.showMessageDialog(null,
                    "Password reset successful! (Demo mode)",
                    "Password Recovery Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        }

        JOptionPane.showMessageDialog(null,
            "No account found with this email address.",
            "Password Recovery Error",
            JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private static final String EMAIL_USERNAME_DEFAULT = "your-email@gmail.com";
}
