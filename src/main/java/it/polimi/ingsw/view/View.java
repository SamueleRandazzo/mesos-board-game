package it.polimi.ingsw.view;

import it.polimi.ingsw.network.DTO.OfferTileDTO;
import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.RemoteController;

import java.util.List;

public interface View {
    void showLogin();
    void showLobby(int currentPlayers, int maxPlayers);
    void startGame(RemoteController controller);
    void showError(String message);
    void askMaxPlayers();
    void askTotemPlacement();
    void askCardChoose();
    void showMessage(String message);
    void showPlayersOrder(List<String> playersOrder);
    void retryTotemPlacement();
    void displayOfferTrack(List<OfferTileDTO> tiles);
}
