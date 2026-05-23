package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;

/**
 * Represents a Hunter character card within the game.
 * <p>
 * Hunter cards are specialized characters that can yield immediate resource bonuses upon recruitment.
 * Depending on whether the card features a specific food icon, it can trigger an immediate payout
 * of food resources to the player's pool when added to their tableau.
 * </p>
 *
 * @see Card
 * @see TribeDeck
 */
public class Hunter extends Card implements TribeDeck {
    private final boolean foodIcon;

    /**
     * Constructs a Hunter card with game attributes and its specific resource icon configuration.
     *
     * @param id           the unique string identifier of the card
     * @param era          the historical era or round number this card belongs to
     * @param minPlayer    the minimum number of players required in the game for this card to be included
     * @param isObtainable true if the card can currently be acquired by players, false otherwise
     * @param foodIcon     true if this hunter card features an icon that awards immediate food resources
     */
    public Hunter(String id, int era, int minPlayer, boolean isObtainable, boolean foodIcon) {
        super(id, era, minPlayer, isObtainable);
        this.foodIcon = foodIcon;
    }

    /**
     * Checks whether this Hunter card features a food icon.
     *
     * @return true if the food icon is present on the card, false otherwise
     */
    public boolean hasFoodIcon(){
        return this.foodIcon;
    }

    /**
     * Applies this Hunter card to the specified player by embedding it into their tribe's collection.
     * <p>
     * This execution leverages double-dispatch to invoke the overloaded {@code addCard(Hunter)}
     * routine within the player's tribe. If {@link #hasFoodIcon()} evaluates to true, the tribe
     * logic will automatically adjust the player's food supply based on the current hunter count.
     * </p>
     *
     * @param p the {@link Player} acquiring this card; must not be null
     */
    @Override
    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}