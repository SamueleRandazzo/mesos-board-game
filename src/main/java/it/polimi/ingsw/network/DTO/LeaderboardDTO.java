package it.polimi.ingsw.network.DTO;

import java.io.Serializable;
import java.util.List;
import java.util.Collections;

/**
 * A Data Transfer Object (DTO) containing the finalized session leaderboard and tiebreaker results.
 * <p>
 * This class encapsulates the final standings of a specific game session, mapping each participating
 * player to their respective scoring metrics. It explicitly tracks whether the match concluded in an
 * absolute tie via the {@code isSharedVictory} flag, allowing the client interface to render custom
 * celebration or victory banners accordingly.
 * </p>
 * <p>
 * To ensure architectural stability across the network layer, the underlying list structure
 * is wrapped inside an unmodifiable collection upon creation.
 * </p>
 *
 * @see Serializable
 * @see PlayerRankDTO
 */
public class LeaderboardDTO implements Serializable {

    /** The serial version UID for ensuring structural binary compatibility during network deserialization. */
    private static final long serialVersionUID = 1L;

    /** The sorted leaderboard entries containing session player standings and final metrics. */
    private List<PlayerRankDTO> rankings;

    /** Flag indicating whether multiple players shared the absolute first-place victory. */
    private boolean isSharedVictory;

    /**
     * Default constructor strictly reserved for automated reflection, object mapping,
     * or network serialization frameworks (e.g., Jackson, RMI).
     */
    protected LeaderboardDTO() { }

    /**
     * Constructs an immutable LeaderboardDTO instance with the provided player rankings
     * and victory status flag.
     * <p>
     * The input list is automatically decorated with {@link Collections#unmodifiableList(List)}
     * to safeguard the immutable design of data transfer nodes across client threads.
     * </p>
     *
     * @param rankings        the sorted {@link List} of {@link PlayerRankDTO} entries; must not be null
     * @param isSharedVictory {@code true} if the game rules resolved into a joint first-place tie;
     *                        {@code false} if a single distinct winner was declared
     */
    public LeaderboardDTO(List<PlayerRankDTO> rankings, boolean isSharedVictory) {
        this.rankings = Collections.unmodifiableList(rankings);
        this.isSharedVictory = isSharedVictory;
    }

    /**
     * Retrieves the unmodifiable list of player rankings composing this session scoreboard.
     *
     * @return an immutable {@link List} of {@link PlayerRankDTO} objects sorted by final placement
     */
    public List<PlayerRankDTO> getRankings() {
        return rankings;
    }

    /**
     * Checks whether the match ended in a shared victory among the top-tier tied players.
     *
     * @return {@code true} if there is an absolute tie for first place; {@code false} if there is a lone winner
     */
    public boolean isSharedVictory() {
        return isSharedVictory;
    }
}