package it.polimi.ingsw.model.BuildingCards;

import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Player;

/**
 * Represents a specialized type of {@link BuildingCard} that triggers bonuses
 * when specific card combinations or duplicates are added to a player's tribe.
 * It tracks parameters for rewarding sets of characters or duplicate inventors.
 */
public class CardAddedBuilding extends BuildingCard {
    private final int foodBonus;
    private final boolean bonusOnDuplicateInventor;
    private final boolean bonusOnSetCharacters;
    private int initialSetCount;
    private int rewardedSetCount;
    private int setDim;

    /**
     * Constructs a new CardAddedBuilding with the specified game rules, costs, and bonus triggers.
     *
     * @param id                       the unique identifier of the card
     * @param era                      the historical era or age this card belongs to
     * @param minPlayer                the minimum number of players required for this card to be active
     * @param isObtainable             true if the card can currently be acquired by players
     * @param foodCost                 the amount of food required to construct/buy this building
     * @param prestigePoints           the raw prestige or victory points awarded by this card
     * @param bonusOnDuplicateInventor true if this building grants a bonus for having duplicate inventors
     * @param bonusOnSetCharacters     true if this building grants a bonus for completing sets of characters
     * @param foodBonus                the amount of food bonus provided when a condition is met
     * @param setDim                   the required size (dimension) of a character set to trigger the bonus
     */
    public CardAddedBuilding(String id, int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints,
                             boolean bonusOnDuplicateInventor, boolean bonusOnSetCharacters, int foodBonus, int setDim) {
        super(id, era, minPlayer, isObtainable, foodCost, prestigePoints);
        this.bonusOnDuplicateInventor = bonusOnDuplicateInventor;
        this.bonusOnSetCharacters = bonusOnSetCharacters;
        this.foodBonus = foodBonus;
        this.setDim = setDim;
        initialSetCount = 0;
        rewardedSetCount = 0;
    }

    /**
     * Checks if this building provides a bonus when duplicate inventor cards are acquired.
     *
     * @return true if the bonus applies to duplicate inventors, false otherwise
     */
    public boolean isBonusOnDuplicateInventor() {
        return bonusOnDuplicateInventor;
    }

    /**
     * Checks if this building provides a bonus based on completing sets of distinct characters.
     *
     * @return true if the bonus applies to sets of characters, false otherwise
     */
    public boolean isBonusOnSetCharacters() {
        return bonusOnSetCharacters;
    }

    /**
     * Returns the food bonus amount granted by this building's special conditions.
     *
     * @return the food bonus value
     */
    public int getFoodBonus() {
        return foodBonus;
    }

    /**
     * Returns the initial count of sets recorded when the card or tracking action began.
     *
     * @return the initial set count
     */
    public int getInitialSetCount() {
        return initialSetCount;
    }

    /**
     * Returns the required size (number of unique cards) that forms a complete set for this building.
     *
     * @return the dimension/size of the targeted card set
     */
    public int getSetDim() {
        return setDim;
    }

    /**
     * Returns the total number of sets that have already been rewarded with a bonus.
     *
     * @return the count of rewarded sets
     */
    public int getRewardedSetCount() {
        return rewardedSetCount;
    }

    /**
     * Sets the initial baseline count of sets for scoring or tracking purposes.
     *
     * @param n the initial number of sets to register
     */
    public void setInitialSetCount(int n) {
        this.initialSetCount = n;
    }

    /**
     * Increments the internal counter tracking how many sets have successfully
     * triggered and received their corresponding rewards.
     */
    public void incrementRewardedSetCount() {
        rewardedSetCount++;
    }

    /**
     * Applies the effects of this building card to the specified player.
     * This permanently attaches this card to the player's personal {@link Tribe}.
     *
     * @param p the {@link Player} acquiring this building card
     */
    @Override
    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}