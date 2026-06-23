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
     * If the database or its tables do not exist, they are created automatically.
     *
     * @param username The database username provided via command line.
     * @param password The database password provided via command line.
     */
    public static void init(String username, String password, String port) {
        user = username;
        pass = password;

        String baseUrl = "jdbc:mysql://localhost:" + port + "/?serverTimezone=UTC";

        try (Connection conn = DriverManager.getConnection(baseUrl, user, pass);
             Statement stmt = conn.createStatement()) {

            boolean dbExisted = false;
            try (ResultSet rs = conn.getMetaData().getCatalogs()) {
                while (rs.next()) {
                    if ("GC35_Mesos_DB".equalsIgnoreCase(rs.getString(1))) {
                        dbExisted = true;
                        break;
                    }
                }
            }

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS GC35_Mesos_DB");

            try (Connection dbConn = getConnection()) {
                available = true;

                if (!dbExisted || !doesTableExist(dbConn, "matches")) {
                    System.out.println("[DB] Schema missing or fresh DB. Running initialization script...");
                    executeSqlScript(dbConn);
                    System.out.println("[DB] Schema created successfully.");
                } else {
                    System.out.println("[DB] Database and schema are already up to date.");
                }
            }

        } catch (SQLException e) {
            System.err.println("[DB] Critical Error during initialization: " + e.getMessage());
            System.err.println("Ranking functionality disabled.");
            available = false;
        }
    }

    /**
     * Helper method to safely check if a table exists by running a lightweight query.
     */
    private static boolean doesTableExist(Connection conn, String tableName) {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeQuery("SELECT 1 FROM " + tableName + " LIMIT 1");
            return true;
        } catch (SQLException e) {
            return false;
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
     * Reads and executes a SQL script file from the resources' folder.
     *
     * @param conn The active database connection.
     */
    private static void executeSqlScript(Connection conn) {
        try (InputStream is = DatabaseManager.class.getResourceAsStream("/database/schema.sql")) {
            if (is == null) {
                System.err.println("[DB] Error: SQL script not found at " + "/schema.sql");
                System.err.println("Ranking functionality disabled.");
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
            System.err.println("Ranking functionality disabled.");
        }
    }
}