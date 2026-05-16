package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.*;
import it.polimi.ingsw.network.RemoteController;
import java.util.List;
import java.util.Map;

public interface View {
    void showLogin();
    void showLobby(int currentPlayers, int maxPlayers);
    void startGame(RemoteController controller, int totalPlayers);
    void showError(String message);
    void askMaxPlayers();
    void askTotemPlacement();
    void askCardChoose();
    void showMessage(String message);
    void showPlayersOrder(List<String> playersOrder);
    void displayOfferTrack(List<OfferTileDTO> tiles);
    void showPlayersInfo(Map<String, Color> playersInfo);
    void displayBoard(BoardDTO board);
    void displayLeaderboard(LeaderboardDTO leaderboard, String globalRank);
    void showFatalError(String error);
    void displayGlobalLeaderboard(GlobalLeaderboardDTO globalLeaderboard);
    void displayTurnOrderTile(List<TurnOrderTileDTO> turnOrderTile);

    /**
     * Displays the updated status of a specific player's tribe.
     *
     * @param nickname the nickname of the player owning the tribe
     * @param tribe    the updated DTO representing the tribe status
     */
    void showTribe(String nickname, TribeStatusDTO tribe);
}
