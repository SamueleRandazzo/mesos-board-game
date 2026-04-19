package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Enum.Color;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class NetworkManager {
    private Loggable serverStub;
    private RemoteController controller;

    public void connect(String ip, int port) throws Exception {
        Registry registry = LocateRegistry.getRegistry(ip, port);
        this.serverStub = (Loggable) registry.lookup("Loggable");
    }

    public void setController(RemoteController controller) {
        this.controller = controller;
    }

    public void login(Color color, String name, GameObserver obs) throws RemoteException {
        serverStub.login(color, name, obs);
    }

    public void setTotalPlayers(int n) throws RemoteException {
        serverStub.setTotalPlayers(n);
    }

    public void tileSelection(int tileIndex) throws RemoteException {
        controller.handleTileSelection(tileIndex);
    }
}