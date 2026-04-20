package it.polimi.ingsw.network;

import it.polimi.ingsw.exception.CustomException;
import it.polimi.ingsw.model.Enum.Color;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Loggable extends Remote {
    void login(String nickname, Color color, GameObserver obs) throws RemoteException, CustomException.BaseGameException;
    void setTargetPlayers(int num) throws RemoteException, CustomException.BaseGameException;
}
