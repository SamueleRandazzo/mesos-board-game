package it.polimi.ingsw.network;

import it.polimi.ingsw.client.ClientObserver;
import it.polimi.ingsw.model.Enum.Color;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Handles client-side network operations using Java RMI (Remote Method Invocation).
 * It extends {@link NetworkManager} to forward user interactions directly to the server stub
 * or the remote controller via RMI remote procedure calls, and manages an automatic
 * heartbeat mechanism to detect server disconnections.
 */
public class RMINetworkManager extends NetworkManager {
    /**
     * The remote gateway stub of the RMI server used for registration and system actions.
     */
    private Loggable serverStub;

    /**
     * A single-threaded scheduler used to periodically invoke the ping method on the server
     * to monitor link stability.
     */
    private final ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * The remote observer implementation exported by the client to receive asynchronous
     * callback updates back from the server.
     */
    private GameObserver myObserver;

    /**
     * Connects to the remote RMI Registry at the specified IP address and port, looks up
     * the entry bound to "Loggable", and initiates the background heartbeat scheduler.
     *
     * @param ip   the server IP address hosting the RMI registry
     * @param port the port number of the RMI registry
     * @throws Exception if the registry could not be contacted or the binding name does not exist
     */
    @Override
    public void connect(String ip, int port) throws Exception {
        Registry registry = LocateRegistry.getRegistry(ip, port);
        this.serverStub = (Loggable) registry.lookup("Loggable");

        startHeartbeat();
    }

    /**
     * Instantiates a new remote client observer interface, exports it onto an anonymous
     * ephemeral port, and invokes the server registration procedure.
     *
     * @param color the chosen player color configuration parameter
     * @param name  the requested unique player nickname
     * @throws RemoteException if an export failure or network communication breakdown happens
     */
    @Override
    public void login(Color color, String name) throws RemoteException {
        myObserver = new ClientObserver(this.view);
        UnicastRemoteObject.exportObject(myObserver, 0);

        serverStub.login(name, color, myObserver);
    }

    /**
     * Links the given remote game controller instance to this manager to permit
     * standard gameplay move method dispatches.
     *
     * @param controller the remote match game state controller instance
     */
    @Override
    public void setController(RemoteController controller) {
        this.controller = controller;
    }

    /**
     * Invokes the remote routine to configure the global match player capacity constraint.
     *
     * @param n the target total number of active players required
     * @throws RemoteException if an RMI communication fault is detected
     */
    @Override
    public void setTotalPlayers(int n) throws RemoteException {
        serverStub.setTargetPlayers(n);
    }

    /**
     * Forwards card structural interaction actions directly to the remote controller.
     *
     * @param prefix the command contextual descriptor code prefix
     * @param n      the target card reference index or identifier value
     * @throws RemoteException if an RMI communication fault is detected
     */
    @Override
    protected void handleCardAction(String prefix, int n) throws RemoteException {
        controller.executeCardAction(prefix, n);
    }

    /**
     * Forwards a tile selection choice operation directly to the remote controller.
     *
     * @param tileIndex the zero-based index position reference on the track layout
     * @throws RemoteException if an RMI communication fault is detected
     */
    @Override
    public void tileSelection(int tileIndex) throws RemoteException {
        controller.handleTileSelection(tileIndex);
    }

    /**
     * Schedules a cyclic background task executing every 5 seconds to issue
     * server ping validations. If a RemoteException occurs, it triggers the breakdown route.
     */
    private void startHeartbeat() {
        heartbeatScheduler.scheduleAtFixedRate(() -> {
            try {
                serverStub.ping();
            } catch (RemoteException e) {
                handleServerDisconnection();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * Gracefully tears down the scheduling executor infrastructure during infrastructure faults
     * and notifies the local view layer with a terminal fatal failure prompt block.
     */
    private synchronized void handleServerDisconnection() {
        if (!heartbeatScheduler.isShutdown()) {
            heartbeatScheduler.shutdownNow();
            view.showFatalError("Connection lost. The server is unreachable.");
        }
    }

    /**
     * Requests the historical global registry entries filtered by game setup parameters.
     *
     * @param targetPlayers the match group capacity configuration filter
     * @throws RemoteException if an RMI communication fault is detected
     */
    @Override
    public void seeGlobalLeaderboard(int targetPlayers) throws RemoteException {
        serverStub.getGlobalLeaderboard(targetPlayers, myObserver);
    }

    /**
     * Requests the remote controller to commit active status closure routines
     * for the current player's action turn cycle.
     *
     * @throws Exception if an unexpected execution exception or network fault occurs
     */
    @Override
    public void endTurnRequest() throws Exception {
        controller.handleEndTurnRequest();
    }
}