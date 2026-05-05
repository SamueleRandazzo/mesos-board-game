package it.polimi.ingsw.network.commands.serverHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;

public class TileSelectErrorHandler implements ServerCommandHandler {
    @Override
    public void handle(String[] args, View view, ObjectMapper mapper) {
        String errorMessage = args[0].replace("_", " ");
        String message = errorMessage.contains(": ")
                ? errorMessage.substring(errorMessage.lastIndexOf(": ") + 2)
                : errorMessage;

        view.showError(message + ". Choose another tile.");

        view.retryTotemPlacement();
    }
}