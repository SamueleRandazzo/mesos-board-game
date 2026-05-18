package it.polimi.ingsw.client;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.*;
import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.view.View;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class ClientObserver implements GameObserver {
    private View view;

    public ClientObserver(View view) throws RemoteException {
        this.view = view;
    }

    @Override
    public void onPlayerJoined(int current, int total) throws RemoteException {
        view.showLobby(current, total);
    }

    @Override
    public void onGameStarted(RemoteController controller, int totalPlayers) throws RemoteException {
        view.startGame(controller, totalPlayers);
    }

    @Override
    public void askMaxPlayers() throws RemoteException {
        view.askMaxPlayers();
    }

    @Override
    public void askTotemPlacement() throws RemoteException {
        view.askTotemPlacement();
    }

    @Override
    public void onDisplayOfferTrack(List<OfferTileDTO> tiles) {
        view.displayOfferTrack(tiles);
    }

    @Override
    public void askCardChoose() throws RemoteException {
        view.askCardChoose();
    }

    @Override
    public void onShowMessage(String message) throws RemoteException {
        view.showMessage(message);
    }

    @Override
    public void onShowPlayersOrder(List<String> playersOrder) throws RemoteException {
        view.showPlayersOrder(playersOrder);
    }

    @Override
    public void onShowPlayersInfo(Map<String, Color> playersInfo) throws RemoteException {
        view.showPlayersInfo(playersInfo);
    }

    /**
     * Receives the tribe status update from the server via RMI and forwards it to the view.
     *
     * @param nickname the nickname of the player owning the tribe
     * @param tribe    the updated TribeStatusDTO object
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void onShowTribe(String nickname, TribeStatusDTO tribe) throws RemoteException {
        view.showTribe(nickname, tribe);
    }

    @Override
    public void onDisplayBoard(BoardDTO board) throws RemoteException {
        view.displayBoard(board);
    }

    @Override
    public void onDisplayLeaderboard(LeaderboardDTO leaderboard, String globalRank) throws RemoteException {
        view.displayLeaderboard(leaderboard, globalRank);
    }

    @Override
    public void ping() throws RemoteException {
        // method to check if client is alive
    }

    @Override
    public void onShowFatalError(String error) throws RemoteException {
        view.showFatalError(error);
    }

    @Override
    public void onDisplayGlobalLeaderboard(GlobalLeaderboardDTO leaderboard) throws RemoteException {
        view.displayGlobalLeaderboard(leaderboard);
    }

    @Override
    public void onDisplayTurnOrderTile(List<TurnOrderTileDTO> turnOrderTile) throws RemoteException {
        view.displayTurnOrderTile(turnOrderTile);
    }

    @Override
    public void onShowEventMessage(String message) throws RemoteException {
        view.showEventMessage(message);
    }

    @Override
    public void askEndTurnOrBuyBuilding() throws RemoteException {
        view.askEndTurnOrBuyBuilding();
    }
}
