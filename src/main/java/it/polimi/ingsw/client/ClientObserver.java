package it.polimi.ingsw.client;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.*;
import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.view.View;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of the {@link GameObserver} interface on the client side.
 * Actively registered via RMI callback, this stub receives live event updates and action
 * requests pushed by the server game loop, delegating the visual presentation layer tasks
 * directly to the active {@link View} instance.
 */
public class ClientObserver implements GameObserver {
    /**
     * The active user interface view layer instance.
     */
    private View view;

    /**
     * Constructs a ClientObserver instance linked to a target display view interface.
     *
     * @param view the user interface {@link View} reference used to render updates
     * @throws RemoteException if an RMI network export or link communication error occurs
     */
    public ClientObserver(View view) throws RemoteException {
        this.view = view;
    }

    /**
     * Notifies that a new player has registered into the lobby slot container,
     * updating the participant matchmaking occupancy metrics.
     *
     * @param current the current total number of joined players inside the lobby
     * @param total   the exact configuration target capacity limit for the match
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void onPlayerJoined(int current, int total) throws RemoteException {
        view.showLobby(current, total);
    }

    /**
     * Notifies that the lobby setup has successfully concluded and triggers the official
     * match transition sequence, forwarding the actionable game controller reference wrapper.
     *
     * @param controller   the {@link RemoteController} RMI stub to execute game moves
     * @param totalPlayers the total count of participants in the current match session
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void onGameStarted(RemoteController controller, int totalPlayers) throws RemoteException {
        view.startGame(controller, totalPlayers);
    }

    /**
     * Pushes an interaction prompt asking the match creator or host player to input
     * the definitive target size capacity bounds for this game lobby.
     *
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void askMaxPlayers() throws RemoteException {
        view.askMaxPlayers();
    }

    /**
     * Pushes an interaction prompt asking the active player to choose an initial grid coordinate
     * target to place their starting totem piece down on the game board map layout.
     *
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void askTotemPlacement() throws RemoteException {
        view.askTotemPlacement();
    }

    /**
     * Pushes a refresh event updating the client layout view state about
     * the collection of tile entities currently displayed on the main offer track row.
     *
     * @param tiles a list of {@link OfferTileDTO} data transfer objects reflecting the track assets
     */
    @Override
    public void onDisplayOfferTrack(List<OfferTileDTO> tiles) {
        view.displayOfferTrack(tiles);
    }

    /**
     * Pushes an interaction prompt notifying the active player that they must select
     * a card asset choice option from the available common boards or tracks.
     *
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void askCardChoose() throws RemoteException {
        view.askCardChoose();
    }

    /**
     * Forwards a standard text status message log summary sent by the server engine
     * to be displayed inside the user console or status message tray.
     *
     * @param message the contextual text message string content received from the server
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void onShowMessage(String message) throws RemoteException {
        view.showMessage(message);
    }

    /**
     * Forwards the official computed turn sequence schedule layout listing player order positions.
     *
     * @param playersOrder a list of nicknames sorted according to active turn resolution priority
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void onShowPlayersOrder(List<String> playersOrder) throws RemoteException {
        view.showPlayersOrder(playersOrder);
    }

    /**
     * Delivers a lookup registry dictionary binding each joined profile nickname
     * to their chosen or assigned token marker color indicator.
     *
     * @param playersInfo a mapping collection between player nicknames and their designated {@link Color}
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void onShowPlayersInfo(Map<String, Color> playersInfo) throws RemoteException {
        view.showPlayersInfo(playersInfo);
    }

    /**
     * Receives the tribe status update from the server via RMI and forwards it to the view.
     *
     * @param nickname the nickname of the player owning the tribe
     * @param tribe    the updated TribeStatusDTO object
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void onShowTribe(String nickname, TribeStatusDTO tribe) throws RemoteException {
        view.showTribe(nickname, tribe);
    }

    /**
     * Refreshes the local client view representation of the central common board map and resource layout spaces.
     *
     * @param board the complete {@link BoardDTO} state snapshot package containing grid layout metrics
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void onDisplayBoard(BoardDTO board) throws RemoteException {
        view.displayBoard(board);
    }

    /**
     * Displays a partial or current intermediate match endgame standings panel listing positions.
     *
     * @param leaderboard the detailed scores and rank placements stored inside a {@link LeaderboardDTO} wrapper
     * @param globalRank  a textual summary description of the scores outcome standings
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void onDisplayLeaderboard(LeaderboardDTO leaderboard, String globalRank) throws RemoteException {
        view.displayLeaderboard(leaderboard, globalRank);
    }

    /**
     * Heartbeat verification mechanism endpoint invoked remotely by the server supervisor engine
     * to continuously assess if this callback observer bridge connection link remains alive.
     *
     * @throws RemoteException if a network breakdown happens, signaling client unavailability
     */
    @Override
    public void ping() throws RemoteException {
        // method to check if client is alive
    }

    /**
     * Shuts down or aborts local views screens layouts by showing a critical, unrecoverable network error notification panel.
     *
     * @param error the fatal error summary statement message explaining the network failure
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void onShowFatalError(String error) throws RemoteException {
        view.showFatalError(error);
    }

    /**
     * Displays the final global end-of-game absolute leaderboard rankings sheet package.
     *
     * @param leaderboard the terminal score values stored within a {@link GlobalLeaderboardDTO} package object
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void onDisplayGlobalLeaderboard(GlobalLeaderboardDTO leaderboard) throws RemoteException {
        view.displayGlobalLeaderboard(leaderboard);
    }

    /**
     * Delivers an updated listing data snapshot mapping the state of all available action turn priority tiles.
     *
     * @param turnOrderTile a list containing individual {@link TurnOrderTileDTO} data record objects
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void onDisplayTurnOrderTile(List<TurnOrderTileDTO> turnOrderTile) throws RemoteException {
        view.displayTurnOrderTile(turnOrderTile);
    }

    /**
     * Forwards an informative gameplay historical log announcement alert row text payload to the user view container.
     *
     * @param message the detailed descriptive logging phrase message outlining the specific action event
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void onShowEventMessage(String message) throws RemoteException {
        view.showEventMessage(message);
    }

    /**
     * Pushes an interaction fork prompt to the active client player, asking whether they want to officially conclude
     * their turn phase or trigger an additional structural building card purchase sequence instead.
     *
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void askEndTurnOrBuyBuilding() throws RemoteException {
        view.askEndTurnOrBuyBuilding();
    }
}