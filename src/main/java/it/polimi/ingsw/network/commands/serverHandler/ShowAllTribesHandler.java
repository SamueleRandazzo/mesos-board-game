package it.polimi.ingsw.network.commands.serverHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.DTO.AllTribesStatusDTO;
import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;

/**
 * Handler responsible for processing the global tribes update command received from the server.
 * <p>
 * Deserializes the JSON format back into an {@link AllTribesStatusDTO} object,
 * and forwards it to the View for global caching and rendering.
 * </p>
 */
public class ShowAllTribesHandler implements ServerCommandHandler {

    @Override
    public void handle(String[] args, View view, ObjectMapper mapper) {
        try {
            if (args == null || args.length == 0) {
                return;
            }

            // Extract the JSON string from the arguments
            String jsonPayload = args[0];

            // Deserialize the JSON into the global DTO
            AllTribesStatusDTO allTribes = mapper.readValue(jsonPayload, AllTribesStatusDTO.class);

            // Forward to the View (CLI or GUI) to update the global cache
            view.showAllTribes(allTribes);

        } catch (Exception e) {
            view.showError("Server data error: unable to load the global tribes data.");
            System.err.println("ShowAllTribesHandler Error: " + e.getMessage());
        }
    }
}