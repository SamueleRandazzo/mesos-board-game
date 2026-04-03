package it.polimi.ingsw.model.Cards;

import it.polimi.ingsw.model.Interfaces.EventEffect;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Interfaces.TribeDeck;

import java.util.List;

/**
 * Represents an Event Card in the game.
 * It holds an EventEffect that is applied to the players when the event is resolved.
 */
public class EventCard extends Card implements TribeDeck {

    private final boolean isFinal;
    private final EventEffect eventEffect;

    /**
     * Creates an EventCard.
     *
     * @param era         the era of the card
     * @param minPlayer   the minimum number of players required for this card to be in the deck
     * @param isFinal     true if this is a final event card, false otherwise
     * @param eventEffect the specific effect triggered by this event
     * @throws IllegalArgumentException if eventEffect is null
     */
    public EventCard(int era, int minPlayer, boolean isFinal, EventEffect eventEffect) {
        super(era, minPlayer, false);

        if (eventEffect == null) {
            throw new IllegalArgumentException("EventEffect cannot be null.");
        }

        this.isFinal = isFinal;
        this.eventEffect = eventEffect;
    }

    /**
     * Resolves the event effect for the provided list of players.
     *
     * @param players the list of players participating in the event resolution
     * @throws IllegalArgumentException if the players list is null
     * @throws IllegalStateException    if the players list is empty
     */
    public void raiseEvent(List<Player> players) {
        if (players == null) {
            throw new IllegalArgumentException("Players list cannot be null.");
        }
        if (players.isEmpty()) {
            throw new IllegalStateException("Cannot resolve event with no players.");
        }

        eventEffect.resolve(players);
    }

    /**
     * @return true if this card represents a final event, false otherwise
     */
    public boolean isFinal() {
        return this.isFinal;
    }

    /**
     * Returns the EventEffect associated with this card.
     * Used by Game to identify the type of event (e.g. Sustenance)
     * without relying on subclasses.
     *
     * @return the EventEffect of this card
     */
    public EventEffect getEventEffect() {
        return eventEffect;
    }

    /**
     * Event cards cannot be obtained by a player.
     * * @param player the player trying to obtain the card
     * @throws IllegalArgumentException always, because event cards cannot be obtained
     */
    @Override
    public void applyTo(Player player) {
        throw new IllegalArgumentException("You can't obtain an event card.");
    }

    /**
     * @return true to indicate that this specific TribeDeck card is an event
     */
    @Override
    public boolean isEvent() {
        return true;
    }
}