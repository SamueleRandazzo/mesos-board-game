package it.polimi.ingsw.persistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Serializable snapshot of the turn order tile.
 */
public class TurnOrderSnapshot {
    /** Saved turn order slots in board order. */
    public List<TurnOrderSlotSnapshot> slots = new ArrayList<>();
}
