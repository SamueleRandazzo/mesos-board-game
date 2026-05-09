package it.polimi.ingsw.model.Cards;

public abstract class BuildingCard extends Card {
    private final int foodCost;
    private final int prestigePoints;

    protected BuildingCard(String id, int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints) {
        super(id, era, minPlayer, isObtainable);
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
