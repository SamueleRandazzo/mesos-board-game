package it.polimi.ingsw.persistence;

import java.util.ArrayList;
import java.util.List;

public class PlayerSnapshot {
    public String nickname;
    public String color;
    public int foodAmount;
    public int prestigePoints;
    public List<String> ownedCardIds = new ArrayList<>();
    public int upperPick;
    public int lowerPick;
}
