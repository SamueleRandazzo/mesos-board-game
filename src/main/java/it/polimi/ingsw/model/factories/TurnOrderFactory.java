package it.polimi.ingsw.model.factories;

import it.polimi.ingsw.model.Board.*;

import java.util.ArrayList;
import java.util.List;

public class TurnOrderFactory {

    /* creates the TurnOrderTile, the param is the number of player of the game and returns
       a configuration TurnOrderTile
     */
    public static TurnOrderTile createTrack(int numOfPlayers){

        List<TurnOrderSlot> slots = new ArrayList<>();

        switch(numOfPlayers){
            case 2:
            case 3:
                slots.add(new TurnOrderSlot(1));
                slots.add(new TurnOrderSlot(0));
                slots.add(new TurnOrderSlot(-1));
                break;
            case 4:
                slots.add(new TurnOrderSlot(2));
                slots.add(new TurnOrderSlot(1));
                slots.add(new TurnOrderSlot(0));
                slots.add(new TurnOrderSlot(-1));
                break;
            case 5:
                // For 5 players: 5 slots
                slots.add(new TurnOrderSlot(2));
                slots.add(new TurnOrderSlot(1));
                slots.add(new TurnOrderSlot(0));
                slots.add(new TurnOrderSlot(0));
                slots.add(new TurnOrderSlot(-2));
                break;
            default:
                throw new IllegalArgumentException("Invalid number of players: " + numOfPlayers);
        }

        return new TurnOrderTile(slots);
    }
}
