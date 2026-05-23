package it.polimi.ingsw.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The remote controller interface exposing the primary game action gateway over networks.
 * <p>
 * This interface defines the transactional contract for client-to-server communications during
 * an active gameplay session. It acts as the distributed Controller within the MVC architecture,
 * receiving raw interaction intents from remote clients and
 * forwarding validated operations down to the domain model engine.
 * </p>
 * <p>
 * All methods declare throwing {@link RemoteException} to intercept and handle potential network
 * layer disruptions or disconnection states during remote method invocations.
 * </p>
 *
 * @see Remote
 * @see RemoteException
 * @see GameObserver
 */
public interface RemoteController extends Remote {

    /**
     * Dispatches a player's intent to place their totem marker onto a specific board tile.
     *
     * @param tileIndex the sequential layout index of the target tile on the central grid matrix
     * @throws RemoteException if a network communication or transport routing failure occurs
     */
    void handleTileSelection(int tileIndex) throws RemoteException;

    /**
     * Dispatches an explicit request from a player to commit their actions and officially
     * conclude their active turn phase.
     *
     * @throws RemoteException if a network communication or transport routing failure occurs
     */
    void handleEndTurnRequest() throws RemoteException;

    /**
     * Dispatches a choice action indicating the drafting or purchasing of a card from
     * the upper market row layout.
     *
     * @param pos the zero-based grid position index tracking the card within the upper row
     * @throws RemoteException if a network communication or transport routing failure occurs
     */
    void handleUpperCardSelection(int pos) throws RemoteException;

    /**
     * Dispatches a choice action indicating the drafting or purchasing of a card from
     * the lower market row layout.
     *
     * @param pos the zero-based grid position index tracking the card within the lower row
     * @throws RemoteException if a network communication or transport routing failure occurs
     */
    void handleLowerCardSelection(int pos) throws RemoteException;

    /**
     * Dispatches a building construction request referencing an asset available in
     * the upper blueprint market row.
     *
     * @param pos the zero-based layout position index of the target building blueprint
     * @throws RemoteException if a network communication or transport routing failure occurs
     */
    void handleUpperBuildingSelection(int pos) throws RemoteException;

    /**
     * Dispatches a building construction request referencing an asset available in
     * the lower blueprint market row.
     *
     * @param pos the zero-based layout position index of the target building blueprint
     * @throws RemoteException if a network communication or transport routing failure occurs
     */
    void handleLowerBuildingSelection(int pos) throws RemoteException;

    /**
     * Executes a generalized, dynamically parsed card action context using structured prefix tags
     * and destination indices.
     * <p>
     * This method serves as a polymorphic routing endpoint, mapping pre-processed alphanumeric commands
     * forwarded by components like the {@link NetworkManager} into explicit game logic branches.
     * </p>
     *
     * @param prefix the isolated alphabetical section key identifying the target market or board zone
     * @param n      the numerical index tracking the exact element selection within that zone
     * @throws RemoteException if a network communication or transport routing failure occurs
     */
    void executeCardAction(String prefix, int n) throws RemoteException;
}