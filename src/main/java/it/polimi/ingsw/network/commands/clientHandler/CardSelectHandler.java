package it.polimi.ingsw.network.commands.clientHandler;

import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.network.commands.ClientCommandHandler;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.server.SocketVirtualView;

public class CardSelectHandler implements ClientCommandHandler {
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