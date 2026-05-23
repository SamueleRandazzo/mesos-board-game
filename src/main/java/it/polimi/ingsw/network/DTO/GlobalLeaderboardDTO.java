package it.polimi.ingsw.network.DTO;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * A Data Transfer Object (DTO) representing the immutable global leaderboard snapshot at the end of the game.
 * <p>
 * This class packages the final standings of all participants, sorted chronologically by performance
 * and tiebreaker resolutions. It is serialized on the server and transmitted across the network
 * to synchronize the final scoreboard display on client interfaces.
 * </p>
 * <p>
 * To guarantee data integrity over the network layer, the underlying list structure is sealed
 * using unmodifiable collection wrappers upon construction.
 * </p>
 *
 * @see Serializable
 * @see GlobalPlayerRankDTO
 */
public class GlobalLeaderboardDTO implements Serializable {

    /** The serial version UID for ensuring structural binary compatibility during network deserialization. */
    private static final long serialVersionUID = 1L;

    /** The sorted leaderboard entries containing player standings and scoring breakdowns. */
    private List<GlobalPlayerRankDTO> rankings;

    /**
     * Default constructor strictly reserved for automated reflection, object mapping,
     * or network serialization frameworks (e.g., Jackson, RMI).
     */
    protected GlobalLeaderboardDTO() { }

    /**
     * Constructs an immutable GlobalLeaderboardDTO instance with the provided final player rankings.
     * <p>
     * The input list is automatically decorated with {@link Collections#unmodifiableList(List)}
     * to prevent unexpected client-side mutations of the final score records.
     * </p>
     *
     * @param rankings the sorted {@link List} of {@link GlobalPlayerRankDTO} records; must not be null
     */
    public GlobalLeaderboardDTO(List<GlobalPlayerRankDTO> rankings) {
        this.rankings = Collections.unmodifiableList(rankings);
    }

    /**
     * Retrieves the unmodifiable list of player rankings composing this global leaderboard.
     *
     * @return a {@link List} of {@link GlobalPlayerRankDTO} objects sorted by their final standings
     */
    public List<GlobalPlayerRankDTO> getRankings() {
        return rankings;
    }
}