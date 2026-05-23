package it.polimi.ingsw.model.Board;

import it.polimi.ingsw.model.Player;
import java.util.Optional;

/**
 * Represents a single slot within the turn order track.
 * Each slot can hold a single player's totem, determines turn order, and applies
 * a specific food modifier (bonus or malus) to the occupying player.
 */
public class TurnOrderSlot {

    private final int foodModifier;
    private Player occupyingPlayer;

    /**
     * Constructs a TurnOrderSlot with a specified food modifier.
     *
     * @param foodModifier the positive (bonus) or negative (malus) food amount associated with this slot
     */
    public TurnOrderSlot(int foodModifier){
        this.foodModifier = foodModifier;
    }

    /**
     * Returns the food modifier (bonus or malus) of this slot.
     *
     * @return the food modifier value
     */
    public int getFoodModifier(){
        return foodModifier;
    }

    /**
     * Checks whether this slot is currently occupied by a player.
     *
     * @return true if a player occupies this slot, false otherwise
     */
    public boolean isOccupied(){
        return occupyingPlayer != null;
    }

    /**
     * Returns an {@link Optional} containing the player occupying this slot,
     * or an empty {@link Optional} if the slot is free.
     *
     * @return an Optional wrapping the {@link Player}, or Optional.empty() if unoccupied
     */
    public Optional<Player> getOccupyingPlayer() {
        return Optional.ofNullable(this.occupyingPlayer);
    }

    /**
     * Occupies this slot with the specified player's totem.
     *
     * @param player the {@link Player} taking over the slot
     * @throws IllegalArgumentException if the provided player is null
     * @throws IllegalStateException    if the slot is already occupied by another player
     */
    public void occupy(Player player){
        if (player == null) {
            throw new IllegalArgumentException("Cannot occupy a slot with a null totem.");
        }
        if(this.isOccupied()){
            throw new IllegalStateException("Slot already occupied");
        }

        this.occupyingPlayer = player;
    }

    /**
     * Frees up the slot by removing the occupying player.
     * Typically called during cleanup or resetting phases.
     */
    public void clean() {
        this.occupyingPlayer = null;
    }
}