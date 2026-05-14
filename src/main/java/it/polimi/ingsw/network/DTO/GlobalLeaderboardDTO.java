package it.polimi.ingsw.network.DTO;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class GlobalLeaderboardDTO  implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<GlobalPlayerRankDTO> rankings;

    /**
     * Default constructor for serialization frameworks.
     */
    protected GlobalLeaderboardDTO() { }

    public GlobalLeaderboardDTO(List<GlobalPlayerRankDTO> rankings) {
        this.rankings = Collections.unmodifiableList(rankings);
    }

    // Getter
    public List<GlobalPlayerRankDTO> getRankings() { return rankings; }
}