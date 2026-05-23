package it.polimi.ingsw.model.Board;

import it.polimi.ingsw.model.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a Turn Order Tile, which groups together a sequence of {@link TurnOrderSlot}s.
 * This class manages player placement to determine the playing order for the subsequent round
 * and handles the distribution of associated food modifiers.
 */
public class TurnOrderTile {

    private final List<TurnOrderSlot> slots;

    /**
     * Constructs a TurnOrderTile and initializes it with the specified list of slots.
     *
     * @param slots the list of {@link TurnOrderSlot}s that make up this tile
     */
    public TurnOrderTile(List<TurnOrderSlot> slots){
        this.slots = new ArrayList<>(slots);
    }

    /**
     * Places a player's totem into the first available (unoccupied) slot on this tile.
     *
     * @param player the {@link Player} placing their totem
     * @return the food modifier value (bonus or malus) of the newly occupied slot
     * @throws IllegalStateException if all slots on this tile are already occupied
     */
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

    /**
     * Calculates and returns the turn order for the upcoming round based on the
     * order of occupied slots.
     *
     * @return a {@link List} of {@link Player}s arranged in the order they will play next round
     * @throws IllegalStateException if a slot is marked as occupied but contains no player data
     */
    public List<Player> getNextRoundOrder(){

        return slots.stream()
                .filter(TurnOrderSlot::isOccupied)
                .map(slot -> slot.getOccupyingPlayer()
                        .orElseThrow(() -> new IllegalStateException("Slot was occupied but totem is missing")))
                .collect(Collectors.toList());
    }

    /**
     * Frees any slot currently occupied by the specified player on this tile.
     *
     * @param player the {@link Player} whose totem should be removed from the slots
     */
    public void cleanTurnOrderSlot(Player player) {
        for (TurnOrderSlot slot : slots) {
            slot.getOccupyingPlayer()
                    .filter(p -> p.equals(player)) // If present but null -> NPE
                    .ifPresent(p -> slot.clean());
        }
    }

    /**
     * Returns the list of turn order slots contained within this tile.
     *
     * @return the {@link List} of {@link TurnOrderSlot}s
     */
    public List<TurnOrderSlot> getSlots() {
        return slots;
    }
}