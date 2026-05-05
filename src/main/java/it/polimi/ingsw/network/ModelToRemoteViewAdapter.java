package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Interfaces.GameEventListener;
import it.polimi.ingsw.network.DTO.OfferTileDTO;
import java.rmi.RemoteException;
import java.util.*;

public class ModelToRemoteViewAdapter implements GameEventListener {
    private final Map<String, GameObserver> playerObservers;

    public ModelToRemoteViewAdapter(Map<String, GameObserver> playerObservers) {
        this.playerObservers = playerObservers;
    }

    @Override
    public void onTotemPlacementTurnChanged(String playerNickname, List<OfferTileDTO> tiles) {
        GameObserver activeObs = playerObservers.get(playerNickname);
        if (activeObs != null) {
            for (GameObserver o : playerObservers.values()) {
                if (o != activeObs) {
                    try {
                        o.onShowMessage(playerNickname + " is choosing the tile.");
                    } catch (RemoteException e) {
                        System.err.println("Network error with: " + playerNickname);
                    }
                }
            }

            try {
                activeObs.askTotemPlacement(tiles);
            } catch (RemoteException e) {
                System.err.println("Network error with: " + playerNickname);
            }
        }
    }

    @Override
    public void onTotemPlaced(String playerNickname, int tileIndex) {
        GameObserver activeObs = playerObservers.get(playerNickname);
        if (activeObs != null) {
            for (GameObserver o : playerObservers.values()) {
                try {
                    if (o != activeObs)
                        o.onShowMessage(playerNickname + " choose the tile " + tileIndex + ".");
                } catch (RemoteException e) {
                    System.err.println("Network error with " +  playerNickname);
                }
            }
        }
    }

    @Override
    public void onActionResultTurnChanged(String playerNickname) {
        GameObserver activeObs = playerObservers.get(playerNickname);
        if (activeObs != null) {
            for (GameObserver o : playerObservers.values()) {
                if (o != activeObs) {
                    try {
                        o.onShowMessage(playerNickname + " is choosing the cards to pick.");
                    } catch (RemoteException e) {
                        System.err.println("Network error with: " + playerNickname);
                    }
                }
            }

            try {
                activeObs.askCardChoose();
            } catch (RemoteException e) {
                System.err.println("Network error with " +  playerNickname);
            }
        }
    }
}