package it.polimi.ingsw.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameObserver extends Remote {
    void onPlayerJoined(int currentPlayers, int totalNeeded) throws RemoteException;
    void onGameStarted(RemoteController controller) throws RemoteException;
    void askMaxPlayers() throws RemoteException;
    void askTotemPlacement() throws RemoteException;
    void onShowError(String error) throws RemoteException;
}
