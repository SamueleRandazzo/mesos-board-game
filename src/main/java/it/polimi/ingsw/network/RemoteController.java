package it.polimi.ingsw.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteController extends Remote {
    void handleUpperCardSelection(int pos) throws RemoteException;
    void handleLowerCardSelection(int pos) throws RemoteException;
    void handleUpperBuildingSelection(int pos) throws RemoteException;
    void handleLowerBuildingSelection(int pos) throws RemoteException;

    /**
     * Executes an action ensuring the identity of the caller.
     * * @param nickname the nickname of the player sending the command.
     * @param prefix   the action prefix (e.g., "U", "B").
     * @param n        the index.
     */
    void executeCardAction(String nickname, String prefix, int n) throws RemoteException;

    /**
     * Handles the selection of a tile on the offer track.
     * @param nickname the player nickname sending the command.
     * @param tileIndex the index of the chosen tile.
     */
    void handleTileSelection(String nickname, int tileIndex) throws RemoteException;
}