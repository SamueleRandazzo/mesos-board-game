package it.polimi.ingsw.model.Interfaces;

import it.polimi.ingsw.network.DTO.OfferTileDTO;
import it.polimi.ingsw.network.DTO.TribeStatusDTO;

import java.util.List;

public interface GameEventListener {
    void onTotemPlacementTurnChanged(String playerNickname);
    void onTotemPlaced(String playerNickname, int tileIndex);
    void onActionResultTurnChanged(String playerNickname);
    void onShowOfferTrack(List<OfferTileDTO> tiles);
    void onCardChosen();
    void onChooseOtherCards();
    void onShowTribe(String playerNickname, TribeStatusDTO tribe);
}
