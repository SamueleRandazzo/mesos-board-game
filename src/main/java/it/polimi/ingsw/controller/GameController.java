package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.RemoteController;
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
        super();
        this.game = game;

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



    @Override
    public void handleTileSelection(String nickname, int tileIndex) throws RemoteException {
        // 1. SECURITY CHECK: Validate that the caller is the current active player
        String activePlayerNickname = game.getCurrentActivePlayer().getNickname();

        if (!activePlayerNickname.equals(nickname)) {
            System.err.println("[Security] Tile selection rejected: " + nickname +
                    " attempted to play, but it is " + activePlayerNickname + "'s turn.");
            throw new RemoteException("It is not your turn to place a totem!");
        }

        // 2. LOGIC EXECUTION: Delegate to the Game model using the existing wrapper method
        try {
            game.placePlayerTotem(tileIndex);
            System.out.println("[Action] Player " + nickname + " placed totem on tile " + tileIndex);

        } catch (Exception e) {
            // Catch model-level IllegalStateExceptions (e.g., "Invalid tile index", "already taken")
            throw new RemoteException("Tile selection failed: " + e.getMessage());
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes a card-related action requested by a client.
     * This method performs a security check to ensure that the player sending the command
     * is the currently active player in the game state.
     *
     * @param nickname the nickname of the player requesting the action.
     * @param prefix   the type of action/row (e.g., "U" for Upper, "B" for Bottom).
     * @param n        the index of the card/slot within the specified row.
     * @throws RemoteException if it is not the player's turn or if the action fails.
     */
    @Override
    public void executeCardAction(String nickname, String prefix, int n) throws RemoteException {
        // 1. SECURITY CHECK: Verify identity against the Game Model's active player
        String activePlayerNickname = game.getCurrentActivePlayer().getNickname();

        if (!activePlayerNickname.equals(nickname)) {
            System.err.println("[Security] Rejected action from " + nickname +
                    ". Current active player is: " + activePlayerNickname);
            throw new RemoteException("It is not your turn! Current active player: " + activePlayerNickname);
        }

        // 2. LOGIC EXECUTION: Delegate the validated action to the model
        try {
            // Using the existing prefix-based action map in your GameController
            RemoteAction action = cardActions.get(prefix.toUpperCase());

            if (action == null) {
                throw new RemoteException("Invalid action prefix: " + prefix);
            }

            // Execute the specific pick logic (resolveUpperCardPlayerPick, etc.)
            action.apply(n);

            System.out.println("[Action] Player " + nickname + " executed " + prefix + n);

        } catch (Exception e) {
            // Catch model-level IllegalStateExceptions and wrap them for the network
            throw new RemoteException("Action failed: " + e.getMessage());
        }
    }
}