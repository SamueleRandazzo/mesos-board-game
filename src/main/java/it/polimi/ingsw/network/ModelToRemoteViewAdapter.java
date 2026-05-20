package it.polimi.ingsw.network;

import it.polimi.ingsw.database.*;
import it.polimi.ingsw.model.Interfaces.GameEventListener;
import it.polimi.ingsw.network.DTO.*;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Adapter class that implements {@link GameEventListener} to map model domain events
 * into remote network notifications. It iterates over a map of connected players'
 * observers and triggers the appropriate network callbacks to broadcast or unicast updates.
 */
public class ModelToRemoteViewAdapter implements GameEventListener {
    /**
     * Map pairing each player's unique nickname with their respective remote {@link GameObserver}.
     */
    private final Map<String, GameObserver> playerObservers;

    /**
     * Constructs a new ModelToRemoteViewAdapter linked to the active player observers.
     *
     * @param playerObservers a map containing nickname-to-observer pairs for network propagation
     */
    public ModelToRemoteViewAdapter(Map<String, GameObserver> playerObservers) {
        this.playerObservers = playerObservers;
    }

    /**
     * Notifies that the totem placement turn has changed. It asks the active player
     * to place their totem while notifying all other opponents via status messages.
     *
     * @param playerNickname the nickname of the player who must now perform the placement
     */
    @Override
    public void onTotemPlacementTurnChanged(String playerNickname) {
        GameObserver activeObs = playerObservers.get(playerNickname);
        if (activeObs != null) {
            for (GameObserver o : playerObservers.values()) {
                if (o != activeObs) {
                    try {
                        o.onShowMessage(playerNickname + " is choosing the tile.");
                    } catch (RemoteException e) {
                        System.err.println("Network error with: " + playerNickname);
                    }
                }
            }

            try {
                activeObs.askTotemPlacement();
            } catch (RemoteException e) {
                System.err.println("Network error with: " + playerNickname);
            }
        }
    }

    /**
     * Broadcasts the updated content of the offer track to all connected observers.
     *
     * @param tiles the list of OfferTileDTO objects representing the track's current status
     */
    @Override
    public void onShowOfferTrack(List<OfferTileDTO> tiles) {
        for (GameObserver o : playerObservers.values()) {
            try {
                o.onDisplayOfferTrack(tiles);
            } catch (RemoteException e) {
                System.err.println("Network error");
            }
        }
    }

    /**
     * Notifies all passive opponents that a specific player has successfully placed
     * their totem on a given tile index.
     *
     * @param playerNickname the nickname of the player who executed the action
     * @param tileIndex      the index position reference of the chosen tile
     */
    @Override
    public void onTotemPlaced(String playerNickname, int tileIndex) {
        GameObserver activeObs = playerObservers.get(playerNickname);
        if (activeObs != null) {
            for (GameObserver o : playerObservers.values()) {
                try {
                    if (o != activeObs)
                        o.onShowMessage(playerNickname + " choose the tile " + tileIndex + ".");
                } catch (RemoteException e) {
                    System.err.println("Network error with " +  playerNickname);
                }
            }
        }
    }

    /**
     * Notifies that the action phase turn has changed. It prompts the active player
     * to choose cards from the selection track and updates the others with status messages.
     *
     * @param playerNickname the nickname of the active player
     */
    @Override
    public void onActionResultTurnChanged(String playerNickname) {
        GameObserver activeObs = playerObservers.get(playerNickname);
        if (activeObs != null) {
            for (GameObserver o : playerObservers.values()) {
                if (o != activeObs) {
                    try {
                        o.onShowMessage(playerNickname + " is choosing the cards to pick.");
                    } catch (RemoteException e) {
                        System.err.println("Network error with: " + playerNickname);
                    }
                }
            }

            try {
                activeObs.askCardChoose();
            } catch (RemoteException e) {
                System.err.println("Network error with " +  playerNickname);
            }
        }
    }

    /**
     * Receives the updated tribe status from the Model and broadcasts it to ALL connected clients.
     * This allows every player to track the status of opponents' tribes in real time.
     *
     * @param playerNickname the nickname of the player whose tribe has changed
     * @param tribe          the updated TribeStatusDTO
     */
    @Override
    public void onShowTribe(String playerNickname, TribeStatusDTO tribe) {
        for (Map.Entry<String, GameObserver> entry : playerObservers.entrySet()) {
            try {
                entry.getValue().onShowTribe(playerNickname, tribe);
            } catch (RemoteException e) {
                System.err.println("Network error sending tribe of " + playerNickname + " to " + entry.getKey());
            }
        }
    }

