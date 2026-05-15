package it.polimi.ingsw.network;

import it.polimi.ingsw.database.*;
import it.polimi.ingsw.model.Interfaces.GameEventListener;
import it.polimi.ingsw.network.DTO.*;
import java.rmi.RemoteException;
import java.util.*;

public class ModelToRemoteViewAdapter implements GameEventListener {
    private final Map<String, GameObserver> playerObservers;

    public ModelToRemoteViewAdapter(Map<String, GameObserver> playerObservers) {
        this.playerObservers = playerObservers;
    }

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

    @Override
    public void onShowTribe(String playerNickname, TribeStatusDTO tribe) {
        GameObserver activeObs = playerObservers.get(playerNickname);
        if (activeObs != null) {
            try {
                activeObs.onShowTribe(tribe);
            } catch (RemoteException e) {
                System.err.println("Network error with: " + playerNickname);
            }
        }
    }

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

    @Override
    public void onEventMessage(String playerNickname, String message) {
        GameObserver obs = playerObservers.get(playerNickname);
        if (obs != null) {
            try {
                obs.onShowMessage(message);
            } catch (RemoteException e) {
                System.err.println("Network error with: " + playerNickname);
            }
        }
    }

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
}