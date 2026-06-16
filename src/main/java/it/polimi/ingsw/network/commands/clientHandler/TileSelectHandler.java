package it.polimi.ingsw.network.commands.clientHandler;

import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.network.commands.ClientCommandHandler;
import it.polimi.ingsw.server.SocketVirtualView;

/**
 * Command handler responsible for processing tile selection requests sent by a socket client.
 * It parses the selected tile index and forwards the placement/selection action to the controller.
 */
public class TileSelectHandler implements ClientCommandHandler {

    /**
     * Handles the tile selection command.
     * <p>
     * The method expects at least one argument in the {@code args} array:
     * <ul>
     * <li>{@code args[0]}: An integer string representing the index of the selected board tile.</li>
     * </ul>
     * </p>
     * If parsing fails or the controller rejects the selection, the exception is caught,
     * an error message is sent back to the client, and the view prompts the client again
     * for a totem/tile placement to prevent the interaction from blocking.
     *
     * @param args       the command arguments containing the tile index.
     * @param lobby      the current game lobby session.
     * @param controller the remote controller used to execute the tile selection logic.
     * @param vView      the virtual view representing the network communication with the client.
     */
    @Override
    public void handle(String[] args, Lobby lobby, RemoteController controller, SocketVirtualView vView) {
        try {
            int index = Integer.parseInt(args[0]);
            controller.handleTileSelection(index);
        } catch (Exception e) {
            System.err.println("Invalid move attempted: " + e.getMessage());

            String errorMsg = e.getMessage() != null ? e.getMessage().replace(" ", "_") : "Invalid_Move";
            vView.onShowError(errorMsg);

            vView.askTotemPlacement();
        }
    }
}