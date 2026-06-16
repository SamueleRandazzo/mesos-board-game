package it.polimi.ingsw.network.commands.serverHandler;

import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handler for lobby updates after a player joins.
 */
public class PlayerJoinedHandler implements ServerCommandHandler {
    @Override
    public void handle(String[] args, View view, ObjectMapper mapper) {
        String[] counts = args[0].split("/");
        view.showLobby(Integer.parseInt(counts[0]), Integer.parseInt(counts[1]));
    }
}
