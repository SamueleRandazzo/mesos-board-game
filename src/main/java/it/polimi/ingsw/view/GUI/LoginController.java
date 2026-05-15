package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Enum.Color;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

        colorComboBox.setCellFactory(lv -> new ListCell<Color>() {
            @Override
            protected void updateItem(Color item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.name());
                    setStyle("-fx-text-fill: " + item.name().toLowerCase() + "; -fx-font-weight: bold; -fx-background-color: #222;");
                }
            }
        });

        colorComboBox.setButtonCell(new ListCell<Color>() {
            @Override
            protected void updateItem(Color item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select color...");
                    setStyle("-fx-text-fill: white !important; -fx-opacity: 0.8;");
                } else {
                    setText(item.name());
                    setStyle("-fx-text-fill: " + item.name().toLowerCase() + "; -fx-font-weight: bold;");
                }
            }
        });

        loginButton.setOnMouseEntered(e -> loginButton.setStyle(loginButton.getStyle() + "-fx-background-color: linear-gradient(to bottom, #e67e22, #ff9f43);"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(loginButton.getStyle().replace("-fx-background-color: linear-gradient(to bottom, #e67e22, #ff9f43);", "")));
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
            showErrorMessage("Nickname can't be empty!");
            return;
        }

        if (selectedColor == null) {
            showErrorMessage("Choose a color!");
            return;
        }

        System.out.println("Login with Nick: " + nickname + " and Color: " + selectedColor);

        // --- ADDED FOR NETWORK SECURITY ---
        // Save the local identity in the network manager before sending the request
        network.setNickname(nickname);

        try {
            network.login(selectedColor, nickname);
        } catch (Exception e) {
            showErrorMessage(handleNetworkError(e));
        }
    }
}