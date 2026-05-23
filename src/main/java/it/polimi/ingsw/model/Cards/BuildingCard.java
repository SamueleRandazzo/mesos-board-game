package it.polimi.ingsw.model.Cards;

/**
 * Represents the abstract blueprint for all Building Cards in the game.
 * This class extends the generic {@link Card} by introducing core economic and victory
 * parameters common to every building, specifically a food cost to construct it
 * and upfront prestige points.
 */
public abstract class BuildingCard extends Card {
    private final int foodCost;
    private final int prestigePoints;

    /**
     * Initializes the core attributes of a Building Card.
     * This constructor is invoked by concrete subclasses to set up both generic card properties
     * and building-specific values.
     *
     * @param id             the unique identifier of the card
     * @param era            the historical era or age this card belongs to
     * @param minPlayer      the minimum number of players required for this card to be included in the game
     * @param isObtainable   true if the card can currently be acquired by players, false otherwise
     * @param foodCost       the amount of food required to construct/buy this building
     * @param prestigePoints the immediate prestige or victory points awarded upon constructing this building
     */
    protected BuildingCard(String id, int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints) {
        super(id, era, minPlayer, isObtainable);
        this.foodCost = foodCost;
        this.prestigePoints = prestigePoints;
    }

    /**
     * Returns the food cost required to build or purchase this building.
     *
     * @return the building's food cost
     */
    public int getFoodCost() {
        return this.foodCost;
    }

    /**
     * Returns the baseline prestige or victory points awarded immediately by this building.
     *
     * @return the standard prestige points value
     */
    public int getPrestigePoints() {
        return this.prestigePoints;
    }
}