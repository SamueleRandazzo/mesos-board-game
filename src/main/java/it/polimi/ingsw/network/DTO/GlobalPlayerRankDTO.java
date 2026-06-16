package it.polimi.ingsw.network.DTO;

import java.io.Serializable;

/**
 * A lightweight Data Transfer Object (DTO) capturing a single player's final placement data.
 * <p>
 * This class encapsulates individual performance summaries—specifically the final placement position,
 * player identity, and aggregated victory metrics—to be packed into a {@link GlobalLeaderboardDTO}
 * container and pushed across the network at game completion.
 * </p>
 *
 * @see Serializable
 * @see GlobalLeaderboardDTO
 */
public class GlobalPlayerRankDTO implements Serializable {

    /** The serial version UID for ensuring structural binary compatibility during network deserialization. */
    private static final long serialVersionUID = 1L;

    /** Final global ranking position. */
    private int rank;
    /** Nickname of the ranked player. */
    private String nickname;
    /** Total points accumulated across ranked games. */
    private int totalPoints;

    /**
     * Default constructor strictly reserved for automated reflection, object mapping,
     * or network serialization frameworks (e.g., Jackson, RMI).
     */
    protected GlobalPlayerRankDTO() { }

    /**
     * Constructs a populated GlobalPlayerRankDTO capturing the specific placement metrics of a player.
     *
     * @param rank        the final standing position of the player (e.g., 1 for first place)
     * @param nickname    the unique string profile name identifying the player
     * @param totalPoints the aggregated final score or prestige rating achieved by the player
     */
    public GlobalPlayerRankDTO(int rank, String nickname, int totalPoints) {
        this.rank = rank;
        this.nickname = nickname;
        this.totalPoints = totalPoints;
    }

    /**
     * Retrieves the final standing position assigned to this player row.
     *
     * @return the rank position integer
     */
    public int getRank() { return rank; }

    /**
     * Retrieves the unique identity profile name of the ranked player.
     *
     * @return a {@link String} containing the player's nickname
     */
    public String getNickname() { return nickname; }

    /**
     * Retrieves the total aggregated victory or prestige points earned by this player.
     *
     * @return the accumulated total points score
     */
    public int getTotalPoints() { return totalPoints; }

    /**
     * Generates a clean, user-friendly textual representation of the ranking entry line,
     * suitable for CLI rendering, text interfaces, or logging nodes.
     * <p>
     * Output structure follows the standard format: {@code "[rank]. [nickname] - Points: [totalPoints]"}
     * </p>
     *
     * @return a formatted string tracking this specific placement row
     */
    @Override
    public String toString() {
        return rank + ". " + nickname + " - Points: " + totalPoints;
    }
}
