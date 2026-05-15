package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.DTO.GlobalLeaderboardDTO;
import it.polimi.ingsw.network.DTO.GlobalPlayerRankDTO;
import it.polimi.ingsw.network.DTO.LeaderboardDTO;
import it.polimi.ingsw.network.DTO.PlayerRankDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class GlobalLeaderboardController extends SceneController {

    @FXML private TableView<GlobalPlayerRankDTO> globalLeaderboardTable;
    @FXML private TableColumn<GlobalPlayerRankDTO, String> posColumn;
    @FXML private TableColumn<GlobalPlayerRankDTO, String> nameColumn;
    @FXML private TableColumn<GlobalPlayerRankDTO, Integer> prestigeColumn;

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

    @Override
    public void displayGlobalLeaderboard(GlobalLeaderboardDTO globalLeaderboard) {
        globalLeaderboardTable.setItems(FXCollections.observableArrayList(globalLeaderboard.getRankings()));
    }
}