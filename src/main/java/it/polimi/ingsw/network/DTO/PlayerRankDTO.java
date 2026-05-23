package it.polimi.ingsw.network.DTO;

import java.io.Serializable;

/**
 * A detailed Data Transfer Object (DTO) capturing a player's complete performance metrics
 * at the conclusion of a game session.
 * <p>
 * This class encapsulates not only the final structural position (rank) and a winning flag,
 * but also explicit game resource evaluations—such as accumulated prestige points and final
 * food supplies—which are vital for client-side scoreboard breakdowns and tiebreaker transparency.
 * </p>
 *
 * @see Serializable
 * @see LeaderboardDTO
 */
public class PlayerRankDTO implements Serializable {

    /** The serial version UID for ensuring structural binary compatibility during network deserialization. */
    private static final long serialVersionUID = 1L;

    private String nickname;
    private int prestigePoints;
    private int foodAmount;
    private int position;
    private boolean isWinner;

    /**
     * Default constructor strictly reserved for automated reflection, object mapping,
     * or network serialization frameworks (e.g., Jackson, RMI).
     */
    protected PlayerRankDTO() { }

    /**
     * Constructs a fully populated PlayerRankDTO with individual scoring and resource metrics.
     *
     * @param nickname       the unique profile name identifying the player
     * @param prestigePoints the total primary victory/prestige points accumulated by the end of the match
     * @param foodAmount     the remaining food resources owned by the player, often used as a tiebreaker metric
     * @param position       the final placement or rank position standing (e.g., 1 for first place)
     * @param isWinner       {@code true} if this player qualifies as a winner of the match; {@code false} otherwise
     */
    public PlayerRankDTO(String nickname, int prestigePoints, int foodAmount, int position, boolean isWinner) {
        this.nickname = nickname;
        this.prestigePoints = prestigePoints;
        this.foodAmount = foodAmount;
        this.position = position;
        this.isWinner = isWinner;
    }

    /**
     * Retrieves the unique profile name of the tracked player.
     *
     * @return a {@link String} containing the player's nickname
     */
    public String getNickname() { return nickname; }

    /**
     * Retrieves the total baseline victory or prestige points earned by this player.
     *
     * @return the primary score points integer
     */
    public int getPrestigePoints() { return prestigePoints; }

    /**
     * Retrieves the total quantum of food supplies remaining in the player's stock at game end.
     *
     * @return the remaining food resource count
     */
    public int getFoodAmount() { return foodAmount; }

    /**
     * Retrieves the final placement position or numerical standing assigned to this player row.
     *
     * @return the rank placement integer (e.g., 1, 2, 3)
     */
    public int getPosition() { return position; }

    /**
     * Checks whether this specific player has achieved a victory state in the completed session.
     * <p>
     * Note that multiple players may return {@code true} simultaneously in the event of an absolute,
     * unresolved tie (shared victory scenario).
     * </p>
     *
     * @return {@code true} if this player is a winner; {@code false} otherwise
     */
    public boolean isWinner() { return isWinner; }
}