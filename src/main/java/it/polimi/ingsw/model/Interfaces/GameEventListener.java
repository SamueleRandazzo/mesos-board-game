package it.polimi.ingsw.model.Interfaces;

import it.polimi.ingsw.network.DTO.*;
import java.util.List;

public interface GameEventListener {
    void onTotemPlacementTurnChanged(String playerNickname);
    void onTotemPlaced(String playerNickname, int tileIndex);
    void onActionResultTurnChanged(String playerNickname);
    void onShowOfferTrack(List<OfferTileDTO> tiles);
    void onShowTribe(String playerNickname, TribeStatusDTO tribe);
    void onShowBoard(BoardDTO board);
    void onEventMessage(String playerNickname, String message);
    void onEndGame(LeaderboardDTO leaderboard);
}
