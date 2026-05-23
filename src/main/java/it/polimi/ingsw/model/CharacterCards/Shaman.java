package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;

/**
 * Represents a Shaman character card within the game.
 * <p>
 * Shaman cards are specialized characters that contribute mystical or spiritual power to a tribe.
 * Each Shaman card carries a specific number of shamanic stars which, upon recruitment, are
 * immediately added to the player's tribe-wide shamanic attributes tracker to unlock or boost
 * corresponding mechanics.
 * </p>
 *
 * @see Card
 * @see TribeDeck
 */
public class Shaman extends Card implements TribeDeck {
    private final int shamanStars;

    /**
     * Constructs a Shaman card with the specified generic card attributes and its star power value.
     *
     * @param id           the unique string identifier of the card
     * @param era          the historical era or round number this card belongs to
     * @param minPlayer    the minimum number of players required in the game for this card to be included
     * @param isObtainable true if the card can currently be acquired by players, false otherwise
     * @param shamanStars  the number of shamanic stars/points provided by this card
     */
    public Shaman(String id, int era, int minPlayer, boolean isObtainable, int shamanStars){
        super(id, era, minPlayer, isObtainable);
        this.shamanStars = shamanStars;
    }

    /**
     * Retrieves the number of shamanic stars provided by this Shaman card.
     *
     * @return the shaman stars value
     */
    public int getShamanStars(){
        return this.shamanStars;
    }

    /**
     * Applies this Shaman card to the specified player by anchoring it within their tribe's collection.
     * <p>
     * This execution implements double-dispatch via the {@link TribeDeck} interface, routing the call
     * to the overloaded {@code addCard(Shaman)} method inside the player's tribe. This automatically
     * updates the tribe's total star count within its shamanic attributes tracker.
     * </p>
     *
     * @param p the {@link Player} acquiring this card; must not be null
     */
    @Override
    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}