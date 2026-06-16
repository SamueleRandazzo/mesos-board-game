package it.polimi.ingsw.network.commands.serverHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.DTO.TurnOrderTileDTO;
import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;

import java.util.List;

/**
 * Handler for turn order tile updates received from the server.
 */
public class DisplayTurnOrderTileHandler implements ServerCommandHandler {

    /**
     * Deserializes the received JSON payload into DTO slots and forwards them to the View.
     */
    @Override
    public void handle(String[] args, View view, ObjectMapper mapper) {
        try {
            String json = args[0];
            List<TurnOrderTileDTO> dto = mapper.readValue(json, new TypeReference<List<TurnOrderTileDTO>>() {});
            view.displayTurnOrderTile(dto);
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

}
