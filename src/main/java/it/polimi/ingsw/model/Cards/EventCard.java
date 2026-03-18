package it.polimi.ingsw.model.Cards;

import it.polimi.ingsw.model.Interfaces.EventEffect;
import it.polimi.ingsw.model.*;
import java.util.List;

public class EventCard extends Card {
    private final boolean isFinal;
    private final EventEffect eventEffect;

    public EventCard(int era, int minPlayer, boolean isObtainable, boolean isFinal, EventEffect eventEffect) {
        super(era, minPlayer, isObtainable);
        this.isFinal = isFinal;
        this.eventEffect = eventEffect;
    }

    public void raiseEvent(List<Player> players){
        eventEffect.resolve(players);
    }

    public boolean isFinal(){
        return this.isFinal;
    }
}
