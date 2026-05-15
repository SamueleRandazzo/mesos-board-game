package it.polimi.ingsw.network.DTO;

import it.polimi.ingsw.model.Enum.Color;
import java.io.Serializable;

public class TurnOrderTileDTO implements Serializable{

    private String nickname;
    private Color color;
    int foodBonus;

    protected TurnOrderTileDTO() {}

    public TurnOrderTileDTO(String nickname, Color color, int foodBonus) {
        this.nickname = nickname;
        this.color = color;
        this.foodBonus = foodBonus;
    }

    public String getNickname() {
        return nickname;
    }

    public Color getColor() {
        return color;
    }

    public int getFoodBonus() {
        return foodBonus;
    }

}

