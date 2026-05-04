package it.polimi.ingsw.network;

import it.polimi.ingsw.client.ClientObserver;
import it.polimi.ingsw.model.Enum.Color;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMINetworkManager extends NetworkManager {
    private Loggable serverStub;

    @Override
    public void connect(String ip, int port) throws Exception {
        Registry registry = LocateRegistry.getRegistry(ip, port);
        this.serverStub = (Loggable) registry.lookup("Loggable");
    }

    @Override
    public void login(Color color, String name) throws RemoteException {
        GameObserver myObserver = new ClientObserver(this.view);
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
}