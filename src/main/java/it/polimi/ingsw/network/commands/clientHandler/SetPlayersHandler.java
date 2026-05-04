package it.polimi.ingsw.network.commands.clientHandler;

import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.network.commands.ClientCommandHandler;

public class SetPlayersHandler implements ClientCommandHandler {
    @Override
    public void handle(String[] args, Lobby lobby, RemoteController controller) {
        try {
            int n = Integer.parseInt(args[0]);
            lobby.setTargetPlayers(n);
        } catch (Exception e) {
            System.err.println("Error setting target players: " + e.getMessage());
        }
    }
}