package it.polimi.ingsw.network.commands.serverHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.DTO.BoardDTO;
import it.polimi.ingsw.network.DTO.LeaderboardDTO;
import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;

/**
 * Handler responsible for processing the leaderboard command received from the server via Socket.
 * <p>
 * This class intercepts the serialized leaderboard state, deserializes it from JSON format
 * back into a {@link LeaderboardDTO} object, and forwards it to the View for rendering.
 * </p>
 */
public class DisplayLeaderboardHandler implements ServerCommandHandler {

    /**
     * Executes the command to display the ending leaderboard.
     *
     * @param args   An array containing the command arguments. The first element (args[0])
     * must be the JSON string representing the serialized LeaderboardDTO.
     * @param view   The active {@link View} instance used to interact with the user interface.
     * @param mapper The Jackson {@link ObjectMapper} used to parse the JSON string.
     */
    @Override
    public void handle(String[] args, View view, ObjectMapper mapper) {
        try {
            // Ensure the arguments are present to prevent IndexOutOfBoundsException
            if (args == null || args.length == 0) {
                return;
            }

            String jsonPayload = args[0];
            String globalRank = args[1].replace("_", " ");

            LeaderboardDTO leaderboard = mapper.readValue(jsonPayload, LeaderboardDTO.class);

            view.displayLeaderboard(leaderboard, globalRank);
        } catch (Exception e) {
            // If the JSON is malformed or missing, alert the user without crashing the client
            view.showError("Server data error: unable to load the leaderboard.");
        }
    }
}