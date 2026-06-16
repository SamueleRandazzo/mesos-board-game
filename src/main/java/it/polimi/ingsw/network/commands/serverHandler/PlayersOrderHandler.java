package it.polimi.ingsw.network.commands.serverHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler for updates to the player order shown by the client.
 */
public class PlayersOrderHandler implements ServerCommandHandler {
    @Override
    public void handle(String[] args, View view, ObjectMapper mapper) {
        String allNames = args[0];

        List<String> playersOrder = Arrays.stream(allNames.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        view.showPlayersOrder(playersOrder);
    }
}
