package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;

/**
 * Represents a Builder character card within the game.
 * <p>
 * Builder cards are specialized characters that enhance a player's construction capabilities.
 * They provide passive food discounts when building structures and contribute flat prestige points
 * to the player's final score. It implements the {@link TribeDeck} interface to resolve its concrete
 * type dynamically when added to a player's collection.
 * </p>
 *
 * @see Card
 * @see TribeDeck
 */
public class Builder extends Card implements TribeDeck {
    private final int foodDiscount;
    private final int prestigePoints;

    /**
     * Constructs a Builder card with game attributes and construction-specific modifiers.
     *
     * @param id             the unique string identifier of the card
     * @param era            the historical era or round number this card belongs to
     * @param minPlayer      the minimum number of players required in the game for this card to be included
     * @param isObtainable   true if the card can currently be acquired by players, false otherwise
     * @param foodDiscount   the flat reduction in food cost granted when building new structures
     * @param prestigePoints the fixed amount of victory/prestige points awarded by this card
     */
    public Builder(String id, int era, int minPlayer, boolean isObtainable, int foodDiscount, int prestigePoints) {
        super(id, era, minPlayer, isObtainable);
        this.foodDiscount = foodDiscount;
        this.prestigePoints = prestigePoints;
    }

    /**
     * Retrieves the food resource discount provided by this builder card.
     *
     * @return the building cost discount value in food units
     */
    public int getFoodDiscount(){
        return foodDiscount;
    }

    /**
     * Retrieves the baseline prestige points inherent to this builder card.
     *
     * @return the static prestige points value
     */
    public int getPrestigePoints(){
        return prestigePoints;
    }

    /**
     * Applies this Builder card to the specified player by adding itself to their tribe's tableau.
     * <p>
     * This method utilizes dynamic double-dispatch to trigger the specific {@code addCard(Builder)}
     * implementation inside the player's tribe, allowing the engine to aggregate cumulative building
     * discounts and evaluate character set configurations.
     * </p>
     *
     * @param p the {@link Player} acquiring this card; must not be null
     */
    @Override
    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}