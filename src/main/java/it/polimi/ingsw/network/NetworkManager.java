package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.view.View;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class NetworkManager {
    protected RemoteController controller;
    protected final Map<String, RemoteAction> cardActions = new HashMap<>();
    protected View view;

    @FunctionalInterface
    protected interface RemoteAction {
        void apply(int n) throws RemoteException;
    }

    public NetworkManager() {
        cardActions.put("U", n -> controller.handleUpperCardSelection(n));
        cardActions.put("B", n -> controller.handleLowerCardSelection(n));
        cardActions.put("BU", n -> controller.handleUpperBuildingSelection(n));
        cardActions.put("BB", n -> controller.handleLowerBuildingSelection(n));
    }

    public void setView(View view) {
        this.view = view;
    }

    public abstract void connect(String ip, int port) throws Exception;
    public abstract void login(Color color, String name) throws Exception;
    public abstract void setTotalPlayers(int n) throws Exception;
    public abstract void tileSelection(int tileIndex) throws Exception;

    public void setController(RemoteController controller) {

    }

    public void cardSelection(String cardPosition) throws Exception {
        Pattern pattern = Pattern.compile("^([A-Z]+)(\\d+)$");
        Matcher matcher = pattern.matcher(cardPosition);

        if (matcher.matches()) {
            String prefix = matcher.group(1);
            int n = Integer.parseInt(matcher.group(2));

            handleCardAction(prefix, n);
        }
    }

    protected abstract void handleCardAction(String prefix, int n) throws Exception;
}