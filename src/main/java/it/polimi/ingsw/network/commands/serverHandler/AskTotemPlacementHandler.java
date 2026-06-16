package it.polimi.ingsw.network.commands.serverHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;

/**
 * Handler for server requests that ask the active player to place a totem.
 */
public class AskTotemPlacementHandler implements ServerCommandHandler {
    @Override
    public void handle(String[] args, View view, ObjectMapper mapper) {
        try {
            view.askTotemPlacement();
        } catch (Exception e) {
            view.showError("Server data error");
            e.printStackTrace();
        }
    }
}
