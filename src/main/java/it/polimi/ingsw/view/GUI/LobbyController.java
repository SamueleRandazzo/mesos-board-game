package it.polimi.ingsw.view.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.application.Platform;

public class LobbyController extends SceneController {

    @FXML
    private Label lobbyStatusLabel;

    @FXML
    public void initialize() {

        if (lobbyStatusLabel != null) {
            lobbyStatusLabel.setText("In attesa di connessione...");
        }
    }

    @Override
    public void updateLobby(int current, int total) {
        Platform.runLater(() -> {
            if (lobbyStatusLabel != null) {
                lobbyStatusLabel.setText("In attesa degli altri giocatori...\n(" + current + " di " + total + " connessi)");
            }
        });
    }
}

