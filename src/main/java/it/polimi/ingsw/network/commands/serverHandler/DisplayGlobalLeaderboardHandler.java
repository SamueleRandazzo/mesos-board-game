package it.polimi.ingsw.network.commands.serverHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.DTO.GlobalLeaderboardDTO;
import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;

/**
 * Handler for global leaderboard updates received from the server.
 */
public class DisplayGlobalLeaderboardHandler implements ServerCommandHandler {

    @Override
    public void handle(String[] args, View view, ObjectMapper mapper) {
        try {
            if (args == null || args.length == 0) return;

            String jsonList = args[0];

            GlobalLeaderboardDTO leaderboard = mapper.readValue(jsonList, GlobalLeaderboardDTO.class);

            view.displayGlobalLeaderboard(leaderboard);
        } catch (Exception e) {
            view.showError("Server data error: unable to load the global leaderboard.");
        }
    }
}
