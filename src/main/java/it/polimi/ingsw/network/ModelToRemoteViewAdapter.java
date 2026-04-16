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
        GameObserver obs = playerObservers.get(playerNickname);
        if (obs != null) {
            try {
                obs.askTotemPlacement(tiles);
            } catch (RemoteException e) {
                System.err.println("Network error with " +  playerNickname);
            }
        }
    }

    @Override
    public void onTotemPlaced(String playerNickname, int tileIndex) {

    }

    @Override
    public void onActionResultTurnChanged(String playerNickname) {

    }
}