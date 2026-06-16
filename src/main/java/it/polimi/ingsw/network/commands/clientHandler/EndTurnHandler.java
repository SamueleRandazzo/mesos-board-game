package it.polimi.ingsw.network.commands.clientHandler;

import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.network.commands.ClientCommandHandler;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.server.SocketVirtualView;

/**
 * Command handler responsible for processing turn termination requests sent by a socket client.
 * It forwards the end-turn request to the controller to advance the game state.
 */
public class EndTurnHandler implements ClientCommandHandler {

    /**
     * Handles the end-turn command by requesting the controller to process the turn termination.
     * <p>
     * If an exception occurs during the execution of this action, the error is caught
     * and logged to the server's standard error console.
     * </p>
     *
     * @param args       the command arguments (not explicitly used by this handler).
     * @param lobby      the current game lobby session.
     * @param controller the remote controller used to handle the game logic and turn rotation.
     * @param vView      the virtual view representing the network communication with the client.
     */
    @Override
    public void handle(String[] args, Lobby lobby, RemoteController controller, SocketVirtualView vView) {
        try {
            controller.handleEndTurnRequest();
        } catch (Exception e) {
            System.err.println("Invalid move attempted: " + e.getMessage());
        }
    }
}