package it.polimi.ingsw.network.commands.serverHandler;

import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handler for server requests that ask the host to choose the lobby size.
 */
public class AskMaxPlayersHandler implements ServerCommandHandler {
    @Override
    public void handle(String[] args, View view, ObjectMapper mapper) {
        view.askMaxPlayers();
    }
}
