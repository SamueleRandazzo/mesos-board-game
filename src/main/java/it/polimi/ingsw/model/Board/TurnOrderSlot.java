package it.polimi.ingsw.model.Board;


import it.polimi.ingsw.model.Totem;

import java.util.Optional;

public class TurnOrderSlot {

    private final int foodModifier;
    private Totem occupyingTotem;

    /** Constructor: create the single TurnOrderSlot with the bonus/malus of food.
     */
    public TurnOrderSlot(int foodModifier){

        this.foodModifier = foodModifier;
    }

    /** returns the food bonus/malus of this slot*/
    public int getFoodModifier(){
        return foodModifier;
    }

    /** returns true if this slot is occupied, otherwise returns false*/
    public boolean isOccupied(){
        return occupyingTotem != null;
    }

    /** returns the totem that occupied this slot*/
    public Optional<Totem> getOccupyingTotem() {

        if(this.occupyingTotem != null){
            return Optional.of(this.occupyingTotem);
        }

        else{
            throw new IllegalStateException("Attempted to access on an empty slot.");
        }
    }

    /** make this slot occupied, returns an IllegalStateException if this slot was already occupied*/
    public void occupy(Totem totem){
        if (totem == null) {
            throw new IllegalArgumentException("Cannot occupy a slot with a null totem.");
        }
        if(this.isOccupied()){
            throw new IllegalStateException("Slot already occupied");
        }

        this.occupyingTotem = totem;

    }
}
