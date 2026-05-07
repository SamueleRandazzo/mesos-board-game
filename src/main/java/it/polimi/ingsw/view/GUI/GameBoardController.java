package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.OfferTileDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameBoardController extends SceneController {

    private static final int OFFER_TILE_WIDTH = 92;
    private static final int OFFER_TILE_HEIGHT = 148;
    private int totalPlayers;
    private final Map<String, Color> playersInfo = new LinkedHashMap<>();

    //TODO: Verificare le OfferTile effettive per ogni giocatore
    private static final Map<Integer, List<String>> OFFER_TILE_IMAGES = Map.of(
            2, List.of("/images/Map/Front/Start 2 Players.png","/images/Map/Front/Font Map 2.png","/images/Map/Front/Font Map 3.png","/images/Map/Front/Font Map 5.png","/images/Map/Front/Font Map 6.png"),
            3, List.of("/images/Map/Front/Start 3 Players.png","/images/Map/Front/Font Map 2.png","/images/Map/Front/Font Map 3.png","/images/Map/Front/Font Map 4.png","/images/Map/Front/Font Map 5.png","/images/Map/Front/Font Map 6.png"),
            4, List.of("/images/Map/Front/Start 4 Players.png","/images/Map/Front/Font Map 2.png","/images/Map/Front/Font Map 3.png","/images/Map/Front/Font Map 4.png","/images/Map/Front/Font Map 5.png","/images/Map/Front/Font Map 6.png","/images/Map/Front/Font Map 7.png"),
            5, List.of("/images/Map/Front/Start 5 Players.png","/images/Map/Front/Font Map 1.png","/images/Map/Front/Font Map 2.png","/images/Map/Front/Font Map 3.png","/images/Map/Front/Font Map 4.png","/images/Map/Front/Font Map 5.png","/images/Map/Front/Font Map 6.png","/images/Map/Front/Font Map 7.png")
    );

    @FXML
    private Label gameNotificationLabel;

    @FXML
    private VBox playersContainer;

    @FXML
    private HBox offerTrackContainer;

    @FXML
    private HBox upperCardsContainer;

    @FXML
    private HBox lowerCardsContainer;

    /**
     * Initializes the board scene with disabled offer tiles and  cards.
     *
     * The offer track is populated later, when the server sends the initial
     * OfferTileDTO list.
     */
    @FXML
    public void initialize() {
        offerTrackContainer.setDisable(true);

        // TODO: quando avremo i DTO delle carte, queste righe verranno riempite
        // con le carte reali presenti sopra e sotto le mappe.
        renderStaticCardBacks();
    }

    /**
     * Renders temporary card backs in the upper and lower board rows.
     *
     * TODO: Replace the fixed placeholder counts with the actual number of cards
     * received from the server once board card DTOs are implemented.
     */
    private void renderStaticCardBacks() {
        upperCardsContainer.getChildren().clear();
        lowerCardsContainer.getChildren().clear();

        // TODO: sostituire questi numeri con upperCards.size() e lowerCards.size()
        // quando arriveranno i DTO delle carte dal server.
        int upperCardsPlaceholderCount = 7;
        int lowerCardsPlaceholderCount = 4;

        for (int i = 0; i < upperCardsPlaceholderCount; i++) {
            upperCardsContainer.getChildren().add(createCardBack());
        }

        for (int i = 0; i < lowerCardsPlaceholderCount; i++) {
            lowerCardsContainer.getChildren().add(createCardBack());
        }
    }

    //Fino a che non abbiamo il DTO delle carte viene visualizzago solo il back
    private StackPane createCardBack() {
        StackPane card = new StackPane();
        card.setPrefSize(88, 128);

        ImageView imageView = createImageView(
                "/images/Cards/Back/Back Card Age 1.png",
                88,
                128
        );

        card.getChildren().add(imageView);

        card.setStyle("""
            -fx-background-color: transparent;
            -fx-padding: 0;
            """);

        return card;
    }

    @Override
    public void showNotification(String msg) {
        Platform.runLater(() -> setNotification(msg, false));
    }

    @Override
    public void showErrorMessage(String msg) {
        Platform.runLater(() -> setNotification(msg, true));
    }

    @Override
    public void setPlayersInfo(Map<String, Color> playersInfo) {
        this.playersInfo.clear();
        this.playersInfo.putAll(playersInfo);

        this.totalPlayers = playersInfo.size();
    }

    @Override
    public void updatePlayersOrder(List<String> order) {
        Platform.runLater(() -> {
            playersContainer.getChildren().clear();

            for (int i = 0; i < order.size(); i++) {
                Label playerLabel = new Label((i + 1) + ". " + order.get(i));
                playerLabel.setMaxWidth(Double.MAX_VALUE);
                playerLabel.setStyle("""
                        -fx-font-size: 14px;
                        -fx-font-weight: bold;
                        -fx-text-fill: #2b2f32;
                        -fx-background-color: rgba(255,255,255,0.45);
                        -fx-background-radius: 5;
                        -fx-padding: 8 10 8 10;
                        """);
                playersContainer.getChildren().add(playerLabel);
            }
        });
    }

    /*
    Method that creates the offerTrack, is it called just one time at the start of the game beacuse the offerTrack will never change
     */
    @Override
    public void displayOfferTrack(List<OfferTileDTO> tiles, int total) {
        Platform.runLater(() -> {
            offerTrackContainer.getChildren().clear();

            int num_players = total;
            List <String> map_images = OFFER_TILE_IMAGES.get(total);

            // Index 0 is the static starting tile; offer tile DTOs start from index 1.
            for (int i = 0; i < map_images.size(); i++){
                String imagePath = map_images.get(i);

                if (i == 0) {
                    offerTrackContainer.getChildren().add(createStaticStartMapTile(imagePath));
                } else {
                OfferTileDTO tile = tiles.get(i - 1);
                offerTrackContainer.getChildren().add(createOfferTileButton(tile, imagePath));
                }
            }

            setOfferTrackEnabled(false);
            });
    }

    public void displayBoardCards() {
        //TODO: carica le carte sopra e sotto quando ci saranno
    }

    /**
     * Enables interaction with the offer track when this client has to place a totem.
     */
    @Override
    public void askTotemPlacement() {
        Platform.runLater(() -> {
            setNotification("Your turn: choose an offer tile.", false);
            setOfferTrackEnabled(true);
        });
    }

    /**
     * Handles the card selection phase notification.
     *
     * TODO: Enable card selection here once board card DTOs are available.
     */
    @Override
    public void displayChoosableCards() {
        Platform.runLater(() -> {
            setNotification("Your turn: choose a card position.", false);
        });
    }

    @Override
    public void setTotalPlayers (int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    /**
     * Creates an interactive offer tile button bound to a specific OfferTileDTO.
     */
    private Button createOfferTileButton(OfferTileDTO tile, String imagePath) {
        Button tileButton = new Button();
        tileButton.setMinSize(OFFER_TILE_WIDTH + 12, OFFER_TILE_HEIGHT + 12);
        tileButton.setPrefSize(OFFER_TILE_WIDTH + 12, OFFER_TILE_HEIGHT + 12);
        tileButton.setMaxSize(OFFER_TILE_WIDTH + 12, OFFER_TILE_HEIGHT + 12);

        tileButton.setGraphic(createOfferTileGraphic(tile, imagePath));
        tileButton.setStyle(offerTileStyle(tile.isAvailable()));

        if (tile.isAvailable()) {
            tileButton.setOnAction(event -> {
                try {
                    setOfferTrackEnabled(false);
                    network.tileSelection(tile.getIndex());
                } catch (Exception e) {
                    setOfferTrackEnabled(true);
                    showErrorMessage(handleNetworkError(e));
                }
            });
        } else {
            tileButton.setDisable(true);
        }

        return tileButton;
    }
    /**
     * Builds the visual content of an offer tile.
     */
    private StackPane createOfferTileGraphic(OfferTileDTO tile, String imagePath) {
        StackPane tilePane = new StackPane();
        tilePane.setMinSize(OFFER_TILE_WIDTH, OFFER_TILE_HEIGHT);
        tilePane.setPrefSize(OFFER_TILE_WIDTH, OFFER_TILE_HEIGHT);
        tilePane.setMaxSize(OFFER_TILE_WIDTH, OFFER_TILE_HEIGHT);

        ImageView imageView = createImageView(imagePath, OFFER_TILE_WIDTH, OFFER_TILE_HEIGHT);
        tilePane.getChildren().add(imageView);

        if (!tile.isAvailable()) {
            Label owner = new Label(tile.getNickname());
            owner.setMaxWidth(OFFER_TILE_WIDTH - 12);
            owner.setAlignment(Pos.CENTER);
            owner.setStyle("""
                    -fx-background-color: rgba(36,52,71,0.88);
                    -fx-text-fill: white;
                    -fx-font-size: 11px;
                    -fx-font-weight: bold;
                    -fx-padding: 4 6 4 6;
                    -fx-background-radius: 4;
                    """);
            StackPane.setAlignment(owner, Pos.TOP_CENTER);
            tilePane.getChildren().add(owner);
        }

        Label index = new Label(String.valueOf(tile.getIndex()));
        index.setStyle("""
                -fx-background-color: rgba(255,255,255,0.88);
                -fx-text-fill: #243447;
                -fx-font-size: 12px;
                -fx-font-weight: bold;
                -fx-padding: 2 6 2 6;
                -fx-background-radius: 12;
                """);
        StackPane.setAlignment(index, Pos.BOTTOM_LEFT);
        tilePane.getChildren().add(index);

        return tilePane;
    }

    /**
     * Loads an image resource and wraps it in a configured ImageView.
     */
    private ImageView createImageView(String resourcePath, double width, double height) {
        URL resource = getClass().getResource(resourcePath);
        ImageView view = new ImageView();
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setPreserveRatio(true);
        view.setSmooth(true);

        if (resource != null) {
            view.setImage(new Image(resource.toExternalForm(), width, height, true, true));
        }

        return view;
    }

    /**
     * Enables or disables the whole offer track depending on whether the client
     * is currently allowed to place a totem.
     */
    private void setOfferTrackEnabled(boolean enabled) {
        offerTrackContainer.setDisable(!enabled);
        offerTrackContainer.setOpacity(enabled ? 1.0 : 0.78);
    }

    /**
     * Updates the top notification label using a different color for errors.
     */
    private void setNotification(String msg, boolean error) {
        if (gameNotificationLabel == null) {
            return;
        }

        gameNotificationLabel.setText(msg);
        gameNotificationLabel.setTextFill(error
                ? javafx.scene.paint.Color.web("#ffb3a7")
                : javafx.scene.paint.Color.WHITE);
    }

    private String offerTileStyle(boolean available) {
        String base = """
                -fx-padding: 6;
                -fx-background-radius: 7;
                -fx-border-radius: 7;
                -fx-border-width: 2;
                """;

        if (available) {
            return base + """
                    -fx-background-color: #fff8e6;
                    -fx-border-color: #d7b35a;
                    -fx-cursor: hand;
                    """;
        }

        return base + """
                -fx-background-color: #c8c1ad;
                -fx-border-color: #8d846e;
                """;
    }

    /**
     * Creates the non-interactive starting map tile for the selected player count.
     */
    private StackPane createStaticStartMapTile(String imagePath) {
        StackPane tilePane = new StackPane();
        tilePane.setMinSize(OFFER_TILE_WIDTH, OFFER_TILE_HEIGHT);
        tilePane.setPrefSize(OFFER_TILE_WIDTH, OFFER_TILE_HEIGHT);
        tilePane.setMaxSize(OFFER_TILE_WIDTH, OFFER_TILE_HEIGHT);

        ImageView imageView = createImageView(imagePath, OFFER_TILE_WIDTH, OFFER_TILE_HEIGHT);
        tilePane.getChildren().add(imageView);

        return tilePane;
    }

}
