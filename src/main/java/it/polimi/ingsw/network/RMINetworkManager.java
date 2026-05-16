package it.polimi.ingsw.network;

import it.polimi.ingsw.client.ClientObserver;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.TribeStatusDTO;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RMINetworkManager extends NetworkManager {
    private Loggable serverStub;
    private final ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
    private GameObserver myObserver;

    @Override
    public void connect(String ip, int port) throws Exception {
        Registry registry = LocateRegistry.getRegistry(ip, port);
        this.serverStub = (Loggable) registry.lookup("Loggable");

        startHeartbeat();
    }

    @Override
    public void login(Color color, String name) throws RemoteException {
        myObserver = new ClientObserver(this.view);
        UnicastRemoteObject.exportObject(myObserver, 0);

        serverStub.login(name, color, myObserver);
    }

    @Override
    public void setController(RemoteController controller) {
        this.controller = controller;
    }

    @Override
    public void setTotalPlayers(int n) throws RemoteException {
        serverStub.setTargetPlayers(n);
    }

    @Override
    protected void handleCardAction(String prefix, int n) throws RemoteException {
        controller.executeCardAction(prefix, n);
    }

    @Override
    public void tileSelection(int tileIndex) throws RemoteException {
        controller.handleTileSelection(tileIndex);
    }

    private void startHeartbeat() {
        heartbeatScheduler.scheduleAtFixedRate(() -> {
            try {
                serverStub.ping();
            } catch (RemoteException e) {
                handleServerDisconnection();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private synchronized void handleServerDisconnection() {
        if (!heartbeatScheduler.isShutdown()) {
            heartbeatScheduler.shutdownNow();
            view.showFatalError("Connection lost. The server is unreachable.");
        }
    }

    @Override
    public void seeGlobalLeaderboard(int targetPlayers) throws RemoteException {
        serverStub.getGlobalLeaderboard(targetPlayers, myObserver);
    }

    @Override
    public void seeOtherPlayerTribe(String nickname) throws RemoteException {
        serverStub.seeOtherPlayerTribe(nickname, myObserver);
    }
}