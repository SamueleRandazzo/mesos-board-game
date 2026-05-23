package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Enum.InventionIcon;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;

/**
 * Represents an Inventor character card within the game.
 * <p>
 * Inventor cards are specialized characters associated with a specific {@link InventionIcon}.
 * When added to a player's tribe, their unique icons are tracked to evaluate set-collection
 * milestones or trigger resource bonuses from certain structures (e.g., duplicate inventor rewards).
 * It implements the {@link TribeDeck} interface for dynamic dispatch during recruitment.
 * </p>
 *
 * @see Card
 * @see TribeDeck
 * @see InventionIcon
 */
public class Inventor extends Card implements TribeDeck {
    private final InventionIcon inventionIcon;

    /**
     * Constructs an Inventor card with the specified configuration and invention token type.
     *
     * @param id            the unique string identifier of the card
     * @param era           the historical era or round number this card belongs to
     * @param minPlayer     the minimum number of players required in the game for this card to be included
     * @param isObtainable  true if the card can currently be acquired by players, false otherwise
     * @param inventionIcon the specific {@link InventionIcon} printed on this inventor card
     */
    public Inventor(String id, int era, int minPlayer, boolean isObtainable, InventionIcon inventionIcon){
        super(id, era, minPlayer, isObtainable);
        this.inventionIcon = inventionIcon;
    }

    /**
     * Retrieves the invention icon associated with this inventor card.
     *
     * @return the {@link InventionIcon} of this card
     */
    public InventionIcon getInventionIcon(){
        return this.inventionIcon;
    }

    /**
     * Applies this Inventor card to the specified player by registering it into their tribe's collection.
     * <p>
     * This execution uses double-dispatch to trigger the overloaded {@code addCard(Inventor)} method
     * inside the player's tribe. This allows the system to recalculate unique invention sets and
     * check for potential building food bonuses related to paired icons.
     * </p>
     *
     * @param p the {@link Player} acquiring this card; must not be null
     */
    @Override
    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}