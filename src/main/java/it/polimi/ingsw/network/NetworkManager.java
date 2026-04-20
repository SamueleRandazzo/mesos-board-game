package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Enum.Color;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkManager {
    private Loggable serverStub;
    private RemoteController controller;
    private final Map<String, RemoteAction> cardActions = new HashMap<>();

    @FunctionalInterface
    private interface RemoteAction {
        void apply(int n) throws RemoteException;
    }

    public NetworkManager() {
        cardActions.put("U", n -> controller.handleUpperCardSelection(n));
        cardActions.put("B", n -> controller.handleLowerCardSelection(n));
        cardActions.put("BU", n -> controller.handleUpperBuildingSelection(n));
        cardActions.put("BB", n -> controller.handleLowerBuildingSelection(n));
    }

    public void connect(String ip, int port) throws Exception {
        Registry registry = LocateRegistry.getRegistry(ip, port);
        this.serverStub = (Loggable) registry.lookup("Loggable");
    }

    public void setController(RemoteController controller) {
        this.controller = controller;
    }

    public void login(Color color, String name, GameObserver obs) throws RemoteException {
        serverStub.login(name, color, obs);
    }

    public void setTotalPlayers(int n) throws RemoteException {
        serverStub.setTargetPlayers(n);
    }

    public void tileSelection(int tileIndex) throws RemoteException {
        controller.handleTileSelection(tileIndex);
    }

    public void cardSelection(String cardPosition) throws RemoteException {
        Pattern pattern = Pattern.compile("^([A-Z]+)(\\d+)$");
        Matcher matcher = pattern.matcher(cardPosition);

        if (matcher.matches()) {
            String prefix = matcher.group(1);
            int n = Integer.parseInt(matcher.group(2));

            RemoteAction action = cardActions.get(prefix);
            if (action != null) {
                action.apply(n);
            } else {
                System.err.println("Tipo di carta non supportato: " + prefix);
            }
        }
    }
}