package it.polimi.ingsw.model.BuildingCards;

import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Player;

/**
 * Represents a specialized {@link BuildingCard} that grants instant effects or
 * continuous passive abilities to a player upon acquisition.
 * These effects include star modifiers, loss prevention, scoring multipliers,
 * and bonuses to card drawing or resource gathering.
 */
public class InstantEffectBuilding extends BuildingCard {
    private final int extraStars;
    private final boolean preventLoss;
    private final boolean doubleOnWinning;
    private final boolean extraCardFromUpper;
    private final boolean extraFoodFromBonus;

    /**
     * Constructs a new InstantEffectBuilding with the specified attributes and active effect flags.
     *
     * @param id                 the unique identifier of the card
     * @param era                the historical era or age this card belongs to
     * @param minPlayer          the minimum number of players required for this card to be active
     * @param isObtainable       true if the card can currently be acquired by players
     * @param foodCost           the amount of food required to construct/buy this building
     * @param prestigePoints     the raw prestige or victory points awarded by this card
     * @param extraStars         the number of additional stars granted by this building
     * @param preventLoss        true if this building prevents a specific type of penalty or resource loss
     * @param doubleOnWinning    true if this building doubles the rewards or points when a winning condition is met
     * @param extraCardFromUpper true if this building allows drawing an extra card from the upper row
     * @param extraFoodFromBonus true if this building yields extra food whenever a bonus is triggered
     */
    public InstantEffectBuilding(String id, int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints,
                                 int extraStars, boolean preventLoss, boolean doubleOnWinning, boolean extraCardFromUpper, boolean extraFoodFromBonus) {
        super(id, era, minPlayer, isObtainable, foodCost, prestigePoints);
        this.extraStars = extraStars;
        this.preventLoss = preventLoss;
        this.doubleOnWinning = doubleOnWinning;
        this.extraCardFromUpper = extraCardFromUpper;
        this.extraFoodFromBonus = extraFoodFromBonus;
    }

    /**
     * Returns the number of extra stars granted by this building.
     *
     * @return the extra stars value
     */
    public int getExtraStars() {
        return extraStars;
    }

    /**
     * Checks whether this building protects the player from resource or point loss.
     *
     * @return true if loss prevention is active, false otherwise
     */
    public boolean isPreventLoss() {
        return preventLoss;
    }

    /**
     * Checks whether this building applies a doubling multiplier upon winning a specific check or phase.
     *
     * @return true if rewards are doubled on winning, false otherwise
     */
    public boolean isDoubleOnWinning() {
        return doubleOnWinning;
    }

    /**
     * Checks whether this building enables the player to draw an additional card from the upper row.
     *
     * @return true if extra upper row drawing is allowed, false otherwise
     */
    public boolean isExtraCardFromUpper() {
        return extraCardFromUpper;
    }

    /**
     * Checks whether this building triggers additional food placement or collection from bonuses.
     *
     * @return true if extra food from bonuses is enabled, false otherwise
     */
    public boolean isExtraFoodFromBonus() {
        return extraFoodFromBonus;
    }

    /**
     * Applies the effects of this building card to the specified player.
     * This permanently attaches this card to the player's personal {@link it.polimi.ingsw.model.Cards.Tribe}.
     *
     * @param p the {@link Player} acquiring this building card
     */
    @Override
    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}