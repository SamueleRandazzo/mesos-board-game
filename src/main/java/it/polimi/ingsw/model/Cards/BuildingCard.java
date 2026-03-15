package it.polimi.ingsw.model.Cards;

import it.polimi.ingsw.model.Interfaces.BuildingEffect;
import java.util.List;

public class BuildingCard extends Card {
    private final int foodCost;
    private final int prestigePoints;
    private List<BuildingEffect> effectList;
    private final boolean isFinalBuilding;

    BuildingCard(int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints, boolean isFinalBuilding) {
        super(era, minPlayer, isObtainable);
        this.foodCost = foodCost;
        this.prestigePoints = prestigePoints;
        this.isFinalBuilding = isFinalBuilding;
    }

    // Return building food cost
    public int getFoodCost() {
        return this.foodCost;
    }

    // Return building prestige points
    public int getPrestigePoints() {
        return  this.prestigePoints;
    }

    public boolean isFinalBuilding() {
        return isFinalBuilding;
    }

    // Add effect
    public void addEffect(BuildingEffect effect) {
        this.effectList.add(effect);
    }

    // Activate all effect
    public void activateAllEffect() {
        for (BuildingEffect e : this.effectList) {
            e.applyEffect();
        }
    }
}
