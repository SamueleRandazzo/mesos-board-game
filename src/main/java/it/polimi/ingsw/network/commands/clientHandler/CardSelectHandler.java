package it.polimi.ingsw.network.commands.clientHandler;

import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.network.commands.ClientCommandHandler;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.server.SocketVirtualView;

/**
 * Handler for the "CARD_SELECT" command received from clients via Socket.
 * Expected format from the client: CARD_SELECT <nickname> <prefix> <index>
 */
public class CardSelectHandler implements ClientCommandHandler {

    /**
     * Parses the arguments and triggers the card selection action on the server.
     *
     * @param args       The arguments sent by the client (nickname, prefix, index).
     * @param lobby      The game lobby.
     * @param controller The remote controller handling game logic.
     * @param vView      The virtual view to respond to the client.
     */
    @Override
    public void handle(String[] args, Lobby lobby, RemoteController controller, SocketVirtualView vView) {
        // 1. Validate parameter count
        if (args.length < 3) {
            vView.onShowError("Invalid_command_parameters");
            return;
        }

        try {
            // 2. Parse arguments
            // Replace underscores back with spaces if the nickname contains spaces
            String nickname = args[0].replace("_", " ");
            String prefix = args[1].toUpperCase();
            int index = Integer.parseInt(args[2]);

            // 3. Execute the move (The controller will check if it's actually this player's turn)
            controller.executeCardAction(nickname, prefix, index);

        } catch (NumberFormatException e) {
            // Handle the case where the client sends a non-numeric index
            vView.onShowError("Index_must_be_a_number");
            vView.askCardChoose();

        } catch (Exception e) {
            // 4. Safe error handling
            // Avoid NullPointerException if e.getMessage() is null
            String errorMsg = e.getMessage() != null ? e.getMessage().replace(" ", "_") : "Action_Failed";

            // Log the error on the server console for security/debugging
            System.err.println("[CardSelectHandler] Invalid move attempted by " + args[0] + ": " + e.getMessage());

            // 5. Send error to client and unblock them
            vView.onShowError(errorMsg);
            vView.askCardChoose();
        }
    }
}