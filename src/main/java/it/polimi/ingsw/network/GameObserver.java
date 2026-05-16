package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface GameObserver extends Remote {
    void onPlayerJoined(int currentPlayers, int totalNeeded) throws RemoteException;
    void onGameStarted(RemoteController controller, int totalPlayers) throws RemoteException;
    void askMaxPlayers() throws RemoteException;
    void askTotemPlacement() throws RemoteException;
    void askCardChoose() throws RemoteException;
    void onShowMessage(String message) throws RemoteException;
    void onShowPlayersOrder(List<String> playersOrder) throws RemoteException;
    void onDisplayOfferTrack(List<OfferTileDTO> tiles) throws RemoteException;
    void onShowPlayersInfo(Map<String, Color> playersInfo) throws RemoteException;
    void onDisplayBoard(BoardDTO board) throws RemoteException;
    void onDisplayLeaderboard(LeaderboardDTO leaderboard, String globalRank) throws RemoteException;
    void ping() throws RemoteException;
    void onShowFatalError(String error) throws RemoteException;
    void onDisplayGlobalLeaderboard(GlobalLeaderboardDTO globalLeaderboard) throws RemoteException;
    void onDisplayTurnOrderTile(List<TurnOrderTileDTO> turnOrderTile) throws RemoteException;
    void onShowEventMessage(String message) throws RemoteException;

    /**
     * Notifies the client about the updated status of a specific player's tribe.
     *
     * @param nickname the nickname of the player owning the tribe
     * @param tribe    the updated DTO representing the tribe status
     * @throws RemoteException if a network error occurs during the RMI call
     */
    void onShowTribe(String nickname, TribeStatusDTO tribe) throws RemoteException;
}
