package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Enum.Color;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Loggable extends Remote {
    void login(String nickname, Color color, GameObserver obs) throws RemoteException;
    void setTargetPlayers(int num) throws RemoteException;
    void ping() throws RemoteException;
    void getGlobalLeaderboard(int targetPlayers, GameObserver observer) throws RemoteException;
    void seeOtherPlayerTribe(String nickname, GameObserver observer) throws RemoteException;
}
