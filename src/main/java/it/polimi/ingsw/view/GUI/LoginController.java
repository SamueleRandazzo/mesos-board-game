package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Enum.Color;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.effect.DropShadow;

/**
 * Controller class for the Login scene in the GUI.
 * <p>
 * This class handles user credentials input (nickname validation and color selection),
 * dynamic UI component styling based on game enumerations, hovering effects, and dispatching
 * the login event to the underlying network architecture.
 * </p>
 *
 * @see SceneController
 */
public class LoginController extends SceneController {

    /** Input field where the player types their unique nickname. */
    @FXML
    private TextField nicknameField;

    /** Dropdown selection component displaying the available player {@link Color}s. */
    @FXML
    private ComboBox<Color> colorComboBox;

    /** Button component that triggers the login procedure when pressed. */
    @FXML
    private Button loginButton;

    /** Graphical label used to display validation failures or server connection errors. */
    @FXML
    private Label errorLabel;

    /**
     * Initializes the controller automatically after its FXML elements have been fully loaded.
     * <p>
     * This method populates the {@code colorComboBox} with values from the {@link Color} enum, hides the
     * {@code errorLabel}, and applies custom item styling rules to both the dropdown list cells and the
     * main selection cell. It also registers inline hover event listeners on the {@code loginButton}
     * to inject dynamic linear gradient transitions via inline CSS styles.
     * </p>
     */
    @FXML
    public void initialize() {

        colorComboBox.setItems(FXCollections.observableArrayList(Color.values()));

        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }

        DropShadow textGlow = new DropShadow();
        textGlow.setColor(javafx.scene.paint.Color.web("#FFF5E1", 0.85)); // Colore crema/oro chiaro coerente con la UI
        textGlow.setRadius(5.0);
        textGlow.setSpread(0.6);

        // Customizes the rendering of each color selection cell inside the dropdown view
        colorComboBox.setCellFactory(lv -> new ListCell<Color>() {
            @Override
            protected void updateItem(Color item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setEffect(null);
                } else {
                    setText(item.name());
                    // Cambiato lo sfondo in un grigio roccia leggermente più morbido per armonizzarsi con l'alone
                    setStyle("-fx-text-fill: " + item.name().toLowerCase() + "; -fx-font-weight: bold; -fx-background-color: #2b2524; -fx-padding: 8px;");
                    setEffect(textGlow);
                }
            }
        });

        // Customizes the rendering of the button cell when the selection is active or placeholder is needed
        colorComboBox.setButtonCell(new ListCell<Color>() {
            @Override
            protected void updateItem(Color item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select color...");
                    setStyle("-fx-text-fill: rgba(255, 255, 255, 0.35); -fx-font-weight: bold;");
                    setEffect(null);
                } else {
                    setText(item.name());
                    setStyle("-fx-text-fill: " + item.name().toLowerCase() + "; -fx-font-weight: bold;");
                    setEffect(textGlow);
                }
            }
        });

        nicknameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                nicknameField.setStyle(nicknameField.getStyle() + "-fx-border-color: #e67e22; -fx-border-width: 1.5;");
            } else {
                nicknameField.setStyle(nicknameField.getStyle() + "-fx-border-color: rgba(244, 213, 141, 0.25); -fx-border-width: 1;");
            }
        });

        colorComboBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                colorComboBox.setStyle(colorComboBox.getStyle() + "-fx-border-color: #e67e22; -fx-border-width: 1.5;");
            } else {
                colorComboBox.setStyle(colorComboBox.getStyle() + "-fx-border-color: rgba(244, 213, 141, 0.25); -fx-border-width: 1;");
            }
        });

        // Setup mouse hover visual feedback rules for the login trigger button
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(loginButton.getStyle() + "-fx-background-color: linear-gradient(to bottom, #e67e22, #ff9f43);"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(loginButton.getStyle().replace("-fx-background-color: linear-gradient(to bottom, #e67e22, #ff9f43);", "")));
    }

    /**
     * Displays a customized error string message on the UI's error label block.
     * <p>
     * Since this message may originate from background network listening threads, the updates
     * are safely wrapped and scheduled inside JavaFX's application UI rendering thread via {@link Platform#runLater(Runnable)}.
     * </p>
     *
     * @param msg the validation error or exception descriptive message to show
     */
    @Override
    public void showErrorMessage(String msg){
        Platform.runLater(() -> {
            if(errorLabel != null) {
                errorLabel.setText(msg);
                errorLabel.setVisible(true);
            }
        });
    }

    /**
     * Handles the login action event triggered when the user submits their data.
     * <p>
     * Clears previous errors, extracts contents from the input components, performs basic local
     * trimming checks to assure empty strings or missing object pointers are blocked, and calls
     * the remote login procedure. If the request is accepted, the current client view configuration
     * caches the validated user nickname.
     * </p>
     *
     * @param event the {@link ActionEvent} generated by clicking the login button or pressing enter
     */
    @FXML
    void handleLogin(ActionEvent event) {

        // Error reset each try
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

        try {
            network.login(selectedColor, nickname);
            view.setMyNickname(nickname);
        } catch (Exception e) {
            showErrorMessage(handleNetworkError(e));
        }
    }
}