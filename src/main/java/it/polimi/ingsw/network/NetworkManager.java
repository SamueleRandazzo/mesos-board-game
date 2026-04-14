package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Enum.Color;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class NetworkManager {
    private Loggable serverStub;       // Per il login
    private RemoteController controller; // Per le mosse di gioco (arriva dopo)

    public void connect(String ip, int port) throws Exception {
        Registry registry = LocateRegistry.getRegistry(ip, port);
        this.serverStub = (Loggable) registry.lookup("Loggable");
    }

    public void login(Color color, String name, GameObserver obs) throws RemoteException {
        serverStub.login(color, name, obs);
    }

    public void setTotalPlayers(int n) throws RemoteException {
        serverStub.setTotalPlayers(n);
    }
}