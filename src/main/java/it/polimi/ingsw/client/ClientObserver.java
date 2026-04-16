package it.polimi.ingsw.client;

import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.view.View;
import java.rmi.RemoteException;

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
    public void onGameStarted(RemoteController controller) throws RemoteException {
        view.startGame(controller);
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
    public void onShowError(String error) throws RemoteException {
        view.showError(error);
    }
}
