package it.polimi.ingsw.view.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller class for the Fatal Error scene in the GUI.
 * <p>
 * This class is responsible for handling unrecoverable game events (e.g., sudden player disconnections,
 * server crashes, or fatal network failures) by displaying a descriptive error message to the user
 * and halting standard gameplay navigation.
 * </p>
 *
 * @see SceneController
 */
public class FatalErrorController extends SceneController {

    /**
     * Graphical label component injected from the FXML layout used to display
     * the technical or user-friendly description of the fatal error.
     */
    @FXML
    private Label errorMessage;

    /**
     * Sets and updates the visible error text on the screen.
     * <p>
     * This method overrides the base implementation in {@link SceneController} to directly
     * inject the incoming error description string into the dedicated {@code errorMessage} label.
     * </p>
     *
     * @param message the descriptive text explaining the cause of the fatal exception or disconnection
     */
    @Override
    public void showErrorMessage(String message) {
        errorMessage.setText(message);
    }
}