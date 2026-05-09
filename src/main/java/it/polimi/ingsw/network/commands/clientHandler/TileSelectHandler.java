package it.polimi.ingsw.network.commands.clientHandler;

import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.network.commands.ClientCommandHandler;
import java.io.PrintWriter;

public class TileSelectHandler implements ClientCommandHandler {
    @Override
    public void handle(String[] args, Lobby lobby, RemoteController controller, PrintWriter out) {
        try {
            int index = Integer.parseInt(args[0]);
            controller.handleTileSelection(index);
        } catch (Exception e) {
            System.err.println("Invalid move attempted: " + e.getMessage());

            String errorMsg = e.getMessage() != null ? e.getMessage().replace(" ", "_") : "Invalid_Move";
            out.println("ERROR " + errorMsg);

            out.println("ASK_TOTEM_PLACEMENT");

            //out.println("TILE_SELECT_ERROR " + e.getMessage().replace(" ", "_"));
        }
    }
}