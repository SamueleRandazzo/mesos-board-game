package it.polimi.ingsw.network.commands.serverHandler;

import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handler for recoverable error messages received from the server.
 */
public class ErrorHandler implements ServerCommandHandler {
    @Override
    public void handle(String[] args, View view, ObjectMapper mapper) {
        String errorMessage = args[0].replace("_", " ");
        String message = errorMessage.contains(": ")
                ? errorMessage.substring(errorMessage.lastIndexOf(": ") + 2)
                : errorMessage;

        view.showError(message);
    }
}
