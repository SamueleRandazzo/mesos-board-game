package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * The remote client-side listener interface implementing the Observer pattern over network channels.
 * <p>
 * This interface defines the callback infrastructure exposed by a client connection. It allows the
 * server to push live game state models (DTOs), narrative logs, structural errors, and interactive
 * phase prompts down to individual participants or structural view components asynchronously.
 * </p>
 * <p>
 * All methods declare throwing {@link RemoteException} to safely catch and handle transport or
 * link drops across the remote socket architecture.
 * </p>
 *
 * @see Remote
 * @see RemoteException
 * @see RemoteController
 */
public interface GameObserver extends Remote {

    /**
     * Notifies the client that a new participant has successfully registered inside the game room.
     *
     * @param currentPlayers the updated number of players currently present in the room setup
     * @param totalNeeded    the required seating capacity needed to seal the lobby and start initialization
     * @throws RemoteException if a communication failure occurs during network routing
     */
    void onPlayerJoined(int currentPlayers, int totalNeeded) throws RemoteException;

    /**
     * Signals that the match has legally begun, providing the player with the session's execution controller.
     *
     * @param controller   the active {@link RemoteController} stub used to send interaction intents back to the server
     * @param totalPlayers the definitive count of participants engaged in this session
     * @throws RemoteException if a communication failure occurs during network routing
     */
    void onGameStarted(RemoteController controller, int totalPlayers) throws RemoteException;

    /**
     * Prompts the host player connection to define and commit the total size layout of the upcoming session.
     *
     * @throws RemoteException if a communication failure occurs during network routing
     */
    void askMaxPlayers() throws RemoteException;

    /**
     * Prompts the client view to unlock input captures for positioning their totem token on the central grid.
     *
     * @throws RemoteException if a communication failure occurs during network routing
     */
    void askTotemPlacement() throws RemoteException;

    /**
     * Prompts the client view to select, draft, or buy a card from the active market row configurations.
     *
     * @throws RemoteException if a communication failure occurs during network routing
     */
    void askCardChoose() throws RemoteException;

    /**
     * Pushes a basic diagnostic text, state log entry, or descriptive string update to be drawn on the presentation interface.
     *
     * @param message the string message description
     * @throws RemoteException if a communication failure occurs during network routing
     */
    void onShowMessage(String message) throws RemoteException;

    /**
     * Synchronizes the sequential seat rotation and active turn execution sequence across all client nodes.
     *
     * @param playersOrder an ordered {@link List} of player profile names matching the sequential execution path
     * @throws RemoteException if a communication failure occurs during network routing
     */
    void onShowPlayersOrder(List<String> playersOrder) throws RemoteException;

    /**
     * Broadcasts the current structural display state of the market offer track layout.
     *
     * @param tiles a {@link List} of {@link OfferTileDTO} units representing the card market matrix
     * @throws RemoteException if a communication failure occurs during network routing
     */
    void onDisplayOfferTrack(List<OfferTileDTO> tiles) throws RemoteException;

    /**
     * Maps and synchronizes the immutable player identity profiles to their specific gaming color registrations.
     *
     * @param playersInfo a {@link Map} pairing unique player nicknames to their assigned token {@link Color}
     * @throws RemoteException if a communication failure occurs during network routing
     */
    void onShowPlayersInfo(Map<String, Color> playersInfo) throws RemoteException;

    /**
     * Broadcasts a comprehensive structural snapshot of the central game board layout.
     *
     * @param board the fully populated {@link BoardDTO} tracking grid positions and track parameters
     * @throws RemoteException if a communication failure occurs during network routing
     */
    void onDisplayBoard(BoardDTO board) throws RemoteException;

    /**
     * Dispatches the localized session scoreboard at game completion along with a string capture of global rankings.
     *
     * @param leaderboard the session tiebreaker ranking matrix via {@link LeaderboardDTO}
     * @param globalRank  a formatted string tracking historical performance data or global standings
     * @throws RemoteException if a communication failure occurs during network routing
     */
    void onDisplayLeaderboard(LeaderboardDTO leaderboard, String globalRank) throws RemoteException;

    /**
     * Simple low-overhead heartbeat frame triggered by the server to verify remote reference liveliness
     * and clean up dead connections.
     *
     * @throws RemoteException if the client is unreachable or the connection is broken
     */
    void ping() throws RemoteException;

    /**
     * Broadcasts an unrecoverable server breakdown or transaction crash that forces the immediate teardown of the view interface.
     *
     * @param error the literal description or stack footprint of the fatal crash
     * @throws RemoteException if a communication failure occurs during network routing
     */
    void onShowFatalError(String error) throws RemoteException;

    /**
     * Broadcasts the immutable global historical leaderboard data payload at game conclusion.
     *
     * @param globalLeaderboard the compiled ranking matrix via {@link GlobalLeaderboardDTO}
     * @throws RemoteException if a communication failure occurs during network routing
     */
    void onDisplayGlobalLeaderboard(GlobalLeaderboardDTO globalLeaderboard) throws RemoteException;

    /**
     * Pushes the structural layout and dynamic occupation of the turn priority tracking nodes.
     *
     * @param turnOrderTile a {@link List} of {@link TurnOrderTileDTO} data structures capturing the priority track state
     * @throws RemoteException if a communication failure occurs during network routing
     */
    void onDisplayTurnOrderTile(List<TurnOrderTileDTO> turnOrderTile) throws RemoteException;

    /**
     * Dispatches a specific text message describing an event card execution or resolution sequence.
     *
     * @param message the literal event description or penalty narrative string
     * @throws RemoteException if a communication failure occurs during network routing
     */
    void onShowEventMessage(String message) throws RemoteException;

    /**
     * Prompts the player to decide whether to explicitly finalize their active turn or proceed with construction operations.
     *
     * @throws RemoteException if a communication failure occurs during network routing
     */
    void askEndTurnOrBuyBuilding() throws RemoteException;

    /**
     * Notifies the client about the updated status of a specific player's tribe.
     *
     * @param nickname the nickname of the player owning the tribe
     * @param tribe    the updated DTO representing the tribe status
     * @throws RemoteException if a network error occurs during the RMI call
     */
    void onShowTribe(String nickname, TribeStatusDTO tribe) throws RemoteException;
}