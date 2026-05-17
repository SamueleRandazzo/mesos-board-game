package it.polimi.ingsw.persistence;

import java.util.ArrayList;
import java.util.List;

public class GameSnapshot {
    public int numPlayers;
    public int currentEra;
    public int currentRound;
    public int currentPlayerIndex;
    public String currentStateName;
    public List<PlayerSnapshot> players = new ArrayList<>();
    public BoardSnapshot board;
    public OfferTrackSnapshot offerTrack;
    public TurnOrderSnapshot turnOrderTile;
    public List<String> roundTurnOrder = new ArrayList<>();
    public List<List<String>> eraBuildingDecks = new ArrayList<>();
    public ActionResolutionSnapshot actionResolutionState;
}
