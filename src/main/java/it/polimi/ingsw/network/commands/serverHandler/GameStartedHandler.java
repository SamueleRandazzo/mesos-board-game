package it.polimi.ingsw.network.commands.serverHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;

public class GameStartedHandler implements ServerCommandHandler {
    @Override
    public void handle(String[] args, View view, ObjectMapper mapper) {
        int totalPlayers = Integer.parseInt(args[0]);
        view.startGame(null, totalPlayers);
    }
}
