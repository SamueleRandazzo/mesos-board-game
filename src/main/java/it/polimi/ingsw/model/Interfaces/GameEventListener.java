package it.polimi.ingsw.model.Interfaces;

import it.polimi.ingsw.network.DTO.OfferTileDTO;
import java.util.List;

public interface GameEventListener {
    void onTotemPlacementTurnChanged(String playerNickname, List<OfferTileDTO> tiles);
    void onTotemPlaced(String playerNickname, int tileIndex);
    void onActionResultTurnChanged(String playerNickname);
}
