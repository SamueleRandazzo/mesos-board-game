package it.polimi.ingsw.network.commands.clientHandler;

import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.network.commands.ClientCommandHandler;

import java.io.PrintWriter;

public class SetPlayersHandler implements ClientCommandHandler {
    @Override
    public void handle(String[] args, Lobby lobby, RemoteController controller, PrintWriter out) {
        try {
            int n = Integer.parseInt(args[0]);
            lobby.setTargetPlayers(n);
        } catch (NumberFormatException e) {
            System.err.println("Invalid move attempted: " + e.getMessage());

            out.println("ERROR Insert_a_valid_number!");
            out.println("ASK_MAX_PLAYERS");
        } catch (Exception e) {
            System.err.println("Invalid move attempted: " + e.getMessage());

            out.println("ERROR " + e.getMessage().replace(" ", "_"));
            out.println("ASK_MAX_PLAYERS");
        }
    }
}