package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;

/**
 * Represents a Gatherer character card within the game.
 * <p>
 * The Gatherer is a standard character card type that can be recruited into a player's tribe.
 * It implements the {@link TribeDeck} interface, leveraging the double-dispatch visitor pattern
 * to safely add itself to the player's tableau and contribute to set-collection mechanics.
 * </p>
 *
 * @see Card
 * @see TribeDeck
 */
public class Gatherer extends Card implements TribeDeck {

    /**
     * Constructs a Gatherer card with the specified configuration attributes.
     *
     * @param id           the unique string identifier of the card
     * @param era          the historical era or round number this card belongs to
     * @param minPlayer    the minimum number of players required in the game for this card to be included
     * @param isObtainable true if the card can currently be acquired by players, false otherwise
     */
    public Gatherer(String id, int era, int minPlayer, boolean isObtainable){
        super(id, era, minPlayer, isObtainable);
    }

    /**
     * Applies this Gatherer card to the specified player by routing it directly
     * into their tribe's internal card lists.
     * <p>
     * At runtime, this triggers the specific overloaded {@code addCard(Gatherer)} method
     * on the player's tribe, allowing the board state to update and re-evaluate active set bonuses.
     * </p>
     *
     * @param p the {@link Player} acquiring this card; must not be null
     */
    @Override
    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}