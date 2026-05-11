package it.polimi.ingsw.network.DTO;

import java.io.Serializable;

public class PlayerRankDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nickname;
    private int prestigePoints;
    private int foodAmount;
    private int position;
    private boolean isWinner;

    /**
     * Default constructor for serialization frameworks.
     */
    protected PlayerRankDTO() { }

    public PlayerRankDTO(String nickname, int prestigePoints, int foodAmount, int position, boolean isWinner) {
        this.nickname = nickname;
        this.prestigePoints = prestigePoints;
        this.foodAmount = foodAmount;
        this.position = position;
        this.isWinner = isWinner;
    }

    // Getter
    public String getNickname() { return nickname; }
    public int getPrestigePoints() { return prestigePoints; }
    public int getFoodAmount() { return foodAmount; }
    public int getPosition() { return position; }
    public boolean isWinner() { return isWinner; }
}