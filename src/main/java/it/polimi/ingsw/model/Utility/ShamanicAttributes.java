package it.polimi.ingsw.model.Utility;

import it.polimi.ingsw.model.BuildingCards.InstantEffectBuilding;

/**
 * Utility class that tracks a player's shamanic power-ups and active gameplay modifiers.
 * It stores indicators for extra victory points/stars, insurance mechanics that block point losses,
 * and double-reward multipliers triggered upon winning events.
 */
public class ShamanicAttributes {
    /**
     * The total count of extra shamanic stars or score modifiers accumulated.
     */
    private int stars;

    /**
     * Flag indicating whether the player is immune to losing points/resources during specific game events.
     */
    private boolean preventLoss;

    /**
     * Flag indicating whether the player's rewards are doubled upon a successful winning event.
     */
    private boolean doubleOnWinning;

    /**
     * Constructs a default ShamanicAttributes instance with zero stars
     * and all specific modifiers deactivated (set to false).
     */
    public ShamanicAttributes() {
        stars = 0;
        preventLoss = false;
        doubleOnWinning = false;
    }

    /**
     * Gets the total number of accumulated shamanic stars.
     *
     * @return the star counter value
     */
    public int getStars() {
        return stars;
    }

    /**
     * Checks if the score or resource loss prevention modifier is active.
     *
     * @return true if loss prevention is enabled, false otherwise
     */
    public boolean isPreventLoss() {
        return preventLoss;
    }

    /**
     * Checks if the double-reward victory multiplier modifier is active.
     *
     * @return true if rewards are doubled on a win, false otherwise
     */
    public boolean isDoubleOnWinning() {
        return doubleOnWinning;
    }

    /**
     * Increases the total shamanic star counter by adding a specific amount.
     *
     * @param stars the number of extra stars to add
     */
    public void addStars(int stars) {
        this.stars += stars;
    }

    /**
     * Directly updates the status of the loss prevention flag modifier.
     *
     * @param preventLoss the new state to apply to the loss prevention feature
     */
    public void setPreventLoss(boolean preventLoss) {
        this.preventLoss = preventLoss;
    }

    /**
     * Directly updates the status of the double reward multiplier flag modifier.
     *
     * @param doubleOnWinning the new state to apply to the victory multiplier feature
     */
    public void setDoubleOnWinning(boolean doubleOnWinning) {
        this.doubleOnWinning = doubleOnWinning;
    }

    /**
     * Appends and merges modifiers provided by an executed instant-effect building card.
     * Boolean parameters are combined using a logical OR operation to ensure that
     * existing active bonuses are safely preserved.
     *
     * @param card the {@link InstantEffectBuilding} instance source containing the bonuses to extract
     */
    public void setParamByBuilding(InstantEffectBuilding card) {
        addStars(card.getExtraStars());
        setPreventLoss(this.preventLoss || card.isPreventLoss());
        setDoubleOnWinning(this.doubleOnWinning || card.isDoubleOnWinning());
    }
}