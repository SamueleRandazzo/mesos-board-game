package it.polimi.ingsw.database;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.DTO.PlayerRankDTO;

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
    private static final String TABLE_MATCHES = "matches";
    private static final String TABLE_PLAYER_MATCHES = "player_matches";
    private static final String VIEW_GLOBAL_RANKINGS = "global_rankings";

    // --- Column Names ---
    private static final String COL_PLAYER_ID = "player_id";
    private static final String COL_MATCH_ID = "match_id";
    private static final String COL_NICKNAME = "nickname";
    private static final String COL_SCORE = "score";
    private static final String COL_PLAYER_COUNT = "player_count";
    private static final String COL_TOTAL_POINTS = "total_points";

    // --- SQL Queries ---
    private static final String INSERT_MATCH =
            "INSERT INTO " + TABLE_MATCHES + " (" + COL_PLAYER_COUNT + ") VALUES (?)";

    private static final String INSERT_PLAYER_MATCH =
            "INSERT INTO " + TABLE_PLAYER_MATCHES + " (" + COL_PLAYER_ID + ", " + COL_MATCH_ID + ", " + COL_SCORE + ") VALUES (?, ?, ?)";

    private static final String SELECT_LEADERBOARD =
            "SELECT " + COL_NICKNAME + ", " + COL_TOTAL_POINTS + " " +
                    "FROM " + VIEW_GLOBAL_RANKINGS + " " +
                    "WHERE " + COL_PLAYER_COUNT + " = ? " +
                    "ORDER BY " + COL_TOTAL_POINTS + " DESC";

    private static final String SELECT_GLOBAL_RANK_BY_SUM =
            "SELECT COUNT(*) + 1 AS ranking " +
                    "FROM " + VIEW_GLOBAL_RANKINGS + " v1 " +
                    "WHERE v1." + COL_PLAYER_COUNT + " = ? " +
                    "AND v1." + COL_TOTAL_POINTS + " > (" +
                    "    SELECT v2." + COL_TOTAL_POINTS + " " +
                    "    FROM " + VIEW_GLOBAL_RANKINGS + " v2 " +
                    "    WHERE v2." + COL_NICKNAME + " = ? AND v2." + COL_PLAYER_COUNT + " = ?" +
                    ")";
    //endregion

    /**
     * Saves the complete results of a match using a transaction.
     *
     * @param players     The list of players who participated.
     */
    public static void saveFullMatch(List<PlayerRankDTO> players) {
        if (!DatabaseManager.isAvailable()) return;

        int playerCount = players.size();

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
                for (PlayerRankDTO p : players) {
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
     * Retrieves the global leaderboard (cumulative points) for a specific match type.
     * Uses the SQL View 'v_player_total_points' for efficiency.
     *
     * @param playerCount The type of match (2-5 players).
     * @return A list of strings formatted as "Rank. Nickname - Total Points: X".
     */
    public static List<String> getLeaderboard(int playerCount) {
        List<String> results = new ArrayList<>();
        if (!DatabaseManager.isAvailable()) return results;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_LEADERBOARD)) {

            pstmt.setInt(1, playerCount);

            try (ResultSet rs = pstmt.executeQuery()) {
                int rank = 1;
                while (rs.next()) {
                    String entry = String.format("%d. %s - Total Points: %d",
                            rank++,
                            rs.getString(COL_NICKNAME),
                            rs.getInt(COL_TOTAL_POINTS));
                    results.add(entry);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error fetching leaderboard from view: " + e.getMessage());
        }
        return results;
    }

    /**
     * Calculates the global rank of a player based on their total accumulated score
     * across all matches played with the same number of players.
     *
     * @param nickname    The player's unique nickname.
     * @param playerCount The match type (e.g., 2, 3, 4, or 5 players).
     * @return The ranking position (1 is best). Returns -1 if player is not found or an error occurs.
     */
    public static int getRankByTotalPoints(String nickname, int playerCount) {
        if (!DatabaseManager.isAvailable()) return -1;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_GLOBAL_RANK_BY_SUM)) {

            pstmt.setInt(1, playerCount);
            pstmt.setString(2, nickname);
            pstmt.setInt(3, playerCount);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ranking");
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error calculating cumulative rank: " + e.getMessage());
        }
        return -1;
    }
}