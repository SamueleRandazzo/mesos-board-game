package it.polimi.ingsw.network.commands.clientHandler;

import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.network.commands.ClientCommandHandler;
import it.polimi.ingsw.server.SocketVirtualView;

public class TileSelectHandler implements ClientCommandHandler {
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