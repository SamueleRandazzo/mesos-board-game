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
            lobbyStatusLabel.setText("Waiting for connection...");
        }
    }

    @Override
    public void updateLobby(int current, int total) {
        Platform.runLater(() -> {
            if (lobbyStatusLabel != null) {
                lobbyStatusLabel.setText("Waiting for other players...\n(" + current + " of " + total + " connected)");
            }
        });
    }
}

