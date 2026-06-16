package it.polimi.ingsw.persistence;

/**
 * Serializable snapshot of the action resolution state.
 */
public class ActionResolutionSnapshot {
    /** Nickname of the player currently resolving actions. */
    public String currentActivePlayerNickname;
    /** Whether an extra upper-row card choice is pending. */
    public boolean extraCardChoose;
}
