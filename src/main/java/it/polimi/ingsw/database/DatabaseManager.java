package it.polimi.ingsw.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Manages the connection and initialization of the MySQL database.
 * This class follows a static utility pattern to provide database access
 * across the server application.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/GC35_Mesos_DB";
    private static String user;
    private static String pass;
    private static boolean available = false;

    /**
     * Initializes the database connection with provided credentials.
     * If the connection is successful, it proceeds to verify and create
     * the necessary database schema.
     *
     * @param username The database username provided via command line.
     * @param password The database password provided via command line.
     */
    public static void init(String username, String password) {
        user = username;
        pass = password;

        String baseUrl = "jdbc:mysql://localhost:3306/?serverTimezone=UTC";

        try {
            try (Connection conn = DriverManager.getConnection(baseUrl, user, pass);
                 Statement stmt = conn.createStatement()) {

                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS GC35_Mesos_DB");
                System.out.println("[DB] Database 'GC35_Mesos_DB' checked/created.");
            }

            try (Connection conn = getConnection()) {
                available = true;
                checkAndCreateTables(conn);
                System.out.println("[DB] Connection and schema are ready.");
            }

        } catch (SQLException e) {
            System.err.println("[DB] Critical Error: " + e.getMessage());
            available = false;
        }
    }

    /**
     * Provides a new connection to the database.
     *
     * @return A {@link Connection} object.
     * @throws SQLException If a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, user, pass);
    }

    /**
     * Checks if the database service is currently available.
     *
     * @return true if the database is connected and initialized, false otherwise.
     */
    public static boolean isAvailable() {
        return available;
    }

    /**
     * Verifies the existence of the required tables and executes the
     * initialization script if they are missing.
     *
     * @param conn The active database connection.
     */
    private static void checkAndCreateTables(Connection conn) {
        try {
            // Check if 'matches' table exists (adjust 'matches' to your actual table name)
            ResultSet rs = conn.getMetaData().getTables(null, null, "matches", null);
            if (!rs.next()) {
                System.out.println("[DB] Schema not found. Running initialization script...");
                executeSqlScript(conn);
                System.out.println("[DB] Schema created successfully.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error during table verification: " + e.getMessage());
        }
    }

    /**
     * Reads and executes a SQL script file from the resources' folder.
     *
     * @param conn The active database connection.
     */
    private static void executeSqlScript(Connection conn) {
        try (InputStream is = DatabaseManager.class.getResourceAsStream("/database/schema.sql")) {
            if (is == null) {
                System.err.println("[DB] Error: SQL script not found at " + "/schema.sql");
                return;
            }

            String script = new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));

            try (Statement stmt = conn.createStatement()) {
                // Split by semicolon to execute multiple statements
                for (String sql : script.split(";")) {
                    if (!sql.trim().isEmpty()) {
                        stmt.execute(sql);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[DB] Failed to execute initialization script: " + e.getMessage());
        }
    }
}