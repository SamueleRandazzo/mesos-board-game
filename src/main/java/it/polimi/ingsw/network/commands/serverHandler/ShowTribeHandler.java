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
     * Handles the SHOW_TRIBE command received from the server via socket.
     * Extracts the player's nickname and deserializes the JSON payload into a TribeStatusDTO,
     * then forwards both to the view.
     *
     * @param args   the command arguments, where args[0] is the nickname and args[1] is the JSON string
     * @param view   the active view instance to be updated
     * @param mapper the Jackson ObjectMapper used to parse the JSON string
     */
    @Override
    public void handle(String[] args, View view, ObjectMapper mapper) {
        try {
            if (args == null || args.length < 2) {
                return;
            }

            String nickname = args[0];
            String jsonPayload = args[1];

            TribeStatusDTO tribe = mapper.readValue(jsonPayload, new com.fasterxml.jackson.core.type.TypeReference<TribeStatusDTO>() {});

            view.showTribe(nickname, tribe);
        } catch (Exception e) {
            view.showError("Server data error: unable to show the tribe");
        }
    }
}