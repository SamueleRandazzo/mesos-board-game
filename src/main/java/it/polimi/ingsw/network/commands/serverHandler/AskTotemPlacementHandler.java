package it.polimi.ingsw.network.commands.serverHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.DTO.OfferTileDTO;
import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;
import java.util.List;

public class AskTotemPlacementHandler implements ServerCommandHandler {
    @Override
    public void handle(String[] args, View view, ObjectMapper mapper) {
        try {
            if (args.length == 0) return;

            String json = args[0];
            List<OfferTileDTO> tiles = mapper.readValue(json, new TypeReference<List<OfferTileDTO>>(){});

            view.askTotemPlacement(tiles);
        } catch (Exception e) {
            view.showError("Server data error");
            e.printStackTrace();
        }
    }
}