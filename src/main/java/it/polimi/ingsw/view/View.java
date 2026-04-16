package it.polimi.ingsw.view;

import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.RemoteController;

public interface View {
    void setObserver(GameObserver observer);
    void showLogin();
    void showLobby(int currentPlayers, int maxPlayers);
    void startGame(RemoteController controller);
    void showError(String message);
    void askMaxPlayers();
    void askTotemPlacement();
}
