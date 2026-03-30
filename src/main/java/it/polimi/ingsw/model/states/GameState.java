package it.polimi.ingsw.model.states;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

/**
 * Abstract base class for the Game State Pattern.
 * <p>
 * This class defines the common interface for all game phases in Mesos.
 * It provides default implementations that throw an IllegalStateException,
 * preventing players from executing actions that are not allowed in the current phase.
 * Concrete state classes must override only the methods relevant to their specific phase.
 */
public abstract class GameState {

    /**
     * Handles the placement of a player's totem on the offer track.
     *
     * @param context   the current game context
     * @param player    the player attempting to place the totem
     * @param tileIndex the index of the chosen offer tile
     * @throws IllegalStateException if totem placement is not allowed in this phase
     */
    public void placeTotem(Game context, Player player, int tileIndex) {
        throw new IllegalStateException("Action not allowed: You cannot place a totem in the current game phase.");
    }

    /**
     * Resolves the picking of a tribe card from the upper row.
     *
     * @param context   the current game context
     * @param player    the player attempting to pick the card
     * @param cardIndex the index of the card in the upper row
     * @throws IllegalStateException if picking an upper row card is not allowed in this phase
     */
    public void resolveUpperCardPick(Game context, Player player, int cardIndex) {
        throw new IllegalStateException("Action not allowed: You cannot pick an upper row card now.");

    }

    /**
     * Resolves the picking of a tribe card from the lower row.
     *
     * @param context   the current game context
     * @param player    the player attempting to pick the card
     * @param cardIndex the index of the card in the lower row
     * @throws IllegalStateException if picking a lower row card is not allowed in this phase
     */
    public void resolveLowerCardPick(Game context, Player player, int cardIndex) {
        throw new IllegalStateException("Action not allowed: You cannot pick a lower row card now.");

    }

    /**
     * Ends the current player's turn or concludes a specific action sequence.
     *
     * @param context the current game context
     * @param player  the player attempting to end their turn
     * @throws IllegalStateException if ending the turn is not allowed in this phase
     */
    public void endTurn(Game context, Player player) {
        throw new IllegalStateException("Action not allowed: you cannot end your turn now.");
    }

    /**
     * Resolves the picking of a building card from the upper building row.
     *
     * @param context       the current game context
     * @param player        the player attempting to pick the building
     * @param buildingIndex the index of the building in the upper row
     * @throws IllegalStateException if picking an upper row building is not allowed in this phase
     */
    public void resolveUpperBuildingPick (Game context, Player player, int buildingIndex){
        throw new IllegalStateException("Action not allowed: You cannot pick an upper building card now.");

    }

    /**
     * Resolves the picking of a tribe card from the lower row.
     *
     * @param context   the current game context
     * @param player    the player attempting to pick the card
     * @param BuildingIndex the index of the building in the lower row
     * @throws IllegalStateException if picking a lower row card is not allowed in this phase
     */
    public void resolveLowerBuildingPick (Game context, Player player, int BuildingIndex){
        throw new IllegalStateException("Action not allowed: You cannot pick a lower building card now.");

    }
}

