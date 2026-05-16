package it.polimi.ingsw.view.GUI;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.event.ActionEvent;
import javafx.scene.control.ListCell;

import java.util.Arrays;
import java.util.List;

public class SetPlayersController extends SceneController {

    //List that allow host player to select the total number of players in the game (from 2 to 5(
    private static final List<Integer> NUMBER_OF_PLAYERS = Arrays.asList(2,3,4,5);

    @FXML
    private ComboBox<Integer> numPlayersCombo;

    public void initialize() {
        numPlayersCombo.setItems(FXCollections.observableArrayList(NUMBER_OF_PLAYERS));
        numPlayersCombo.setValue(NUMBER_OF_PLAYERS.getFirst());

        numPlayersCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: white; -fx-background-color: #2b2b2b;");
                }
            }
        });

        numPlayersCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: white;");
                }
            }
        });
    }

    @FXML
    void handleConfirm(ActionEvent event) {
        Integer selected = numPlayersCombo.getValue();
        if (selected != null) {
            try {

                network.setTotalPlayers(selected);
            } catch (Exception e) {
                showErrorMessage("Errore di connessione: " + e.getMessage());
            }
        }
    }
}


