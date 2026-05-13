package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.DTO.LeaderboardDTO;
import it.polimi.ingsw.network.DTO.PlayerRankDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class LeaderboardController extends SceneController {

    @FXML private TableView<PlayerRankDTO> leaderboardTable;
    @FXML private TableColumn<PlayerRankDTO, String> posColumn;
    @FXML private TableColumn<PlayerRankDTO, String> nameColumn;
    @FXML private TableColumn<PlayerRankDTO, Integer> prestigeColumn;
    @FXML private TableColumn<PlayerRankDTO, Integer> foodColumn;
    @FXML private Label winnerText;
    @FXML private Label globalRankText;
    @FXML private Button globalRankButton;

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

    @Override
    public void displayLeaderboard(LeaderboardDTO leaderboard, String globalRankMessage) {
        leaderboardTable.setItems(FXCollections.observableArrayList(leaderboard.getRankings()));

        updateWinnerLabel(leaderboard);

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

    @FXML
    private void handleShowGlobalRankings() {
        System.out.println("Opening Global Rankings...");
    }

    private void updateWinnerLabel(LeaderboardDTO leaderboard) {
        if (leaderboard.isSharedVictory()) {
            winnerText.setText("IT'S A DRAW! VICTORY IS SHARED");
        } else if (!leaderboard.getRankings().isEmpty()) {
            winnerText.setText("PLAYER " + leaderboard.getRankings().getFirst().getNickname() + " is the winner!");
        }
    }
}