package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.DTO.OfferTileDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class GameBoardController extends SceneController {

    @FXML
    private Label gameNotificationLabel;

    @FXML
    private VBox playersContainer;

    @FXML
    private HBox offerTrackContainer;

    @FXML
    public void initialize() {
        offerTrackContainer.setDisable(true);
    }

    @Override
    public void showNotification(String msg) {
        Platform.runLater(() -> {
            if (gameNotificationLabel != null) {
                gameNotificationLabel.setText(msg);
            }
        });
    }

    @Override
    public void updatePlayersOrder(List<String> order) {
        Platform.runLater(() -> {
            // Empty the container before adding the updated list
            playersContainer.getChildren().clear();

            for (String playerName : order) {
                Label playerLabel = new Label("• " + playerName);
                playerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                playersContainer.getChildren().add(playerLabel);
            }
        });
    }


    /*
    For each tile creates a new structure with all the elements needed
     */
    @Override
    public void displayOfferTrack(List<OfferTileDTO> tiles) {
        offerTrackContainer.setDisable(true);
        Platform.runLater(() -> {
            // Clear the previous tiles from the screen
            offerTrackContainer.getChildren().clear();

            for (OfferTileDTO tile : tiles) {
                // We create a VBox to hold the tile's text information vertically
                VBox tileContent = new VBox(5); // 5 is the spacing between elements
                tileContent.setAlignment(Pos.CENTER);

                // Extract data from the DTO
                Label bonusLabel = new Label("Food: " + tile.getFoodBonus());
                Label topDrawsLabel = new Label("Top: " + tile.getTopRowDraws());
                Label bottomDrawsLabel = new Label("Bottom: " + tile.getBottomRowDraws());

                // Add texts to the vertical box
                tileContent.getChildren().addAll(bonusLabel, topDrawsLabel, bottomDrawsLabel);

                // Create the actual clickable button and put the VBox inside it
                Button tileButton = new Button();
                tileButton.setGraphic(tileContent);
                tileButton.setPrefSize(120, 160);

                // Check if the tile is still available or already taken
                if (!tile.isAvailable()) {
                    // Tile is taken: disable it and change color
                    tileButton.setDisable(true);
                    tileButton.setStyle("-fx-background-color: #bdc3c7; -fx-border-color: #7f8c8d;");

                    Label takenLabel = new Label("Taken by:\n" + tile.getNickname());
                    takenLabel.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold; -fx-text-alignment: center;");
                    tileContent.getChildren().add(takenLabel);
                } else {
                    // Tile is available: make it clickable and colorful
                    tileButton.setStyle("-fx-background-color: #f39c12; -fx-cursor: hand; -fx-border-color: #e67e22; -fx-border-width: 2px;");

                    // Set the action to send the choice to the server
                    tileButton.setOnAction(event -> {
                        try {
                            network.tileSelection(tile.getIndex());
                        } catch (Exception e) {
                            showErrorMessage("Network error: " + e.getMessage());
                        }
                    });
                }

                // add the generated button to the HBox on the screen
                offerTrackContainer.getChildren().add(tileButton);
            }
        });
    }

    @Override
    public void askTotemPlacement() {
        showNotification("It's your turn! Choose an Offer Tile.");
        offerTrackContainer.setDisable(false);
    }
}
