package it.polimi.ingsw.model.Cards;

import it.polimi.ingsw.model.Interfaces.PlayerTribe;

public abstract class BuildingCard extends Card implements PlayerTribe {
    private final int foodCost;
    private final int prestigePoints;

    protected BuildingCard(int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints) {
        super(era, minPlayer, isObtainable);
        this.foodCost = foodCost;
        this.prestigePoints = prestigePoints;
    }

    /** Return building food cost */
    public int getFoodCost() {
        return this.foodCost;
    }

    /** Return building prestige points */
    public int getPrestigePoints() {
        return  this.prestigePoints;
    }
}
