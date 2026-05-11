package it.polimi.ingsw.network.DTO;

import java.io.Serializable;
import java.util.List;
import java.util.Collections;

public class LeaderboardDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<PlayerRankDTO> rankings;
    private boolean isSharedVictory;

    /**
     * Default constructor for serialization frameworks.
     */
    protected LeaderboardDTO() { }

    public LeaderboardDTO(List<PlayerRankDTO> rankings, boolean isSharedVictory) {
        this.rankings = Collections.unmodifiableList(rankings);
        this.isSharedVictory = isSharedVictory;
    }

    // Getter
    public List<PlayerRankDTO> getRankings() { return rankings; }
    public boolean isSharedVictory() { return isSharedVictory; }
}