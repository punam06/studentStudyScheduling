package org.example.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Simple file-based authentication service that doesn't require a database.
 */
public class SimpleAuthService {
    private static final String USERS_FILE = "users.json";
    private final ObjectMapper objectMapper;
    private final File usersFile;
    private List<UserAccount> users;
    private UserAccount currentUser;

    // OTP service for handling one-time passwords
    private final SimpleOTPService otpService;

    // Map to store email addresses for users awaiting OTP verification
    private final Map<String, String> pendingVerifications = new HashMap<>();

    public SimpleAuthService() {
        this.objectMapper = new ObjectMapper();
        this.otpService = new SimpleOTPService();

        // Look for users.json in multiple possible locations
        String userDir = System.getProperty("user.dir");
        File rootFile = new File(USERS_FILE);
        File resourcesFile = new File("src/main/resources/" + USERS_FILE);

        if (rootFile.exists()) {
            this.usersFile = rootFile;
        } else if (resourcesFile.exists()) {
            this.usersFile = resourcesFile;
        } else {
            // Create in root if doesn't exist anywhere
            this.usersFile = rootFile;
        }

        this.users = new ArrayList<>();

        loadUsers();
        createDefaultUsers();
    }

    /**
     * Load users from the JSON file.
     */
    private void loadUsers() {
        try {
            if (usersFile.exists()) {
                CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, UserAccount.class);
                users = objectMapper.readValue(usersFile, listType);
                System.out.println("✅ Loaded " + users.size() + " users from file: " + usersFile.getAbsolutePath());

                // Debug: Print loaded users
                for (UserAccount user : users) {
                    System.out.println("   - Loaded user: " + user.getUsername() +
                                     ", email: " + user.getEmail() +
                                     ", role: " + user.getRole());
                }
            } else {
                System.out.println("⚠️ Users file not found at: " + usersFile.getAbsolutePath() + ", will create new file");
            }
        } catch (IOException e) {
            System.err.println("⚠️ Error loading users: " + e.getMessage());
            System.err.println("   Will create new users list");
            users = new ArrayList<>();
        }
    }

    /**
     * Save users to the JSON file.
     */
    private void saveUsers() {
        try {
            // Ensure parent directories exist
            if (!usersFile.getParentFile().exists()) {
                usersFile.getParentFile().mkdirs();
            }

            objectMapper.writeValue(usersFile, users);
            System.out.println("✅ Saved " + users.size() + " users to file: " + usersFile.getAbsolutePath());

            // Verify the file was created
            if (usersFile.exists() && usersFile.length() > 0) {
                System.out.println("   File exists and is not empty: " + usersFile.length() + " bytes");
            } else {
                System.err.println("⚠️ File appears to be empty or not created!");
            }
        } catch (IOException e) {
            System.err.println("❌ Error saving users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create default users if none exist.
     */
    private void createDefaultUsers() {
        if (users.isEmpty()) {
            // Create default admin user with email
            UserAccount admin = new UserAccount("admin", "admin123", UserAccount.UserRole.ADMIN);
            admin.setEmail("admin@example.com");
            users.add(admin);

            // Create default student user with email
            UserAccount student = new UserAccount("student", "student123", UserAccount.UserRole.STUDENT);
            student.setEmail("student@example.com");
            users.add(student);

            saveUsers();

            System.out.println("✅ Created default users:");
            System.out.println("   Admin: email='admin@example.com', password='admin123'");
            System.out.println("   Student: email='student@example.com', password='student123'");
        }
    }

    /**
     * Simple password hashing (for demo purposes - in production use proper hashing).
     */
    private String hashPassword(String password) {
        // Simple hash for demo - in production, use BCrypt or similar
        return "hash_" + password.hashCode();
    }

    /**
     * Hash a password with the provided salt.
     * This is needed for debugging password verification.
     */
    private String hashPassword(String password, String salt) {
        try {
            // Use the same hashing algorithm as in UserAccount
            return new UserAccount("temp", password, UserAccount.UserRole.STUDENT).getPasswordHash();
        } catch (Exception e) {
            System.err.println("Error hashing password for debug: " + e.getMessage());
            return "";
        }
    }

    /**
     * Verify password against the UserAccount's stored hash.
     */
    private boolean verifyPassword(String password, UserAccount user) {
        // Use the user's own verifyPassword method which handles salt correctly
        return user.verifyPassword(password);
    }

    /**
     * First stage of the login process - check credentials and generate OTP if valid
     *
     * @param usernameOrEmail The username or email to check
     * @param password The password to verify
     * @return true if credentials are valid and OTP is generated, false otherwise
     */
    public boolean initiateLogin(String usernameOrEmail, String password) {
        try {
            System.out.println("DEBUG: Initiating login for: " + usernameOrEmail);
            System.out.println("DEBUG: Total users loaded: " + users.size());

            // Special case: hardcoded default credentials
            if ((usernameOrEmail.equals("admin@example.com") && password.equals("admin123")) ||
                (usernameOrEmail.equals("student@example.com") && password.equals("student123"))) {

                System.out.println("✅ Default credentials match found for: " + usernameOrEmail);

                // Find or create appropriate user account
                UserAccount user = null;

                for (UserAccount u : users) {
                    if ((u.getEmail() != null && u.getEmail().equals(usernameOrEmail)) ||
                        u.getUsername().equals(usernameOrEmail)) {
                        user = u;
                        break;
                    }
                }

                // If user not found in list, create a new one based on login
                if (user == null) {
                    if (usernameOrEmail.equals("admin@example.com")) {
                        user = new UserAccount("admin", "admin123", UserAccount.UserRole.ADMIN);
                        user.setEmail("admin@example.com");
                    } else {
                        user = new UserAccount("student", "student123", UserAccount.UserRole.STUDENT);
                        user.setEmail("student@example.com");
                    }
                    users.add(user);
                    saveUsers();
                }

                // Store email for OTP verification - Store with BOTH username AND email as keys
                // This ensures that looking up by either will work
                pendingVerifications.put(user.getUsername(), user.getEmail());
                pendingVerifications.put(user.getEmail(), user.getEmail());

                // Generate OTP
                String otp = otpService.generateOTP(user.getEmail());

                System.out.println("✅ Login initiated, OTP generated for: " + user.getEmail());
                return true;

            }

            // Standard user verification for custom accounts
            Optional<UserAccount> userOpt = users.stream()
                .filter(u -> u.getUsername().equals(usernameOrEmail) ||
                           (u.getEmail() != null && u.getEmail().equals(usernameOrEmail)))
                .findFirst();

            if (userOpt.isPresent()) {
                UserAccount user = userOpt.get();
                System.out.println("DEBUG: User found! Username: " + user.getUsername() +
                    ", email: " + user.getEmail());

                if (verifyPassword(password, user)) {
                    // Store email for OTP verification - Store with BOTH username AND email as keys
                    // This ensures that looking up by either will work
                    pendingVerifications.put(user.getUsername(), user.getEmail());
                    pendingVerifications.put(user.getEmail(), user.getEmail());

                    // Generate OTP
                    String otp = otpService.generateOTP(user.getEmail());

                    System.out.println("✅ Login initiated, OTP generated for: " + user.getEmail());
                    return true;
                } else {
                    System.out.println("❌ Invalid password for user: " + usernameOrEmail);
                }
            } else {
                System.out.println("❌ User not found: " + usernameOrEmail);
            }
        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Second stage of the login process - verify OTP
     *
     * @param username The username attempting to log in
     * @param otp The OTP to verify
     * @return true if OTP is valid, false otherwise
     */
    public boolean completeLogin(String username, String otp) {
        try {
            // Find the email that was stored during initiateLogin
            String email = pendingVerifications.get(username);

            if (email == null) {
                System.out.println("❌ No pending verification for user: " + username);
                return false;
            }

            // Verify OTP
            boolean otpValid = otpService.verifyOTP(email, otp);

            if (otpValid) {
                // Find the user account
                Optional<UserAccount> userOpt = users.stream()
                    .filter(u -> u.getUsername().equals(username) ||
                               (u.getEmail() != null && u.getEmail().equals(email)))
                    .findFirst();

                if (userOpt.isPresent()) {
                    // Complete login
                    currentUser = userOpt.get();
                    pendingVerifications.remove(username);
                    System.out.println("✅ Login successful for user: " + currentUser.getUsername());
                    return true;
                }
            } else {
                System.out.println("❌ Invalid OTP for user: " + username);
            }
        } catch (Exception e) {
            System.err.println("❌ OTP verification error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the email address for a pending verification
     *
     * @param username The username
     * @return The email address associated with the pending verification, or null if none
     */
    public String getPendingVerificationEmail(String username) {
        return pendingVerifications.get(username);
    }

    /**
     * Register a new user.
     */
    public boolean register(String username, String email, String password) {
        try {
            // Check if user already exists
            boolean userExists = users.stream()
                .anyMatch(u -> u.getUsername().equals(username) ||
                             (u.getEmail() != null && u.getEmail().equals(email)));

            if (userExists) {
                System.out.println("❌ User already exists: " + username);
                return false;
            }

            // Create new user with STUDENT role by default
            UserAccount newUser = new UserAccount(username, password, UserAccount.UserRole.STUDENT);
            newUser.setEmail(email); // Set the email address

            users.add(newUser);
            saveUsers();

            System.out.println("✅ User registered successfully: " + username);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Registration error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Logout the current user.
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("✅ User logged out: " + currentUser.getUsername());
            currentUser = null;
        }
    }

    /**
     * Check if a user is currently logged in.
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Get the current logged-in user.
     */
    public UserAccount getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if current user is admin.
     */
    public boolean isCurrentUserAdmin() {
        return currentUser != null && currentUser.getRole() == UserAccount.UserRole.ADMIN;
    }

    /**
     * Check if current user is student.
     */
    public boolean isCurrentUserStudent() {
        return currentUser != null && currentUser.getRole() == UserAccount.UserRole.STUDENT;
    }

    /**
     * Get all users (admin only).
     */
    public List<UserAccount> getAllUsers() {
        if (isCurrentUserAdmin()) {
            return new ArrayList<>(users);
        }
        return new ArrayList<>();
    }

    /**
     * Reset password for a user with the given email.
     *
     * @param email The email address of the user
     * @param newPassword The new password to set
     * @return true if the password was reset successfully, false otherwise
     */
    public boolean resetPassword(String email, String newPassword) {
        try {
            System.out.println("Attempting to reset password for: " + email);

            // Find user with this email
            Optional<UserAccount> userOpt = users.stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equals(email))
                .findFirst();

            if (userOpt.isPresent()) {
                UserAccount user = userOpt.get();
                System.out.println("User found, resetting password for: " + user.getUsername());

                // Create new salt and hash for the new password
                String newSalt = user.generateSalt();
                String newHash = user.hashPassword(newPassword, newSalt);

                // Update the user's password hash and salt
                user.setSalt(newSalt);
                user.setPasswordHash(newHash);

                // Save the updated user list
                saveUsers();

                System.out.println("✅ Password reset successful for user: " + user.getUsername());
                return true;
            } else {
                System.out.println("❌ User with email not found: " + email);
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Password reset error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
