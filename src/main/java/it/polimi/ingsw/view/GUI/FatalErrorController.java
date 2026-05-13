package it.polimi.ingsw.view.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FatalErrorController extends SceneController {

    @FXML
    private Label errorMessage;

    @Override
    public void showErrorMessage(String message) {
        errorMessage.setText(message);
    }
}