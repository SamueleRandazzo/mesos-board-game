package it.polimi.ingsw.network.commands.clientHandler;

import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.network.commands.ClientCommandHandler;
import it.polimi.ingsw.server.SocketVirtualView;

/**
 * Handler for the "TILE_SELECT" command received from clients via Socket.
 * Expected format from the client: TILE_SELECT <nickname> <index>
 */
public class TileSelectHandler implements ClientCommandHandler {

    /**
     * Parses the arguments and triggers the tile selection action on the server.
     *
     * @param args       The arguments sent by the client (nickname, index).
     * @param lobby      The game lobby.
     * @param controller The remote controller handling game logic.
     * @param vView      The virtual view to respond to the client.
     */
    @Override
    public void handle(String[] args, Lobby lobby, RemoteController controller, SocketVirtualView vView) {
        // 1. Validate parameter count
        if (args.length < 2) {
            vView.onShowError("Invalid_command_parameters");
            return;
        }

        try {
            // 2. Parse arguments
            // Replace underscores back with spaces if the nickname contains spaces
            String nickname = args[0].replace("_", " ");
            int index = Integer.parseInt(args[1]);

            // 3. Execute the move (The controller will check if it's actually this player's turn)
            controller.handleTileSelection(nickname, index);

        } catch (NumberFormatException e) {
            // Handle the case where the client sends a non-numeric index
            vView.onShowError("Index_must_be_a_number");
            vView.askTotemPlacement();

        } catch (Exception e) {
            // 4. Safe error handling
            System.err.println("[TileSelectHandler] Invalid move attempted by " + args[0] + ": " + e.getMessage());

            // Avoid NullPointerException if e.getMessage() is null
            String errorMsg = e.getMessage() != null ? e.getMessage().replace(" ", "_") : "Invalid_Move";
            vView.onShowError(errorMsg);

            // Re-prompt the user to make a valid choice
            vView.askTotemPlacement();
        }
    }
}