package it.polimi.ingsw.network.commands.serverHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.DTO.TribeStatusDTO;
import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;

/**
 * Handler responsible for processing the tribe update command received from the server via Socket.
 * <p>
 * This class intercepts the serialized tribe state, deserializes it from JSON format
 * back into a {@link TribeStatusDTO} object, and forwards it to the View for rendering.
 * </p>
 */
public class ShowTribeHandler implements ServerCommandHandler {

    /**
     * Executes the command to display the player tribe.
     *
     * @param args   An array containing the command arguments. The first element (args[0])
     * must be the JSON string representing the serialized TribeStatusDTO.
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

            // Extract the JSON string from the arguments
            String jsonPayload = args[0];

            // Deserialize the JSON into a TribeStatusDTO object
            TribeStatusDTO tribe = mapper.readValue(jsonPayload, new TypeReference<TribeStatusDTO>() {
            });

            // Forward the updated board to the View so it can be printed to the user
            view.showTribe(tribe);

        } catch (Exception e) {
            // If the JSON is malformed or missing, alert the user without crashing the client
            view.showError("Server data error: unable to show the tribe");
        }
    }
}