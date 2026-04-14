package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Enum.Color;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Loggable extends Remote {
    void login(Color color, String nickname, GameObserver obs) throws RemoteException;
    void setTotalPlayers(int num) throws RemoteException;
}
