package it.polimi.ingsw.persistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Serializable snapshot of a player state.
 */
public class PlayerSnapshot {
    /** Player nickname. */
    public String nickname;
    /** Player color name. */
    public String color;
    /** Current amount of food owned by the player. */
    public int foodAmount;
    /** Current amount of prestige points owned by the player. */
    public int prestigePoints;
    /** Identifiers of all cards owned by the player. */
    public List<String> ownedCardIds = new ArrayList<>();
    /** Remaining upper-row picks for the player. */
    public int upperPick;
    /** Remaining lower-row picks for the player. */
    public int lowerPick;
}
