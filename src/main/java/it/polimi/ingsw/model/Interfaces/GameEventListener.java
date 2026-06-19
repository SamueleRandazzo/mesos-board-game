package it.polimi.ingsw.model.Interfaces;

import it.polimi.ingsw.network.DTO.*;
import java.util.List;

/**
 * Defines the contract for an asynchronous game event listener.
 * <p>
 * This interface acts as the core listener (Observer) in the game architecture. It dispatches
 * state updates, turn transitions, action results, and UI synchronization events from the domain
 * model down to the presentation/network layer (View/Controller).
 * </p>
 */
public interface GameEventListener {

    /**
     * Fired when the active turn for placing a totem on the board shifts to a different player.
     *
     * @param playerNickname the unique nickname of the player who must now place their totem
     */
    void onTotemPlacementTurnChanged(String playerNickname);

    /**
     * Fired immediately after a player has successfully positioned their totem on a board tile.
     *
     * @param playerNickname the unique nickname of the player who performed the placement
     * @param tileIndex      the grid or list index of the tile where the totem was anchored
     */
    void onTotemPlaced(String playerNickname, int tileIndex);

    /**
     * Fired when the active turn for resolving action results shifts to a different player.
     *
     * @param playerNickname the unique nickname of the player who is now resolving their action phase
     */
    void onActionResultTurnChanged(String playerNickname);

    /**
     * Broadcasts the current, updated state of the offer track containing available market tiles.
     *
     * @param tiles a {@link List} of lightweight {@link OfferTileDTO} items representing the market layout
     */
    void onShowOfferTrack(List<OfferTileDTO> tiles);

    /**
     * Broadcasts the visual configuration and occupancy state of the turn order tiles.
     *
     * @param turnOrderTile a {@link List} of {@link TurnOrderTileDTO} instances representing the tracking slots
     */
    void onDisplayTurnOrderTile(List<TurnOrderTileDTO> turnOrderTile);

    /**
     * Pushes a comprehensive snapshot of a specific player's personal tribe tableau.
     *
     * @param playerNickname the unique nickname of the player who owns the updated tribe
     * @param tribe          the data transfer object containing character columns, buildings, and active perks
     */
    void onShowTribe(String playerNickname, TribeStatusDTO tribe);

    /**
     * Broadcasts a complete visual snapshot of the main central game board layout.
     *
     * @param board the composite data transfer object capturing the current board state
     */
    void onShowBoard(BoardDTO board);

    /**
     * Dispatches a specific narrative, operational, or error text message tied to a player context.
     *
     * @param playerNickname the nickname of the player related to the event message context
     * @param message        the literal description or event log text
     */
    void onEventMessage(String playerNickname, String message);

    /**
     * Triggers the end-game routine and distributes final scoring ranking metrics.
     *
     * @param leaderboard the final sorted performance matrix containing scores and tiebreaker resolutions
     */
    void onEndGame(LeaderboardDTO leaderboard);

    /**
     * Broadcasts the current operational seat sequence or round rotation execution order of the players.
     *
     * @param playersOrder an ordered {@link List} of nickname strings matching the sequential turn execution
     */
    void onShowPlayersOrder(List<String> playersOrder);

    /**
     * Notifies that a player has explicitly requested or is required to manually commit and conclude their current turn.
     *
     * @param playerNickname the unique nickname of the player requesting the turn finalization
     */
    void onManualEndTurnRequest(String playerNickname);
}