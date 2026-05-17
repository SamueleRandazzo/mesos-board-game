package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.DTO.GlobalLeaderboardDTO;
import it.polimi.ingsw.network.DTO.GlobalPlayerRankDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller class for the Global Leaderboard scene in the GUI.
 * <p>
 * This class handles the display of historical player rankings accumulated across multiple game sessions.
 * It populates a JavaFX {@link TableView} using dedicated data transfer objects containing absolute positions,
 * player nicknames, and total points accumulated globally.
 * </p>
 *
 * @see SceneController
 */
public class GlobalLeaderboardController extends SceneController {

    /** Table view container displaying the persistent global ranking rows. */
    @FXML private TableView<GlobalPlayerRankDTO> globalLeaderboardTable;

    /** Column rendering the player's historical global position (e.g., "1°"). */
    @FXML private TableColumn<GlobalPlayerRankDTO, String> posColumn;

    /** Column rendering the unique player nickname fetched from the server's database. */
    @FXML private TableColumn<GlobalPlayerRankDTO, String> nameColumn;

    /** Column rendering the total cumulative points achieved by the player throughout their career. */
    @FXML private TableColumn<GlobalPlayerRankDTO, Integer> prestigeColumn;

    /**
     * Initializes the controller automatically after its FXML elements have been completely loaded.
     * <p>
     * Sets up a custom property factory lambda for the {@code posColumn} to format the ranking with an ordinal indicator,
     * extracts text names for the {@code nameColumn}, and maps the {@code prestigeColumn} directly to the
     * corresponding structural fields inside the {@link GlobalPlayerRankDTO}.
     * </p>
     */
    @FXML
    public void initialize() {
        // Mapping DTO fields to columns
        posColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getRank() + "°"));

        nameColumn.setCellValueFactory(data -> {
            String name = data.getValue().getNickname();
            return new javafx.beans.property.SimpleStringProperty(name);
        });

        prestigeColumn.setCellValueFactory(new PropertyValueFactory<>("totalPoints"));
    }

    /**
     * Populates the global standings table with data packages retrieved from the server.
     * <p>
     * Converts the ranking records list into a JavaFX observable array list to bind it directly
     * to the graphical user interface.
     * </p>
     *
     * @param globalLeaderboard data packet containing the comprehensive global rankings list
     */
    @Override
    public void displayGlobalLeaderboard(GlobalLeaderboardDTO globalLeaderboard) {
        globalLeaderboardTable.setItems(FXCollections.observableArrayList(globalLeaderboard.getRankings()));
    }
}