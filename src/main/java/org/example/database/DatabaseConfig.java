package org.example.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database configuration and connection management using HikariCP connection pool.
 */
public class DatabaseConfig {
    private static HikariDataSource dataSource;

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_scheduling";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = ""; // Change this to your MySQL password

    static {
        try {
            // First try to create database if it doesn't exist
            createDatabaseIfNotExists();

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DB_URL);
            config.setUsername(DB_USERNAME);
            config.setPassword(DB_PASSWORD);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);

            dataSource = new HikariDataSource(config);

            // Initialize database tables
            initializeTables();

        } catch (Exception e) {
            System.err.println("Database initialization failed. Running in fallback mode.");
            System.err.println("Error: " + e.getMessage());
            // Don't throw exception - allow app to run without database
            dataSource = null;
        }
    }

    /**
     * Creates the database if it doesn't exist.
     */
    private static void createDatabaseIfNotExists() {
        String createDbUrl = "jdbc:mysql://localhost:3306/";
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(createDbUrl);
            config.setUsername(DB_USERNAME);
            config.setPassword(DB_PASSWORD);

            HikariDataSource tempDataSource = new HikariDataSource(config);
            try (Connection conn = tempDataSource.getConnection();
                 Statement stmt = conn.createStatement()) {

                stmt.execute("CREATE DATABASE IF NOT EXISTS student_scheduling");
                System.out.println("Database 'student_scheduling' created or already exists");

            } finally {
                tempDataSource.close();
            }
        } catch (Exception e) {
            System.err.println("Could not create database: " + e.getMessage());
            throw new RuntimeException("Database setup failed", e);
        }
    }

    /**
     * Gets a database connection from the connection pool.
     *
     * @return Database connection
     * @throws SQLException if connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Initializes the database tables if they don't exist.
     */
    private static void initializeTables() {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                salt VARCHAR(255) NOT NULL,
                role ENUM('ADMIN', 'USER', 'STUDENT') NOT NULL DEFAULT 'USER',
                is_email_verified BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
            """;

        String createOtpTable = """
            CREATE TABLE IF NOT EXISTS otp_tokens (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                otp_code VARCHAR(6) NOT NULL,
                expires_at TIMESTAMP NOT NULL,
                is_used BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
            """;

        String createStudyGroupsTable = """
            CREATE TABLE IF NOT EXISTS study_groups (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                description TEXT,
                created_by INT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
            )
            """;

        String createSchedulesTable = """
            CREATE TABLE IF NOT EXISTS schedules (
                id INT AUTO_INCREMENT PRIMARY KEY,
                study_group_id INT NOT NULL,
                title VARCHAR(200) NOT NULL,
                description TEXT,
                start_time DATETIME NOT NULL,
                end_time DATETIME NOT NULL,
                created_by INT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (study_group_id) REFERENCES study_groups(id) ON DELETE CASCADE,
                FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
            )
            """;

        String createMembersTable = """
            CREATE TABLE IF NOT EXISTS members (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                study_group_id INT NOT NULL,
                role ENUM('ADMIN', 'MEMBER') DEFAULT 'MEMBER',
                joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (study_group_id) REFERENCES study_groups(id) ON DELETE CASCADE,
                UNIQUE KEY unique_membership (user_id, study_group_id)
            )
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createOtpTable);
            stmt.execute(createStudyGroupsTable);
            stmt.execute(createSchedulesTable);
            stmt.execute(createMembersTable);

            System.out.println("Database tables initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Error initializing database tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Closes the data source and all connections.
     */
    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
