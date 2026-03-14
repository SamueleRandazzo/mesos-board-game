package it.polimi.ingsw.Cards;

public class BuildingCard extends Card {
    private final int foodCost;
    private final int prestigePoints;

    BuildingCard(int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints) {
        super(era, minPlayer, isObtainable);
        this.foodCost = foodCost;
        this.prestigePoints = prestigePoints;
    }

    // Return building food cost
    public int getFoodCost() {
        return this.foodCost;
    }

    // Return building prestige points
    public int getPrestigePoints() {
        return  this.prestigePoints;
    }
}
