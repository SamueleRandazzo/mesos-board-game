package it.polimi.ingsw.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteController extends Remote {
    void handleTileSelection(int tileIndex) throws RemoteException;
    void handleUpperCardSelection(int pos) throws RemoteException;
    void handleLowerCardSelection(int pos) throws RemoteException;
    void handleUpperBuildingSelection(int pos) throws RemoteException;
    void handleLowerBuildingSelection(int pos) throws RemoteException;
    void executeCardAction(String prefix, int n) throws RemoteException;
}