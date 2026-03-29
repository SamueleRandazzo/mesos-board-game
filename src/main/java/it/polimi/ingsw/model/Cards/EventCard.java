package it.polimi.ingsw.model.Cards;

import it.polimi.ingsw.model.Interfaces.EventEffect;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Interfaces.TribeDeck;

import java.util.List;

public class EventCard extends Card implements TribeDeck {
    private final boolean isFinal;
    private final EventEffect eventEffect;

    public EventCard(int era, int minPlayer, boolean isFinal, EventEffect eventEffect) {
        super(era, minPlayer, false);
        this.isFinal = isFinal;
        this.eventEffect = eventEffect;
    }

    public void raiseEvent(List<Player> players){
        eventEffect.resolve(players);
    }

    public boolean isFinal(){
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

    public void applyTo(Player player) {
        throw new IllegalArgumentException("You can't obtain event card");
    }
}