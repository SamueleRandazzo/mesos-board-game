package it.polimi.ingsw.network.commands.clientHandler;

import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.network.commands.ClientCommandHandler;
import it.polimi.ingsw.server.Lobby;
import java.rmi.RemoteException;

public class CardSelectHandler implements ClientCommandHandler {
    @Override
    public void handle(String[] args, Lobby lobby, RemoteController controller) {
        if (args.length < 2) return;

        try {
            String prefix = args[0];
            int n = Integer.parseInt(args[1]);

            controller.executeCardAction(prefix, n);
        } catch (RemoteException | RuntimeException e) {
            System.err.println("Error handling tile selection: " + e.getMessage());
        }
    }
}