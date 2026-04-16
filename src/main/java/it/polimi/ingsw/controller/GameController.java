package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.RemoteController;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implementation of the remote controller for the game.
 * This class acts as the mediator between the network layer (client requests)
 * and the game model, translating client actions into model state changes.
 */
public class GameController extends UnicastRemoteObject implements RemoteController {
    private Game game;

    public GameController(Game game) throws RemoteException {
        super();
        this.game = game;
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
}