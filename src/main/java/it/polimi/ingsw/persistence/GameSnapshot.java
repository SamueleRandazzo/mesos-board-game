package it.polimi.ingsw.persistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Serializable snapshot of a full game state.
 */
public class GameSnapshot {
    /** Number of players in the saved game. */
    public int numPlayers;
    /** Current era at save time. */
    public int currentEra;
    /** Current round at save time. */
    public int currentRound;
    /** Index of the active player in the current turn order. */
    public int currentPlayerIndex;
    /** Simple class name of the current game state. */
    public String currentStateName;
    /** Saved player states. */
    public List<PlayerSnapshot> players = new ArrayList<>();
    /** Saved board state. */
    public BoardSnapshot board;
    /** Saved offer track state. */
    public OfferTrackSnapshot offerTrack;
    /** Saved turn order tile state. */
    public TurnOrderSnapshot turnOrderTile;
    /** Nicknames ordered by the current phase turn order. */
    public List<String> roundTurnOrder = new ArrayList<>();
    /** Remaining building deck ids grouped by future era. */
    public List<List<String>> eraBuildingDecks = new ArrayList<>();
    /** Saved action resolution details when the current state requires them. */
    public ActionResolutionSnapshot actionResolutionState;
}
