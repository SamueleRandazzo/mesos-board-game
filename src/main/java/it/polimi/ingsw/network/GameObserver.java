package it.polimi.ingsw.network;

import it.polimi.ingsw.network.DTO.OfferTileDTO;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameObserver extends Remote {
    void onPlayerJoined(int currentPlayers, int totalNeeded) throws RemoteException;
    void onGameStarted(RemoteController controller) throws RemoteException;
    void askMaxPlayers() throws RemoteException;
    void askTotemPlacement(List<OfferTileDTO> tiles) throws RemoteException;
    void onShowError(String error) throws RemoteException;
    void askCardChoose() throws RemoteException;
}
