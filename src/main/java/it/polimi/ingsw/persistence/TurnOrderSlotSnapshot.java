package it.polimi.ingsw.persistence;

/**
 * Serializable snapshot of one turn order slot.
 */
public class TurnOrderSlotSnapshot {
    /** Food modifier assigned to this slot. */
    public int foodModifier;
    /** Nickname of the occupying player, or {@code null} if empty. */
    public String occupyingPlayerNickname;
}
