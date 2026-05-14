package it.polimi.ingsw.network.DTO;

import java.io.Serializable;

public class GlobalPlayerRankDTO implements Serializable {
    private int rank;
    private String nickname;
    private int totalPoints;

    /**
     * Default constructor for serialization frameworks.
     */
    protected GlobalPlayerRankDTO() { }

    public GlobalPlayerRankDTO(int rank, String nickname, int totalPoints) {
        this.rank = rank;
        this.nickname = nickname;
        this.totalPoints = totalPoints;
    }

    public int getRank() { return rank; }
    public String getNickname() { return nickname; }
    public int getTotalPoints() { return totalPoints; }

    @Override
    public String toString() {
        return rank + ". " + nickname + " - Points: " + totalPoints;
    }
}