    /**
     * Broadcasts the fully updated layout configuration of the game board to all connected players.
     *
     * @param board the complete BoardDTO representation object
     */
    @Override
    public void onShowBoard(BoardDTO board) {
        for (GameObserver o : playerObservers.values()) {
            try {
                o.onDisplayBoard(board);
            } catch (RemoteException e) {
                System.err.println("Network error sending board");
            }
        }
    }

    /**
     * Dispatches a specific system or narrative log context message to a targeted player's view interface.
     *
     * @param playerNickname the recipient player nickname
     * @param message        the dynamic event summary text statement
     */
    @Override
    public void onEventMessage(String playerNickname, String message) {
        GameObserver obs = playerObservers.get(playerNickname);
        if (obs != null) {
            try {
                obs.onShowEventMessage(message);
            } catch (RemoteException e) {
                System.err.println("Network error with: " + playerNickname);
            }
        }
    }

    /**
     * Handles the end game event trigger. It records final match scores into the database tier
     * and multithreads individual final leaderboard dispatches with updated historical global rankings
     * to prevent slow network channels from blocking other users.
     *
     * @param leaderboard the final computed outcome scores metadata bundle descriptor
     */
    @Override
    public void onEndGame(LeaderboardDTO leaderboard) {
        if (DatabaseManager.isAvailable()) {
            MatchDAO.saveFullMatch(leaderboard.getRankings());
        }

        for (Map.Entry<String, GameObserver> entry : playerObservers.entrySet()) {
            String nickname = entry.getKey();
            GameObserver observer = entry.getValue();

            // Use separate threads to prevent RMI blocking calls from delaying Socket notifications
            new Thread(() -> {
                try {
                    String rankMessage = null;
                    if (DatabaseManager.isAvailable()) {
                        int targetPlayers = leaderboard.getRankings().size();
                        rankMessage = "Your global rank is: " + MatchDAO.getRankByTotalPoints(nickname, targetPlayers);
                    }
                    observer.onDisplayLeaderboard(leaderboard, rankMessage);
                } catch (RemoteException e) {
                    System.err.println("Error notifying " + nickname);
                }
            }).start();
        }
    }

    /**
     * Broadcasts the updated positional distribution sequence of turn order tiles
     * to all active observers.
     *
     * @param turnOrderTile the list of current TurnOrderTileDTO objects
     */
    @Override
    public void onDisplayTurnOrderTile(List<TurnOrderTileDTO> turnOrderTile) {
        for (GameObserver o : playerObservers.values()) {
            try {
                o.onDisplayTurnOrderTile(turnOrderTile);
            } catch (RemoteException e) {
                System.err.println("Network error sending turn order tile");
            }
        }
    }

    /**
     * Broadcasts the official game round execution seats sequence lineup array
     * to all registered observer interfaces.
     *
     * @param playersOrder the ordered list of player nicknames mapping the seating sequence
     */
    @Override
    public void onShowPlayersOrder(List<String> playersOrder) {
        for (GameObserver o : playerObservers.values()) {
            try {
                o.onShowPlayersOrder(playersOrder);
            } catch (RemoteException e) {
                System.err.println("Network error sending players order");
            }
        }
    }

    /**
     * Notifies that a player has reached the final resolution check of their turn.
     * It prompts the targeted active player to decide whether to officially conclude
     * their turn actions or invest in a structural building asset purchase.
     *
     * @param playerNickname the nickname of the active player facing the turn resolution choice
     */
    @Override
    public void onManualEndTurnRequest(String playerNickname) {
        GameObserver activeObs = playerObservers.get(playerNickname);
        if (activeObs != null) {
            for (GameObserver o : playerObservers.values()) {
                if (o != activeObs) {
                    try {
                        o.onShowMessage(playerNickname + " is choosing the cards to pick.");
                    } catch (RemoteException e) {
                        System.err.println("Network error with: " + playerNickname);
                    }
                }
            }

            try {
                activeObs.askEndTurnOrBuyBuilding();
            } catch (RemoteException e) {
                System.err.println("Network error with " +  playerNickname);
            }
        }
    }
}