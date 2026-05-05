package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.DTO.OfferTileDTO;
import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.view.GUIView;

import java.util.List;

public abstract class SceneController {
    protected NetworkManager network;
    protected GUIView view;

    public void setNetwork(NetworkManager network) { this.network = network; }
    public void setView(GUIView view) { this.view = view; }
    public void updateLobby(int current, int total) {}
    public void displayOfferTrack(List<OfferTileDTO> tiles) {}
    public void displayChoosableCards() {}
    public void showErrorMessage(String msg) {}
    public void showNotification(String msg) {}
    public void updatePlayersOrder(List<String> order) {}
}
