package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.*;
import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.view.GUIView;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.exception.CustomException.cleanRemoteException;

/**
 * Base controller for JavaFX scenes.
 */
public abstract class SceneController {
    /** Network manager used by scene controllers to send commands. */
    protected NetworkManager network;
    /** Owning GUI view used to coordinate scene-level state. */
    protected GUIView view;

    /**
     * Injects the network manager used by this scene controller.
     *
     * @param network network manager shared by the GUI view
     */
    public void setNetwork(NetworkManager network) { this.network = network; }

    /**
     * Injects the owning GUI view.
     *
     * @param view GUI view that owns this controller
     */
    public void setView(GUIView view) { this.view = view; }

    /**
     * Updates lobby counters in scenes that display lobby state.
     *
     * @param current current number of connected players
     * @param total maximum number of players for the match
     */
    public void updateLobby(int current, int total) {}

    /**
     * Displays the offer track in scenes that render the game board.
     *
     * @param tiles offer tiles to render
     * @param total maximum number of players for the match
     */
    public void displayOfferTrack(List<OfferTileDTO> tiles, int total) {}

    /**
     * Enables totem placement in scenes that support it.
     */
    public void askTotemPlacement() {};

    /**
     * Enables card selection in scenes that support it.
     */
    public void displayChoosableCards() {}

    /**
     * Shows an error message in the current scene.
     *
     * @param msg message to display
     */
    public void showErrorMessage(String msg) {}

    /**
     * Shows a non-blocking notification in the current scene.
     *
     * @param msg message to display
     */
    public void showNotification(String msg) {}

    /**
     * Updates the displayed player order.
     *
     * @param order nicknames ordered by turn priority
     */
    public void updatePlayersOrder(List<String> order) {}

    /**
     * Stores the maximum number of players for scenes that need layout sizing.
     *
     * @param totalPlayers maximum number of players for the match
     */
    public void setTotalPlayers(int totalPlayers) {}

    /**
     * Updates known player nicknames and colors.
     *
     * @param playersInfo map from nickname to player color
     */
    public void setPlayersInfo(Map<String, Color> playersInfo) {}

    /**
     * Displays the current board state.
     *
     * @param board board snapshot to render
     */
    public void displayBoard(BoardDTO board) {};

    /**
     * Displays a player's tribe status.
     *
     * @param playerNickname nickname of the player whose tribe is shown
     * @param tribe tribe status snapshot
     */
    public void showTribe(String playerNickname, TribeStatusDTO tribe) {};

    /**
     * Displays the match leaderboard.
     *
     * @param leaderboard leaderboard snapshot to render
     * @param globalRankMessage global ranking message associated with the player
     */
    public void displayLeaderboard(LeaderboardDTO leaderboard, String globalRankMessage) {};

    /**
     * Displays the current turn order tile.
     *
     * @param turnOrderTile turn order slots to render
     */
    public void displayTurnOrderTile(List<TurnOrderTileDTO> turnOrderTile) {};

    /**
     * Displays the global leaderboard.
     *
     * @param globalLeaderboard global leaderboard snapshot to render
     */
    public void displayGlobalLeaderboard(GlobalLeaderboardDTO globalLeaderboard) {};

    /**
     * Shows a temporary toast message.
     *
     * @param msg message to display
     */
    public void showToast(String msg) {}

    /**
     * Shows the end-turn prompt in scenes that support it.
     */
    public void showEndTurn() {}

    String handleNetworkError(Exception e) {
        if (e instanceof RemoteException) {
            return cleanRemoteException((RemoteException) e);
        } else {
            return e.getMessage().contains(": ")
                    ? e.getMessage().substring(e.getMessage().lastIndexOf(": ") + 2)
                    : e.getMessage();
        }
    }
}
