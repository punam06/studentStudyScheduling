package org.example.auth;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Service for managing user authentication and sessions.
 */
public class AuthenticationService {
    private Map<String, UserAccount> userAccounts;
    private UserAccount currentUser;
    private static final String USER_DATA_FILE = "src/main/resources/users.json";

    /**
     * Creates a new authentication service and loads any existing user accounts.
     */
    public AuthenticationService() {
        this.userAccounts = new HashMap<>();
        this.currentUser = null;

        // Load existing users from JSON file
        loadUsersFromFile();

        // If no users were loaded, add default ones
        if (userAccounts.isEmpty()) {
            System.out.println("No existing users found. Creating default accounts.");
            // Add default admin account
            registerUser("admin", "admin123", UserAccount.UserRole.ADMIN);

            // Add default student account
            registerUser("student", "student123", UserAccount.UserRole.STUDENT);

            // Save the default users
            saveUsersToFile();
        } else {
            System.out.println("Loaded " + userAccounts.size() + " user accounts from file.");
        }
    }

    /**
     * Loads user accounts from JSON file.
     */
    private void loadUsersFromFile() {
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(USER_DATA_FILE)) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            JSONArray users = (JSONArray) jsonObject.get("users");

            if (users != null) {
                for (Object userObj : users) {
                    JSONObject userJson = (JSONObject) userObj;
                    String username = (String) userJson.get("username");
                    String passwordHash = (String) userJson.get("passwordHash");
                    String salt = (String) userJson.get("salt");
                    String roleStr = (String) userJson.get("role");

                    UserAccount.UserRole role;
                    try {
                        role = UserAccount.UserRole.valueOf(roleStr);
                    } catch (IllegalArgumentException e) {
                        // Default to USER if role is invalid
                        role = UserAccount.UserRole.USER;
                    }

                    UserAccount account = new UserAccount(username, passwordHash, salt, role, true);
                    userAccounts.put(username, account);
                }
            }
        } catch (IOException | ParseException e) {
            // File might not exist yet or has invalid format
            System.out.println("Could not load users from file: " + e.getMessage());
        }
    }

    /**
     * Saves user accounts to JSON file.
     */
    private void saveUsersToFile() {
        JSONObject rootObject = new JSONObject();
        JSONArray users = new JSONArray();

        for (UserAccount account : userAccounts.values()) {
            JSONObject userJson = new JSONObject();
            userJson.put("username", account.getUsername());
            userJson.put("passwordHash", account.getPasswordHash());
            userJson.put("salt", account.getSalt());
            userJson.put("role", account.getRole().toString());
            users.add(userJson);
        }

        rootObject.put("users", users);

        try (Writer writer = new FileWriter(USER_DATA_FILE)) {
            writer.write(rootObject.toJSONString());
        } catch (IOException e) {
            System.out.println("Could not save users to file: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Failed to save user data: " + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Registers a new user account.
     *
     * @param username The username for the new account
     * @param password The password for the new account
     * @param role The role for the new account
     * @return true if registration was successful, false if the username already exists
     */
    public boolean registerUser(String username, String password, UserAccount.UserRole role) {
        if (userAccounts.containsKey(username)) {
            return false;
        }

        UserAccount account = new UserAccount(username, password, role);
        userAccounts.put(username, account);

        // Save updated user list to file
        saveUsersToFile();

        return true;
    }

    /**
     * Registers a new student account.
     *
     * @param username The username for the new student account
     * @param password The password for the new student account
     * @return true if registration was successful, false if the username already exists
     */
    public boolean registerStudent(String username, String password) {
        return registerUser(username, password, UserAccount.UserRole.STUDENT);
    }

    /**
     * Attempts to log in a user with the provided credentials.
     *
     * @param username The username to log in with
     * @param password The password to log in with
     * @return true if login was successful, false otherwise
     */
    public boolean login(String username, String password) {
        UserAccount account = userAccounts.get(username);

        if (account != null && account.verifyPassword(password)) {
            currentUser = account;
            return true;
        }

        return false;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Gets the current logged-in user.
     *
     * @return An Optional containing the current user, or empty if no user is logged in
     */
    public Optional<UserAccount> getCurrentUser() {
        return Optional.ofNullable(currentUser);
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
     * Checks if the current user is an admin.
     *
     * @return true if the current user is an admin, false otherwise
     */
    public boolean isCurrentUserAdmin() {
        return currentUser != null && currentUser.getRole() == UserAccount.UserRole.ADMIN;
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
     * Gets all registered user accounts.
     *
     * @return A map of username to user account
     */
    public Map<String, UserAccount> getAllUsers() {
        return new HashMap<>(userAccounts);
    }

    /**
     * Removes a user account.
     *
     * @param username The username of the account to remove
     * @return true if the account was removed, false if it didn't exist
     */
    public boolean removeUser(String username) {
        boolean removed = userAccounts.remove(username) != null;
        if (removed) {
            saveUsersToFile();
        }
        return removed;
    }
}
