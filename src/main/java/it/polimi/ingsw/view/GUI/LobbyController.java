package it.polimi.ingsw.view.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.application.Platform;

/**
 * Controller class for the Lobby waiting scene in the GUI.
 * <p>
 * This class manages the visual feedback shown to players who are waiting inside the lobby
 * while other participants connect, dynamically updating the player counter received from the server.
 * </p>
 *
 * @see SceneController
 */
public class LobbyController extends SceneController {

    /**
     * Label component injected from the FXML layout used to display the current lobby
     * status text and player connection ratios.
     */
    @FXML
    private Label lobbyStatusLabel;

    /**
     * Initializes the controller automatically after its FXML elements have been fully loaded.
     * <p>
     * Sets the initial fallback text of the {@code lobbyStatusLabel} to indicate that the application
     * is awaiting a stable handshake or baseline connection setup.
     * </p>
     */
    @FXML
    public void initialize() {
        if (lobbyStatusLabel != null) {
            lobbyStatusLabel.setText("Waiting for connection...");
        }
    }

    /**
     * Updates the lobby status label with the current number of connected players.
     * <p>
     * Since this method is triggered by asynchronous background network events (e.g., RMI notifications),
     * the structural text update is safely delegated to the JavaFX Application Thread
     * using {@link Platform#runLater(Runnable)} to prevent rendering race conditions.
     * </p>
     *
     * @param current the number of players currently inside the lobby
     * @param total the target number of players required to start the match (0 if the host is still deciding)
     */
    @Override
    public void updateLobby(int current, int total) {
        Platform.runLater(() -> {
            if (lobbyStatusLabel != null) {
                lobbyStatusLabel.setText("Waiting for other players...\n(" + current + " of " + total + " connected)");
            }
        });
    }
}