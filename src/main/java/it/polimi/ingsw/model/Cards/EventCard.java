package it.polimi.ingsw.model.Cards;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

import java.util.List;

public abstract class EventCard extends Card {
    private final boolean isFinal;

    //isFinal must be True if and only if the event is one of the two final events.
    //the further parameters are inherited from Card
    public EventCard(int era, int minPlayer, boolean isObtainable, boolean isFinal) {
        super(era, minPlayer, isObtainable);
        this.isFinal = isFinal;
    }

    //this method calls eventHandler on every Player playing the Game.
    //this is the method to call to make the event take place.
    public void raiseEvent(List<Player> players){
        for(Player player: players){
            eventHandler(player);
        }
    }

    //this method is implemented in the subclasses and performs the specific event action in each one of them
    protected abstract void eventHandler(Player player);

    //isFinal will be True if and only if the event is one of the two final events.
    public boolean isFinal(){
        return this.isFinal;
    }
}
