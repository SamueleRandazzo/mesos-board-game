package it.polimi.ingsw.model.Cards;

public abstract class BuildingCard extends Card {
    private final int foodCost;
    private final int prestigePoints;
    private final boolean isFinalBuilding;

    protected BuildingCard(int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints, boolean isFinalBuilding) {
        super(era, minPlayer, isObtainable);
        this.foodCost = foodCost;
        this.prestigePoints = prestigePoints;
        this.isFinalBuilding = isFinalBuilding;
    }

    /** Return building food cost */
    public int getFoodCost() {
        return this.foodCost;
    }

    /** Return building prestige points */
    public int getPrestigePoints() {
        return  this.prestigePoints;
    }

    /** Return final building flag */
    public boolean isFinalBuilding() {
        return isFinalBuilding;
    }
}
