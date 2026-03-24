package it.polimi.ingsw.model.Board;

import it.polimi.ingsw.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TurnOrderTile {

    private final List<TurnOrderSlot> slots;

    /** Constructor: create the tile with the exact number and type of slot*/
    public TurnOrderTile(List<TurnOrderSlot> slots){
        this.slots = new ArrayList<>(slots);
    }

    /** place the param in the first free slot, return the food modifier of the slot, throws an IllegalStateException if all
    * slots are already occupied */
    public int placeTotem(Player player){

        for(TurnOrderSlot slot : slots){

            if(!slot.isOccupied()){

                slot.occupy(player);
                return slot.getFoodModifier();
            }
        }

        //if the loop finishes without returning, the track is full.
        throw new IllegalStateException("Cannot place totem: the Turn Order Track is full.");
    }

    /** Calculates the turn order for next round
        return the list of totem in the order they will play
     */
    public List<Player> getNextRoundOrder(){

        return slots.stream()
                .filter(TurnOrderSlot::isOccupied)
                .map(slot -> slot.getOccupyingPlayer()
                        .orElseThrow(() -> new IllegalStateException("Slot was occupied but totem is missing")))
                .collect(Collectors.toList());
    }


}
