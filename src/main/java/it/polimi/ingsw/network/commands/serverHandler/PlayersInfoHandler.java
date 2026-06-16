package it.polimi.ingsw.network.commands.serverHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handler for player nickname and color information received from the server.
 */
public class PlayersInfoHandler implements ServerCommandHandler {

    @Override
    public void handle(String[] args, View view, ObjectMapper mapper) {
        Map<String, Color> playersInfo = new LinkedHashMap<>();

        if (args.length == 0 || args[0].isBlank()) {
            view.showPlayersInfo(playersInfo);
            return;
        }

        String[] entries = args[0].split(",");

        for (String entry : entries) {
            String[] parts = entry.split(":");

            if (parts.length != 2) {
                continue;
            }

            String nickname = parts[0];
            Color color = Color.valueOf(parts[1]);

            playersInfo.put(nickname, color);
        }

        view.showPlayersInfo(playersInfo);
    }
}
