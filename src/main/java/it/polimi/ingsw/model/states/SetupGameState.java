package it.polimi.ingsw.model.states;

import it.polimi.ingsw.model.Game;

/**
 * Initial state of the game. Represents the lobby/setup phase.
 * All gameplay actions are blocked.
 */
public class SetupGameState extends GameState{

    /**
     * Called by the Controller/Server when all players are connected
     * and the game is ready to start.
     */
    public void startGame(Game context){
        context.initializeGame();
        context.setState(new TotemPlacementState());
    }
}
