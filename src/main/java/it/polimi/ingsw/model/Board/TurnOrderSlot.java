package it.polimi.ingsw.model.Board;


import it.polimi.ingsw.model.Player;

import java.util.Optional;

public class TurnOrderSlot {

    private final int foodModifier;
    private Player occupyingPlayer;
    //TODO: L'ultima TurnOrderSlot da malus punti prestigio se non si ha cibo

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
        return occupyingPlayer != null;
    }

    /**
     * returns the player that occupied this slot
     */
    public Optional<Player> getOccupyingPlayer() {
        return Optional.ofNullable(this.occupyingPlayer);
    }

    /** make this slot occupied, returns an IllegalStateException if this slot was already occupied*/
    public void occupy(Player player){
        if (player == null) {
            throw new IllegalArgumentException("Cannot occupy a slot with a null totem.");
        }
        if(this.isOccupied()){
            throw new IllegalStateException("Slot already occupied");
        }

        this.occupyingPlayer = player;

    }

    /** make this slot free*/
    public void clean() {
        this.occupyingPlayer = null;
    }
}
