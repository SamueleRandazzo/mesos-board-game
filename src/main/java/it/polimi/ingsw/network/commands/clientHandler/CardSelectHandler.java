package it.polimi.ingsw.network.commands.clientHandler;

import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.network.commands.ClientCommandHandler;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.server.SocketVirtualView;

/**
 * Command handler responsible for processing card selection actions sent by a socket client.
 * It parses the necessary selection arguments and forwards the action to the controller.
 */
public class CardSelectHandler implements ClientCommandHandler {

    /**
     * Handles the card selection command.
     * <p>
     * The method expects at least two arguments in the {@code args} array:
     * <ul>
     * <li>{@code args[0]}: The card identifier prefix (e.g., card type or action prefix).</li>
     * <li>{@code args[1]}: The index or numerical identifier of the selected card.</li>
     * </ul>
     * </p>
     * If an exception occurs during parsing or execution, the error is logged, a formatted
     * error message is sent back to the client, and the client is prompted again to prevent
     * the interface from hanging.
     *
     * @param args       the command arguments containing the card prefix and index.
     * @param lobby      the current game lobby session.
     * @param controller the remote controller used to execute the game logic action.
     * @param vView      the virtual view representing the network communication with the client.
     */
    @Override
    public void handle(String[] args, Lobby lobby, RemoteController controller, SocketVirtualView vView) {
        if (args.length < 2) return;

        try {
            String prefix = args[0].toUpperCase();
            int n = Integer.parseInt(args[1]);

            // Execute the move
            controller.executeCardAction(prefix, n);

        } catch (Exception e) {
            // Log the error on the server console
            System.err.println("Invalid move attempted: " + e.getMessage());

            // 1. Send the error message to the client (replace spaces to avoid breaking args)
            String errorMsg = e.getMessage() != null ? e.getMessage().replace(" ", "_") : "Invalid_Move";
            vView.onShowError(errorMsg);

            // 2. CRUCIAL: Unblock the client by re-asking for a card choice!
            vView.askCardChoose();
        }
    }
}