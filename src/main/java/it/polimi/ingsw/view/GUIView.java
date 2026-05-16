package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.*;
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
import java.util.Map;

public class GUIView implements View {
    private final NetworkManager network;
    private Stage stage;
    private SceneController currentController;
    private int totalPlayers;
    private String myNickname;

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
                Scene scene = new Scene(root, 1200, 675);
                stage.setScene(scene);
                stage.setResizable(true);
                stage.show();
                stage.centerOnScreen();
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
        this.totalPlayers = total;

        loadScene("lobby.fxml");

        Platform.runLater(() -> {
            if (currentController != null) {
                currentController.updateLobby(current, total);
            }
        });
    }

    @Override
    public void startGame(RemoteController controller, int totalPlayers) {

        this.totalPlayers = totalPlayers;

        network.setController(controller);

        SceneController controllerScene = loadScene("game_board.fxml");

        Platform.runLater(() -> {
            if (controllerScene != null) {
                controllerScene.setTotalPlayers(totalPlayers);
            }
        });
    }

    @Override
    public void showPlayersInfo(Map<String, Color> playersInfo) {
        Platform.runLater(() -> {
            currentController.setPlayersInfo(playersInfo);
        });
    }

    @Override
    public void askMaxPlayers() {
        loadScene("set_players.fxml");
    }

    @Override
    public void askTotemPlacement() {
        Platform.runLater(() -> {
            currentController.askTotemPlacement();
        });
    }

    @Override
    public void displayOfferTrack(List<OfferTileDTO> tiles) {
        Platform.runLater(() -> {
            currentController.displayOfferTrack(tiles, totalPlayers);
        });
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

    /**
     * Updates the tribe display for the GUI.
     * This method adapts to the new interface signature by accepting the player's nickname,
     * ensuring compatibility without altering the underlying JavaFX scene controller logic.
     *
     * @param nickname the nickname of the player owning the tribe
     * @param tribe    the updated TribeStatusDTO object
     */
    @Override
    public void showTribe(String nickname, TribeStatusDTO tribe) {
        Platform.runLater(() -> {
            if (currentController != null) {
                currentController.showTribe(nickname, tribe);
            }
        });
    }

    public void displayBoard(BoardDTO board) {
        Platform.runLater(() -> {
            currentController.displayBoard(board);
        });
    }

    @Override
    public void displayLeaderboard(LeaderboardDTO leaderboard, String globalRank) {
        loadScene("leaderboard.fxml");

        Platform.runLater(() -> {
           currentController.displayLeaderboard(leaderboard, globalRank);
        });
    }

    @Override
    public void showFatalError(String error) {
        loadScene("fatal_error.fxml");

        Platform.runLater(() -> {
            currentController.showErrorMessage(error);
        });
    }

    @Override
    public void displayGlobalLeaderboard(GlobalLeaderboardDTO leaderboard) {
        loadScene("global_leaderboard.fxml");

        Platform.runLater(() -> {
            currentController.displayGlobalLeaderboard(leaderboard);
        });
    }

    @Override
    public void displayTurnOrderTile(List<TurnOrderTileDTO> turnOrderTile) {
        Platform.runLater(() -> {
            currentController.displayTurnOrderTile(turnOrderTile);
        });
    }

    @Override
    public void showEventMessage(String message) {
        Platform.runLater(() -> {
            currentController.showToast(message);
        });
    }


    public String getMyNickname() { return myNickname; }

    public void setMyNickname(String myNickname) {
        this.myNickname = myNickname;
    }
}