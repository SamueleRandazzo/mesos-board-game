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
     * Generic method to upload a file FXML and change scene while preserving window dimensions
     * @param fxmlFileName file name (ex. "login.fxml")
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

            Scene scene = new Scene(root);

            Platform.runLater(() -> {
                boolean isFullScreen = stage.isFullScreen();
                boolean isMaximized = stage.isMaximized();
                double currentWidth = stage.getWidth();
                double currentHeight = stage.getHeight();

                stage.setScene(scene);
                stage.setResizable(true);

                if (isFullScreen) {
                    stage.setFullScreen(true);
                } else if (isMaximized) {
                    stage.setMaximized(true);
                } else {
                    stage.setWidth(currentWidth);
                    stage.setHeight(currentHeight);
                }

                stage.show();

                if (!isFullScreen && !isMaximized) {
                    stage.centerOnScreen();
                }
            });

            return controller;
        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file FXML: " + fxmlFileName);
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

        SceneController controller = loadScene("lobby.fxml");

        Platform.runLater(() -> {
            if (controller != null) {
                controller.updateLobby(current, total);
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
            if (currentController != null) {
                currentController.setPlayersInfo(playersInfo);
            }
        });
    }

    @Override
    public void askMaxPlayers() {
        loadScene("set_players.fxml");
    }

    @Override
    public void askTotemPlacement() {
        Platform.runLater(() -> {
            if (currentController != null) {
                currentController.askTotemPlacement();
            }
        });
    }

    @Override
    public void displayOfferTrack(List<OfferTileDTO> tiles) {
        Platform.runLater(() -> {
            if (currentController != null) {
                currentController.displayOfferTrack(tiles, totalPlayers);
            }
        });
    }

    @Override
    public void askCardChoose() {
        Platform.runLater(() -> {
            if (currentController != null) {
                currentController.displayChoosableCards();
            }
        });
    }

    @Override
    public void showError(String error) {
        Platform.runLater(() -> {
            if (currentController != null) {
                currentController.showErrorMessage(error);
            }
        });
    }

    @Override
    public void showMessage(String message) {
        Platform.runLater(() -> {
            if (currentController != null) {
                currentController.showNotification(message);
            }
        });
    }

    @Override
    public void showPlayersOrder(List<String> playersOrder) {
        Platform.runLater(() -> {
            if (currentController != null) {
                currentController.updatePlayersOrder(playersOrder);
            }
        });
    }

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
            if (currentController != null) {
                currentController.displayBoard(board);
            }
        });
    }

    @Override
    public void displayLeaderboard(LeaderboardDTO leaderboard, String globalRank) {
        SceneController controller = loadScene("leaderboard.fxml");

        Platform.runLater(() -> {
            if (controller != null) {
                controller.displayLeaderboard(leaderboard, globalRank);
            }
        });
    }

    @Override
    public void showFatalError(String error) {
        SceneController controller = loadScene("fatal_error.fxml");

        Platform.runLater(() -> {
            if (controller != null) {
                controller.showErrorMessage(error);
            }
        });
    }

    @Override
    public void displayGlobalLeaderboard(GlobalLeaderboardDTO leaderboard) {
        SceneController controller = loadScene("global_leaderboard.fxml");

        Platform.runLater(() -> {
            if (controller != null) {
                controller.displayGlobalLeaderboard(leaderboard);
            }
        });
    }

    @Override
    public void displayTurnOrderTile(List<TurnOrderTileDTO> turnOrderTile) {
        Platform.runLater(() -> {
            if (currentController != null) {
                currentController.displayTurnOrderTile(turnOrderTile);
            }
        });
    }

    @Override
    public void showEventMessage(String message) {
        Platform.runLater(() -> {
            if (currentController != null) {
                currentController.showToast(message);
            }
        });
    }

    @Override
    public void askEndTurnOrBuyBuilding() {
        Platform.runLater(() -> {
            if (currentController != null) {
                currentController.showEndTurn();
            }
        });
    }

    public String getMyNickname() { return myNickname; }

    public void setMyNickname(String myNickname) {
        this.myNickname = myNickname;
    }
}