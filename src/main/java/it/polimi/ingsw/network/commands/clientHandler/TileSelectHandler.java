package it.polimi.ingsw.network.commands.clientHandler;

import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.network.commands.ClientCommandHandler;
import java.rmi.RemoteException;

public class TileSelectHandler implements ClientCommandHandler {
    @Override
    public void handle(String[] args, Lobby lobby, RemoteController controller) {
        try {
            int index = Integer.parseInt(args[0]);
            controller.handleTileSelection(index);
        } catch (RemoteException | RuntimeException e) {
            System.err.println("Error handling tile selection: " + e.getMessage());
        }
    }
}