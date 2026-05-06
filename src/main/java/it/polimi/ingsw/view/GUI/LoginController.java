package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Enum.Color;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

public class LoginController extends SceneController {

    @FXML
    private TextField nicknameField;

    @FXML
    private ComboBox<Color> colorComboBox;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {

        colorComboBox.setItems(FXCollections.observableArrayList(Color.values()));

        if (errorLabel != null) {

            errorLabel.setVisible(false);
        }
    }

    @Override
    public void showErrorMessage(String msg){
        Platform.runLater(() -> {

            if(errorLabel != null) {
                errorLabel.setText(msg);
                errorLabel.setVisible(true);
            }
        });
    }

    @FXML
    void handleLogin(ActionEvent event) {

        //Error reset each try
        errorLabel.setVisible(false);

        String nickname = nicknameField.getText();
        Color selectedColor = colorComboBox.getValue();

        if (nickname == null || nickname.trim().isEmpty()) {
            showErrorMessage("Il nickname non può essere vuoto!");
            return;
        }

        if (selectedColor == null) {
            showErrorMessage("Devi selezionare un colore!");
            return;
        }

        System.out.println("Login con Nick: " + nickname + " e Colore: " + selectedColor);

        try {
            network.login(selectedColor, nickname);
        } catch (Exception e) {
            showErrorMessage("Errore di rete: " + e.getMessage());
        }
    }
}