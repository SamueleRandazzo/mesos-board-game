package it.polimi.ingsw.view.GUI;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.event.ActionEvent;

import java.util.Arrays;
import java.util.List;

public class SetPlayersController extends SceneController {

    //List that allow host player to select the total number of players in the game (from 2 to 5(
    private static final List<Integer> NUMBER_OF_PLAYERS = Arrays.asList(2,3,4,5);

    @FXML
    private ComboBox<Integer> numPlayersCombo;

    public void initialize() {

        numPlayersCombo.setItems(FXCollections.observableArrayList(NUMBER_OF_PLAYERS));

        //Set a default number of player in order to avoid to handle the case where players doesn't select the total number of player of the game
        numPlayersCombo.setValue(NUMBER_OF_PLAYERS.getFirst());
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


