package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.persistence.PersistenceManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the remote controller for the game.
 * This class acts as the mediator between the network layer (client requests)
 * and the game model, translating client actions into model state changes.
 */
public class GameController extends UnicastRemoteObject implements RemoteController {
    private Game game;
    private final PersistenceManager persistenceManager;
    private final Map<String, RemoteAction> cardActions = new HashMap<>();

    @FunctionalInterface
    protected interface RemoteAction {
        /**
         * Applies the action with the given parameter.
         *
         * @param n the index or position parameter for the game logic.
         * @throws RemoteException if a network-related error occurs during execution.
         */
        void apply(int n) throws RemoteException;
    }

    /**
     * Constructs a new GameController and links it to the provided game model.
     * Initializes the internal mapping of action prefixes to their respective methods.
     *
     * @param game the game model instance this controller will manipulate.
     * @throws RemoteException if there is an error during the export of the remote object.
     */
    public GameController(Game game) throws RemoteException {
        this(game, null);
    }

    public GameController(Game game, PersistenceManager persistenceManager) throws RemoteException {
        super();
        this.game = game;
        this.persistenceManager = persistenceManager;

        initializeCardActions();
    }

    /**
     * Populates the {@code cardActions} map with command prefixes and their corresponding
     * method references. This centralizes the command-to-logic mapping on the server side,
     * allowing both RMI and Socket clients to use a unified command protocol.
     */
    private void initializeCardActions() {
        this.cardActions.put("T", this::handleUpperCardSelection);
        this.cardActions.put("L", this::handleLowerCardSelection);
        this.cardActions.put("B", this::handleUpperBuildingSelection);
        this.cardActions.put("G", this::handleLowerBuildingSelection);
    }

    /**
     * Executes a card action based on the prefix mapping.
     */
    @Override
    public void executeCardAction(String prefix, int n) throws RemoteException {
        RemoteAction action = cardActions.get(prefix);
        if (action != null) {
            try {
                action.apply(n);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Unknown card prefix: " + prefix);
        }
    }


    /**
     * Handles the player's request to place a totem on a specific board tile.
     * @param tileIndex the index of the chosen tile on the board.
     * @throws RemoteException if a network error occurs during the remote call.
     * @throws RuntimeException if the model logic encounters an unrecoverable error.
     */
    @Override
    public void handleTileSelection(int tileIndex) throws RemoteException {
        try {
            game.placePlayerTotem(tileIndex);
            persistAfterChange();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Processes the player's selection of a card from the upper deck/market.
     * @param pos the position or index of the selected card.
     * @throws RemoteException if a network error occurs during the remote call.
     * @throws RuntimeException if the model logic encounters an unrecoverable error.
     */
    @Override
    public void handleUpperCardSelection(int pos) throws RemoteException {
        try {
            game.resolveUpperCardPlayerPick(pos);
            persistAfterChange();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Processes the player's selection of a card from the lower deck/market.
     * @param pos the position or index of the selected card.
     * @throws RemoteException if a network error occurs during the remote call.
     * @throws RuntimeException if the model logic encounters an unrecoverable error.
     */
    @Override
    public void handleLowerCardSelection(int pos) throws RemoteException {
        try {
            game.resolveLowerCardPlayerPick(pos);
            persistAfterChange();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the selection of a building located in the upper section of the player's area or board.
     * @param pos the position of the selected building.
     * @throws RemoteException if a network error occurs during the remote call.
     * @throws RuntimeException if the model logic encounters an unrecoverable error.
     */
    @Override
    public void handleUpperBuildingSelection(int pos) throws RemoteException {
        try {
            game.resolveUpperBuildingPlayerPick(pos);
            persistAfterChange();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the selection of a building located in the lower section of the player's area or board.
     * @param pos the position of the selected building.
     * @throws RemoteException if a network error occurs during the remote call.
     * @throws RuntimeException if the model logic encounters an unrecoverable error.
     */
    @Override
    public void handleLowerBuildingSelection(int pos) throws RemoteException {
        try {
            game.resolveLowerBuildingPlayerPick(pos);
            persistAfterChange();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Processes a request to end the current player's turn and proceed with game logic
     * (e.g., checking for victory conditions, rotating turns).
     * @throws RemoteException if a network error occurs during the remote call.
     * @throws RuntimeException if the model logic encounters an unrecoverable error.
     */
    @Override
    public void handleEndTurnRequest() throws RemoteException {
        try {
            game.resolveEndTurn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void persistAfterChange() {
        if (persistenceManager == null) {
            return;
        }

        if ("EndGameState".equals(game.getCurrentStateName())) {
            persistenceManager.deleteSave();
        } else {
            persistenceManager.saveGame(game);
        }
    }
}
