package it.polimi.ingsw.model.Interfaces;

public interface GameEventListener {
    void onTotemPlacementTurnChanged(String playerNickname);
    void onTotemPlaced(String playerNickname, int tileIndex);
    void onActionResultTurnChanged(String playerNickname);
}
