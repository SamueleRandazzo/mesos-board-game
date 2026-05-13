package it.polimi.ingsw.database;

import it.polimi.ingsw.model.Player;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Match results.
 * Handles the Many-to-Many relationship between Players and Matches.
 * Uses constants for table and column names to ensure maintainability.
 */
public class MatchDAO {

    //region SQL
    // --- Table Names ---
    private static final String TABLE_PLAYERS = "players";
    private static final String TABLE_MATCHES = "matches";
    private static final String TABLE_PLAYER_MATCHES = "player_matches";

    // --- Column Names ---
    private static final String COL_PLAYER_ID = "player_id";
    private static final String COL_MATCH_ID = "match_id";
    private static final String COL_NICKNAME = "nickname";
    private static final String COL_SCORE = "score";
    private static final String COL_PLAYER_COUNT = "player_count";
    private static final String COL_MATCH_DATE = "match_date";

    // --- SQL Queries ---
    private static final String INSERT_MATCH =
            "INSERT INTO " + TABLE_MATCHES + " (" + COL_PLAYER_COUNT + ") VALUES (?)";

    private static final String INSERT_PLAYER_MATCH =
            "INSERT INTO " + TABLE_PLAYER_MATCHES + " (" + COL_PLAYER_ID + ", " + COL_MATCH_ID + ", " + COL_SCORE + ") VALUES (?, ?, ?)";

    private static final String SELECT_LEADERBOARD =
            "SELECT p." + COL_NICKNAME + ", pm." + COL_SCORE + ", m." + COL_MATCH_DATE + " " +
                    "FROM " + TABLE_PLAYER_MATCHES + " pm " +
                    "JOIN " + TABLE_PLAYERS + " p ON pm." + COL_PLAYER_ID + " = p.id " +
                    "JOIN " + TABLE_MATCHES + " m ON pm." + COL_MATCH_ID + " = m.id " +
                    "WHERE m." + COL_PLAYER_COUNT + " = ? " +
                    "ORDER BY pm." + COL_SCORE + " DESC";

    private static final String SELECT_RANK =
            "SELECT COUNT(*) + 1 FROM " + TABLE_PLAYER_MATCHES + " pm " +
                    "JOIN " + TABLE_MATCHES + " m ON pm." + COL_MATCH_ID + " = m.id " +
                    "WHERE m." + COL_PLAYER_COUNT + " = ? AND pm." + COL_SCORE + " > ?";
    //endregion

    /**
     * Saves the complete results of a match using a transaction.
     *
     * @param players     The list of players who participated.
     * @param playerCount The total number of players for this match category.
     */
    public static void saveFullMatch(List<Player> players, int playerCount) {
        if (!DatabaseManager.isAvailable()) return;

        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            int matchId = -1;
            // 1. Insert Match record
            try (PreparedStatement psMatch = conn.prepareStatement(INSERT_MATCH, Statement.RETURN_GENERATED_KEYS)) {
                psMatch.setInt(1, playerCount);
                psMatch.executeUpdate();

                try (ResultSet rs = psMatch.getGeneratedKeys()) {
                    if (rs.next()) matchId = rs.getInt(1);
                }
            }

            if (matchId == -1) throw new SQLException("Failed to retrieve match ID.");

            // 2. Insert link records (Batch)
            try (PreparedStatement psDetail = conn.prepareStatement(INSERT_PLAYER_MATCH)) {
                for (Player p : players) {
                    // Reusing PlayerDAO to get or create the unique player ID
                    int playerId = PlayerDAO.saveOrGetPlayer(p.getNickname());

                    psDetail.setInt(1, playerId);
                    psDetail.setInt(2, matchId);
                    psDetail.setInt(3, p.getPrestigePoints());
                    psDetail.addBatch();
                }
                psDetail.executeBatch();
            }

            conn.commit();
            System.out.println("[DB] Match " + matchId + " saved successfully.");

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            System.err.println("[DB] Error saving match: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Retrieves the leaderboard for a specific player count mode.
     */
    public static List<String> getLeaderboard(int playerCount) {
        List<String> results = new ArrayList<>();
        if (!DatabaseManager.isAvailable()) return results;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_LEADERBOARD)) {

            pstmt.setInt(1, playerCount);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String entry = String.format("%s: %d (Played on %s)",
                            rs.getString(COL_NICKNAME),
                            rs.getInt(COL_SCORE),
                            rs.getTimestamp(COL_MATCH_DATE).toString());
                    results.add(entry);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error fetching leaderboard: " + e.getMessage());
        }
        return results;
    }

    /**
     * Gets the rank of a player's score.
     */
    public static int getRank(int score, int playerCount) {
        if (!DatabaseManager.isAvailable()) return -1;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_RANK)) {

            pstmt.setInt(1, playerCount);
            pstmt.setInt(2, score);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error calculating rank: " + e.getMessage());
        }
        return -1;
    }
}