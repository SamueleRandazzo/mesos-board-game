package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;

/**
 * Represents an Artist character card within the game.
 * <p>
 * The Artist is a specific type of character card that can be added to a player's tribe.
 * It implements the {@link TribeDeck} interface, participating in the visitor-like pattern
 * used to dynamically dispatch and apply card effects to a {@link Player}'s tribe.
 * </p>
 *
 * @see Card
 * @see TribeDeck
 */
public class Artist extends Card implements TribeDeck {

    /**
     * Constructs an Artist card with the specified unique identifier, era,
     * player count availability, and obtainability status.
     *
     * @param id           the unique string identifier of the card
     * @param era          the historical era or round number this card belongs to
     * @param minPlayer    the minimum number of players required in the game for this card to be included
     * @param isObtainable true if the card can currently be acquired by players, false otherwise
     */
    public Artist(String id, int era, int minPlayer, boolean isObtainable) {
        super(id, era, minPlayer, isObtainable);
    }

    /**
     * Applies this Artist card to the specified player by adding itself directly
     * to their tribe's internal collection.
     * <p>
     * This method resolves the specific type of the card at runtime, ensuring that
     * the player's tribe triggers the appropriate overloaded {@code addCard(Artist)} logic,
     * which in turn handles set evaluation and related bonuses.
     * </p>
     *
     * @param p the {@link Player} acquiring this card; must not be null
     */
    @Override
    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}