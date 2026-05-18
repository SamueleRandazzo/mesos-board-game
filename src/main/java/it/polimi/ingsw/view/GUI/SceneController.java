package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.*;
import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.view.GUIView;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.exception.CustomException.cleanRemoteException;

public abstract class SceneController {
    protected NetworkManager network;
    protected GUIView view;

    public void setNetwork(NetworkManager network) { this.network = network; }
    public void setView(GUIView view) { this.view = view; }
    public void updateLobby(int current, int total) {}
    public void displayOfferTrack(List<OfferTileDTO> tiles, int total) {}
    public void askTotemPlacement() {};
    public void displayChoosableCards() {}
    public void showErrorMessage(String msg) {}
    public void showNotification(String msg) {}
    public void updatePlayersOrder(List<String> order) {}
    public void setTotalPlayers(int totalPlayers) {}
    public void setPlayersInfo(Map<String, Color> playersInfo) {}
    public void displayBoard(BoardDTO board) {};
    public void showTribe(String playerNickname, TribeStatusDTO tribe) {};
    public void displayLeaderboard(LeaderboardDTO leaderboard, String globalRankMessage) {};
    public void displayTurnOrderTile(List<TurnOrderTileDTO> turnOrderTile) {};
    public void displayGlobalLeaderboard(GlobalLeaderboardDTO globalLeaderboard) {};
    public void showToast(String msg) {}
    public void showEndTurn() {}

    String handleNetworkError(Exception e) {
        if (e instanceof RemoteException) {
            return cleanRemoteException((RemoteException) e);
        } else {
            return e.getMessage().contains(": ")
                    ? e.getMessage().substring(e.getMessage().lastIndexOf(": ") + 2)
                    : e.getMessage();
        }
    }
}
