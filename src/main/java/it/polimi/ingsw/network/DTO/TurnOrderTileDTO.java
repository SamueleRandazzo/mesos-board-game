package it.polimi.ingsw.network.DTO;

import it.polimi.ingsw.model.Enum.Color;
import java.io.Serializable;

public class TurnOrderTileDTO implements Serializable{


    private String nickname;
    private Color color;
    private int currentPrestigePoints;
    int foodBonus;
    int prestigeBonus;

    protected TurnOrderTileDTO(){}

    public TurnOrderTileDTO(String nickname, Color color, int currentPrestigePoints, int foodBonus, int prestigeBonus) {
        this.nickname = nickname;
        this.color = color;
        this.currentPrestigePoints = currentPrestigePoints;
        this.foodBonus = foodBonus;
        this.prestigeBonus = prestigeBonus;
    }

    public String getNickname() {
        return nickname;
    }

    public Color getColor() {
        return color;
    }

    public int getCurrentPrestigePoints() {
        return currentPrestigePoints;
    }

    public int getFoodBonus() {
        return foodBonus;
    }

    public void setPrestigeBonus(int prestigeBonus) {
        this.prestigeBonus = prestigeBonus;
    }
}

