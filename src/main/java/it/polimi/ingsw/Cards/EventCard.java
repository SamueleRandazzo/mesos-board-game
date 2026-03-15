package it.polimi.ingsw.Cards;
import it.polimi.ingsw.Game;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.Enum.EventType;

import java.util.*;

public abstract class EventCard extends Card {
    private final boolean isFinal;
    public EventCard(int era, int minPlayer, boolean isObtainable, boolean isFinal) {
        super(era, minPlayer, isObtainable);
        this.isFinal = isFinal;
    } //does it make sense to make a constructor with three arguments in father class?

    public void raiseEvent(){
        for(Player player: Game.game().getPlayers()){
            eventHandler(player);
        }
    }

    public abstract void eventHandler(Player player);

    public boolean isFinal(){
        return this.isFinal;
    }
}
