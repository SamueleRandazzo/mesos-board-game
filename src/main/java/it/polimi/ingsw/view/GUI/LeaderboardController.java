package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.DTO.LeaderboardDTO;
import it.polimi.ingsw.network.DTO.PlayerRankDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller class for the final Leaderboard scene in the GUI.
 * <p>
 * This class handles the visualization of the match results inside a JavaFX {@link TableView},
 * mapping ranking statistics from DTO structures, highlighting the winner(s), and managing
 * the conditional interaction logic to fetch and display global ranking databases.
 * </p>
 *
 * @see SceneController
 */
public class LeaderboardController extends SceneController {

    /** Cache holding the total number of players registered in the completed game session. */
    private int playerSize = 0;

    /** Table view container containing the final sorted list of player ranks. */
    @FXML private TableView<PlayerRankDTO> leaderboardTable;

    /** Column rendering the relative ranking placement position (e.g., "1°"). */
    @FXML private TableColumn<PlayerRankDTO, String> posColumn;

    /** Column rendering the player's unique nickname, capitalized if they won. */
    @FXML private TableColumn<PlayerRankDTO, String> nameColumn;

    /** Column rendering the primary victory points (prestige points) accumulated. */
    @FXML private TableColumn<PlayerRankDTO, Integer> prestigeColumn;

    /** Column rendering the tie-breaker criteria metric (food amount remaining). */
    @FXML private TableColumn<PlayerRankDTO, Integer> foodColumn;

    /** Text label declaring the overall match winner or an eventual draw status. */
    @FXML private Label winnerText;

    /** Text area designated to output global database metrics and historical status messages. */
    @FXML private Label globalRankText;

    /** Interactive action button to request historical global rankings data from the server. */
    @FXML private Button globalRankButton;

    /**
     * Initializes the controller automatically after its FXML elements have been fully loaded.
     * <p>
     * Sets up custom lambda factories for the {@code posColumn} and {@code nameColumn} to dynamically
     * append format elements (like ordinal indicators or casing adjustments), and binds the numeric
     * columns via standard {@link PropertyValueFactory} reflecting {@link PlayerRankDTO} properties.
     * </p>
     */
    @FXML
    public void initialize() {
        // Mapping DTO fields to columns
        posColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPosition() + "°"));

        nameColumn.setCellValueFactory(data -> {
            String name = data.getValue().getNickname();
            if (data.getValue().isWinner()) {
                return new javafx.beans.property.SimpleStringProperty(name.toUpperCase());
            }
            return new javafx.beans.property.SimpleStringProperty(name);
        });

        prestigeColumn.setCellValueFactory(new PropertyValueFactory<>("prestigePoints"));
        foodColumn.setCellValueFactory(new PropertyValueFactory<>("foodAmount"));
    }

    /**
     * Populates the graphical leaderboard components with the final standings data.
     * <p>
     * This method renders the rows within the table, sets text layout statuses for the victory panel,
     * and evaluates if the global leaderboard features should be toggled visible and layout-managed
     * based on incoming data strings.
     * </p>
     *
     * @param leaderboard data packet containing final ranks and structural draw flags
     * @param globalRankMessage status report containing global statistics info (can be null or empty)
     */
    @Override
    public void displayLeaderboard(LeaderboardDTO leaderboard, String globalRankMessage) {
        leaderboardTable.setItems(FXCollections.observableArrayList(leaderboard.getRankings()));

        playerSize = leaderboard.getRankings().size();

        updateWinnerLabel(leaderboard);

        // Manage visibility and layout budgeting of global rankings components
        if (globalRankMessage != null && !globalRankMessage.isEmpty()) {
            globalRankText.setText(globalRankMessage);

            globalRankButton.setVisible(true);
            globalRankButton.setManaged(true);
        } else {
            globalRankText.setText("");
            globalRankButton.setVisible(false);
            globalRankButton.setManaged(false);
        }
    }

    /**
     * Handles the action event triggered when the user requests global standings information.
     * <p>
     * Contacts the network layer to retrieve extensive data profiles based on the current context
     * size parameters. Automatically wraps and logs standard connection fault signatures.
     * </p>
     */
    @FXML
    private void handleShowGlobalRankings() {
        try {
            network.seeGlobalLeaderboard(playerSize);
        } catch (Exception e) {
            showErrorMessage(handleNetworkError(e));
        }
    }

    /**
     * Internal utility parsing the leaderboard context to determine the proper victory headline.
     * Identifies tie configurations vs standalone victory cases.
     *
     * @param leaderboard data profile payload containing sorted rankings listings
     */
    private void updateWinnerLabel(LeaderboardDTO leaderboard) {
        if (leaderboard.isSharedVictory()) {
            winnerText.setText("IT'S A DRAW! VICTORY IS SHARED");
        } else if (!leaderboard.getRankings().isEmpty()) {
            winnerText.setText("PLAYER " + leaderboard.getRankings().getFirst().getNickname() + " is the winner!");
        }
    }
}