package org.example.database;

import org.example.auth.UserAccount;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User operations with MySQL database.
 */
public class UserDAO {

    /**
     * Creates a new user in the database.
     *
     * @param username The username
     * @param email The email address
     * @param password The plain text password
     * @param role The user role
     * @return The created user account, or null if creation failed
     */
    public UserAccount createUser(String username, String email, String password, UserAccount.UserRole role) {
        String sql = "INSERT INTO users (username, email, password_hash, salt, role) VALUES (?, ?, ?, ?, ?)";

        String salt = BCrypt.gensalt();
        String passwordHash = BCrypt.hashpw(password, salt);

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, passwordHash);
            stmt.setString(4, salt);
            stmt.setString(5, role.name());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return new UserAccount(username, email, passwordHash, salt, role, generatedKeys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Finds a user by username.
     *
     * @param username The username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<UserAccount> findByUsername(String username) {
        String sql = "SELECT id, username, email, password_hash, salt, role, is_email_verified FROM users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Finds a user by email.
     *
     * @param email The email to search for
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<UserAccount> findByEmail(String email) {
        String sql = "SELECT id, username, email, password_hash, salt, role, is_email_verified FROM users WHERE email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding user by email: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Finds a user by ID.
     *
     * @param id The user ID
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<UserAccount> findById(int id) {
        String sql = "SELECT id, username, email, password_hash, salt, role, is_email_verified FROM users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Updates the email verification status for a user.
     *
     * @param userId The user ID
     * @param isVerified The verification status
     * @return true if update was successful, false otherwise
     */
    public boolean updateEmailVerificationStatus(int userId, boolean isVerified) {
        String sql = "UPDATE users SET is_email_verified = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, isVerified);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating email verification status: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Gets all users from the database.
     *
     * @return List of all users
     */
    public List<UserAccount> getAllUsers() {
        String sql = "SELECT id, username, email, password_hash, salt, role, is_email_verified FROM users";
        List<UserAccount> users = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Checks if a username already exists.
     *
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if an email already exists.
     *
     * @param email The email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Maps a ResultSet row to a UserAccount object.
     *
     * @param rs The ResultSet
     * @return UserAccount object
     * @throws SQLException if there's an error reading from the ResultSet
     */
    private UserAccount mapResultSetToUser(ResultSet rs) throws SQLException {
        return new UserAccount(
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("password_hash"),
            rs.getString("salt"),
            UserAccount.UserRole.valueOf(rs.getString("role")),
            rs.getInt("id")
        );
    }
}
