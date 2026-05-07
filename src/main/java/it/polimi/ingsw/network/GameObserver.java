package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.OfferTileDTO;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface GameObserver extends Remote {
    void onPlayerJoined(int currentPlayers, int totalNeeded) throws RemoteException;
    void onGameStarted(RemoteController controller, int totalPlayers) throws RemoteException;
    void askMaxPlayers() throws RemoteException;
    void askTotemPlacement() throws RemoteException;
    void onShowError(String error) throws RemoteException;
    void askCardChoose() throws RemoteException;
    void onShowMessage(String message) throws RemoteException;
    void onShowPlayersOrder(List<String> playersOrder) throws RemoteException;
    void onDisplayOfferTrack(List<OfferTileDTO> tiles) throws RemoteException;
    void onShowPlayersInfo(Map<String, Color> playersInfo) throws RemoteException;
}
