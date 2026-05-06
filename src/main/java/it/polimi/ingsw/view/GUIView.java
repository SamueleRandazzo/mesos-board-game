package it.polimi.ingsw.view;

import it.polimi.ingsw.network.DTO.OfferTileDTO;
import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.view.GUI.SceneController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class GUIView implements View {
    private final NetworkManager network;
    private Stage stage;
    private SceneController currentController;
    private List<OfferTileDTO> lastTiles;

    public GUIView(NetworkManager network, Stage stage) {
        this.network = network;
        this.stage = stage;
    }

    /**
     * Generic method to upload a file FXML and change scene
     * @param fxmlFileName file nome (ex. "login.fxml")
     * @return the controller associated to the new scene
     */
    private SceneController loadScene(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFileName));
            Parent root = loader.load();

            SceneController controller = loader.getController();
            controller.setNetwork(network);
            controller.setView(this);
            this.currentController = controller;

            //runLater ensures that only the main thread of JavaFX will update dates
            Platform.runLater(() -> {
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            });

            return controller;
        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file FXML: " + fxmlFileName);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void showLogin() {
        loadScene("login.fxml");
    }

    @Override
    public void showLobby(int current, int total) {

        loadScene("lobby.fxml");

        Platform.runLater(() -> {
            if (currentController != null) {
                currentController.updateLobby(current, total);
            }
        });
    }

    @Override
    public void startGame(RemoteController controller) {
        network.setController(controller);
        loadScene("game_board.fxml");
    }

    @Override
    public void askMaxPlayers() {
        loadScene("set_players.fxml");
    }

    @Override
    public void askTotemPlacement(List<OfferTileDTO> tiles) {
        this.lastTiles = tiles;
        Platform.runLater(() -> {
            currentController.displayOfferTrack(tiles);
        });
    }

    @Override
    public void retryTotemPlacement() {
        askTotemPlacement(this.lastTiles);
    }

    @Override
    public void askCardChoose() {
        Platform.runLater(() -> {
            currentController.displayChoosableCards();
        });
    }

    @Override
    public void showError(String error) {
        Platform.runLater(() -> {
            currentController.showErrorMessage(error);
        });
    }

    @Override
    public void showMessage(String message) {
        Platform.runLater(() -> {
            currentController.showNotification(message);
        });
    }

    @Override
    public void showPlayersOrder(List<String> playersOrder) {
        Platform.runLater(() -> {
            currentController.updatePlayersOrder(playersOrder);
        });
    }
}