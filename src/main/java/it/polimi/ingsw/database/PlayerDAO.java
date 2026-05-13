package it.polimi.ingsw.database;

import java.sql.*;

/**
 * Data Access Object for player management.
 * Handles persistence and retrieval for the 'players' table.
 */
public class PlayerDAO {

    //region SQL
    // --- Table Names ---
    private static final String TABLE_PLAYERS = "players";

    // --- Column Names ---
    private static final String COL_ID = "id";
    private static final String COL_NICKNAME = "nickname";

    // --- SQL Queries ---
    private static final String SELECT_PLAYER_BY_NICKNAME =
            "SELECT " + COL_ID + " FROM " + TABLE_PLAYERS + " WHERE " + COL_NICKNAME + " = ?";

    private static final String INSERT_PLAYER =
            "INSERT INTO " + TABLE_PLAYERS + " (" + COL_NICKNAME + ") VALUES (?)";

    private static final String EXISTS_PLAYER =
            "SELECT 1 FROM " + TABLE_PLAYERS + " WHERE " + COL_NICKNAME + " = ?";
    //endregion

    /**
     * Saves a new player to the database if the nickname doesn't already exist.
     *
     * @param nickname The unique nickname of the player.
     * @return The ID of the player (either newly created or existing).
     * @throws SQLException If a database access error occurs.
     */
    public static int saveOrGetPlayer(String nickname) throws SQLException {
        if (!DatabaseManager.isAvailable()) {
            throw new SQLException("Database service is not available.");
        }

        try (Connection conn = DatabaseManager.getConnection()) {

            // 1. Try to find if the player already exists
            try (PreparedStatement selectStmt = conn.prepareStatement(SELECT_PLAYER_BY_NICKNAME)) {
                selectStmt.setString(1, nickname);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(COL_ID); // Player found
                    }
                }
            }

            // 2. If player does not exist, insert them
            try (PreparedStatement insertStmt = conn.prepareStatement(INSERT_PLAYER, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, nickname);
                insertStmt.executeUpdate();

                // Retrieve the auto-generated ID
                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating player failed, no ID obtained.");
                    }
                }
            }
        }
    }

    /**
     * Checks if a player with the given nickname exists in the database.
     *
     * @param nickname The nickname to check.
     * @return true if the player exists, false otherwise.
     */
    public static boolean playerExists(String nickname) {
        if (!DatabaseManager.isAvailable()) return false;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_PLAYER)) {

            stmt.setString(1, nickname);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error checking player existence: " + e.getMessage());
            return false;
        }
    }
}