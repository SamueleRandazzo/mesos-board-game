package it.polimi.ingsw.network.commands.clientHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.DTO.BoardDTO;
import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;

/**
 * Handler responsible for processing the board update command received from the server via Socket.
 * <p>
 * This class intercepts the serialized board state, deserializes it from JSON format
 * back into a {@link BoardDTO} object, and forwards it to the View for rendering.
 * </p>
 */
public class DisplayBoardHandler implements ServerCommandHandler {

    /**
     * Executes the command to display the game board.
     *
     * @param args   An array containing the command arguments. The first element (args[0])
     * must be the JSON string representing the serialized BoardDTO.
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

            // Deserialize the JSON into a BoardDTO object
            BoardDTO board = mapper.readValue(jsonPayload, BoardDTO.class);

            // Forward the updated board to the View so it can be printed to the user
            view.displayBoard(board);

        } catch (Exception e) {
            // If the JSON is malformed or missing, alert the user without crashing the client
            view.showError("Server data error: unable to load the game board.");
            e.printStackTrace();
        }
    }
}