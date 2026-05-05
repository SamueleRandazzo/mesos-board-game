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
            out.println("SET_PLAYERS_ERROR Insert_a_valid_number!");
        } catch (Exception e) {
            out.println("SET_PLAYERS_ERROR " + e.getMessage().replace(" ", "_"));
        }
    }
}