package it.polimi.ingsw.client;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.BoardDTO;
import it.polimi.ingsw.network.DTO.OfferTileDTO;
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
    public void onShowError(String error) throws RemoteException {
        view.showError(error);
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

    @Override
    public void onDisplayBoard(BoardDTO board) throws RemoteException {
        view.displayBoard(board);
    }
}
