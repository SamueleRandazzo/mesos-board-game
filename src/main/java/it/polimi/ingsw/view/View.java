package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.*;
import it.polimi.ingsw.network.RemoteController;
import java.util.List;
import java.util.Map;

/**
 * The main Boundary interface representing the presentation layer in the MVC pattern.
 * <p>
 * This interface decouples the network processing logic and domain rules from concrete
 * user interface implementations (such as a Command Line Interface or a Graphical User Interface).
 * It defines the programmatic contract required to render state representations, display narrative
 * logs, flash structural errors, and open input capture contexts for active participants.
 * </p>
 *
 * @see it.polimi.ingsw.network.NetworkManager
 * @see it.polimi.ingsw.network.GameObserver
 */
public interface View {

    /**
     * Initializes and displays the authentication layout, prompting the user to provide
     * their profile credentials and token identification options.
     */
    void showLogin();

    /**
     * Displays the pre-game staging room interface, tracking intermediate registration checkpoints.
     *
     * @param currentPlayers the actual number of players currently waiting inside the room
     * @param maxPlayers     the definitive seat capacity threshold required to trigger match initialization
     */
    void showLobby(int currentPlayers, int maxPlayers);

    /**
     * Finalizes the pre-game setup phase, binds the functional remote coordinator, and switches
     * the view workspace to the active match layout.
     *
     * @param controller   the active {@link RemoteController} instance used to pass user commands back to the server
     * @param totalPlayers the definitive count of participants engaged in this session
     */
    void startGame(RemoteController controller, int totalPlayers);

    /**
     * Renders a localized operational warning, command rejection note, or protocol rule violation
     * without compromising the running view state.
     *
     * @param message a textual description of the encountered problem
     */
    void showError(String message);

    /**
     * Unlocks input controls to allow the host player to submit the total participant capacity
     * configuration for this upcoming session.
     */
    void askMaxPlayers();

    /**
     * Unlocks input controls prompting the active player to choose and submit structural coordinates
     * to anchor their totem marker onto the board grid.
     */
    void askTotemPlacement();

    /**
     * Unlocks input components prompting the active player to choose and draft a card from
     * the open market rows.
     */
    void askCardChoose();

    /**
     * Renders a generic narrative log entry, match update ticker, or informational system message
     * onto the display console.
     *
     * @param message the string containing the update text
     */
    void showMessage(String message);

    /**
     * Updates and visualizes the sequential seating rotation mapping out the active turn execution path.
     *
     * @param playersOrder an ordered {@link List} of player nicknames representing the seating sequence
     */
    void showPlayersOrder(List<String> playersOrder);

    /**
     * Updates and draws the grid layout, allocations, and bonus listings of the market offer selection track.
     *
     * @param tiles a {@link List} of {@link OfferTileDTO} elements capturing the selection row state
     */
    void displayOfferTrack(List<OfferTileDTO> tiles);

    /**
     * Sets up and displays the registration bindings pairing individual player identities
     * to their assigned component {@link Color}.
     *
     * @param playersInfo a {@link Map} pairing unique player nicknames to their assigned token color constant
     */
    void showPlayersInfo(Map<String, Color> playersInfo);

    /**
     * Updates and renders a comprehensive structural snapshot layout of the central game board tracks and matrices.
     *
     * @param board the fully packed {@link BoardDTO} tracking active board data matrices
     */
    void displayBoard(BoardDTO board);

    /**
     * Displays the final session scoreboard standings alongside a formatted overview of historical player data records.
     *
     * @param leaderboard the local tiebreaker session rankings compiled inside a {@link LeaderboardDTO}
     * @param globalRank  a formatted text tracking broader standings or analytical records
     */
    void displayLeaderboard(LeaderboardDTO leaderboard, String globalRank);

    /**
     * Displays an unrecoverable server failure, database crash, or transport drop, automatically tearing down
     * active inputs and shutting down the view execution loop.
     *
     * @param error a detailed structural trace or string detailing the critical crash footprint
     */
    void showFatalError(String error);

    /**
     * Displays the comprehensive global historical ranking leaderboard at match completion.
     *
     * @param globalLeaderboard the serialized tracking matrix compiled inside a {@link GlobalLeaderboardDTO}
     */
    void displayGlobalLeaderboard(GlobalLeaderboardDTO globalLeaderboard);

    /**
     * Renders the dynamic layout, priority assignments, and modifiers present on the turn order slots track.
     *
     * @param turnOrderTile a {@link List} of {@link TurnOrderTileDTO} objects representing the priority slots
     */
    void displayTurnOrderTile(List<TurnOrderTileDTO> turnOrderTile);

    /**
     * Unlocks decision controls prompting the player to declare whether they intend to finalize their active turn
     * or proceed with further asset construction maneuvers.
     */
    void askEndTurnOrBuyBuilding();

    /**
     * Displays a dedicated dialogue banner or text breakdown describing the execution rules, choices,
     * or penalties of an active event card.
     *
     * @param message the event resolution narrative description text
     */
    void showEventMessage(String message);

    /**
     * Displays the updated status of a specific player's tribe.
     * <p>
     * This method renders changes within individual player domains, drawing updates concerning
     * hand structures, tableau setups, or specialized capability metrics.
     * </p>
     *
     * @param nickname the unique profile name of the player owning the tribe
     * @param tribe    the populated {@link TribeStatusDTO} payload capturing the player's private tableau parameters
     */
    void showTribe(String nickname, TribeStatusDTO tribe);
}