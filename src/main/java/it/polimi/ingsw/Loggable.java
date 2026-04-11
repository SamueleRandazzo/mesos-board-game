package it.polimi.ingsw;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Loggable extends Remote {
    boolean login(String nickname) throws RemoteException;
    boolean logout(String nickname) throws RemoteException;
}
