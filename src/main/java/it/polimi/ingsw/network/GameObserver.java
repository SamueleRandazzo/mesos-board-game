package it.polimi.ingsw.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameObserver extends Remote {
    void onTimerUpdate(int secondsLeft) throws RemoteException;
    void onPlayerJoined(int currentPlayers, int totalNeeded) throws RemoteException;
    void onGameStarted(RemoteController controller) throws RemoteException;
}
