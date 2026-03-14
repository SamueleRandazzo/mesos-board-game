package it.polimi.ingsw.Cards;
import it.polimi.ingsw.Game;
import it.polimi.ingsw.Player;

import java.util.*;

public abstract class EventCard extends Card {
    public EventCard(int era, int minPlayer, boolean isObtainable) {
        super(era, minPlayer, isObtainable);
    } //does it make sense to make a constructor with three arguments in father class?

    public void raiseEvent(){
        for(Player player: Game.game().getPlayers()){
            callEvent(player);
        }
    }

    abstract public void callEvent(Player player);
}
