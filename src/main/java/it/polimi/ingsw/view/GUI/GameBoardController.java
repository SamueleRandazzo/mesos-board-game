package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.*;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.*;
import it.polimi.ingsw.view.LocalCardDictionary;
import javafx.util.Duration;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GameBoardController extends SceneController {

    private static final int OFFER_TILE_WIDTH = 92;
    private static final int OFFER_TILE_HEIGHT = 148;
    private static final int CARD_WIDTH = 88;
    private static final int CARD_HEIGHT = 128;

    private int totalPlayers;
    private BoardDTO lastBoard; //Last board because displayBoard and askCardChoose comes in different moments
    private boolean cardSelectionEnabled;
    private List<TurnOrderTileDTO> lastTurnOrderTile = null;
    private TribeStatusDTO myTribe;
    private Map<String, TribeStatusDTO> otherPlayerTribes = new LinkedHashMap<>();

    private final Map<String, Color> playersInfo = new LinkedHashMap<>();

    //TODO: Verificare le OfferTile effettive per ogni giocatore
    private static final Map<Integer, List<String>> OFFER_TILE_IMAGES = Map.of(
            2, List.of("/images/Map/Front/Font Map 2.png","/images/Map/Front/Font Map 3.png","/images/Map/Front/Font Map 5.png","/images/Map/Front/Font Map 6.png"),
            3, List.of("/images/Map/Front/Font Map 2.png","/images/Map/Front/Font Map 3.png","/images/Map/Front/Font Map 4.png","/images/Map/Front/Font Map 5.png","/images/Map/Front/Font Map 6.png"),
            4, List.of("/images/Map/Front/Font Map 2.png","/images/Map/Front/Font Map 3.png","/images/Map/Front/Font Map 4.png","/images/Map/Front/Font Map 5.png","/images/Map/Front/Font Map 6.png","/images/Map/Front/Font Map 7.png"),
            5, List.of("/images/Map/Front/Font Map 1.png","/images/Map/Front/Font Map 2.png","/images/Map/Front/Font Map 3.png","/images/Map/Front/Font Map 4.png","/images/Map/Front/Font Map 5.png","/images/Map/Front/Font Map 6.png","/images/Map/Front/Font Map 7.png")
    );

    private static final Map<Integer, String> TURN_ORDER_TILE_IMAGES = Map.of(
            2, "/images/Map/Front/Start 2 Players.png",
            3, "/images/Map/Front/Start 3 Players.png",
            4, "/images/Map/Front/Start 4 Players.png",
            5, "/images/Map/Front/Start 5 Players.png"
    );

    private static final Map<Integer, String> BACK_CARD_ERA = Map.of(
            1,"/images/Cards/Back/Back Card Age 1.png",
            2,"/images/Cards/Back/Back Card Age 2.png",
            3,"/images/Cards/Back/Back Card Age 3.png"
    );

    private static final Map<Color, String> PLAYER_TOTEM = Map.of(
            Color.BLACK,"/images/Characters/Player black.png",
            Color.BLUE,"/images/Characters/Player blue.png",
            Color.RED,"/images/Characters/Player red.png",
            Color.WHITE,"/images/Characters/Player white.png",
            Color.YELLOW,"/images/Characters/Player yellow.png"
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

    @FXML
    private Label gameLogLabel;

    @FXML
    private StackPane turnOrderContainer;

    @FXML
    private StackPane firstDeckCardContainer;

    @FXML private ImageView artistsIcon;
    @FXML private Label artistsCount;

    @FXML private ImageView gatherersIcon;
    @FXML private Label gatherersCount;

    @FXML private ImageView buildersIcon;
    @FXML private Label buildersCount;

    @FXML private ImageView huntersIcon;
    @FXML private Label huntersCount;

    @FXML private ImageView inventorsIcon;
    @FXML private Label inventorsCount;

    @FXML private ImageView shamansIcon;
    @FXML private Label shamansCount;

    @FXML private ImageView buildingsIcon;
    @FXML private Label buildingsCount;

    @FXML private AnchorPane tribeOverlay;
    @FXML private HBox tribeCardsContainer;
    @FXML private Button closeTribeButton;

    @FXML private Label totalPrestigePointsLabel;

    @FXML private Label currentFoodLabel;
    @FXML private Label totalSustenanceDiscountLabel;
    @FXML private Label totalBuildingsFoodDiscountLabel;
    @FXML private Label shamanStarsLabel;
    @FXML private Label extraCardFromUpperLabel;
    @FXML private Label extraFoodFromBonusLabel;

    @FXML private VBox toastContainer;

    @FXML private Label tribeOverlayTitle;

    @FXML private Button endTurnButton;

    /**
     * Initializes the board scene with disabled offer tiles and  cards.
     *
     * The offer track is populated later, when the server sends the initial
     * OfferTileDTO list.
     */
    @FXML
    public void initialize() {
        offerTrackContainer.setDisable(true);

        renderStaticCardBacks();

        initializeTribeOverlay();

        initializeTribeIconClicks();

        initializePlayerStats();
    }

    /**
     * Initializes the player statistics
     */
    private void initializePlayerStats() {
        totalPrestigePointsLabel.setText("0");
        currentFoodLabel.setText("0");
        totalSustenanceDiscountLabel.setText("0");
        totalBuildingsFoodDiscountLabel.setText("0");
        shamanStarsLabel.setText("0");
        updateBooleanLabel(extraCardFromUpperLabel, false);
        updateBooleanLabel(extraFoodFromBonusLabel, false);
    }

    /**
     * Initializes the tribe overlay close action and base visibility state.
     */
    private void initializeTribeOverlay() {
        if (tribeOverlay != null) {
            tribeOverlay.setVisible(false);
            tribeOverlay.setManaged(false);
        }
        if (closeTribeButton != null) {
            closeTribeButton.setOnAction(e -> closeTribeOverlay());
        }
    }

    /**
     * Binds click handlers on tribe icons to open the tribe overlay.
     */
    private void initializeTribeIconClicks() {
        bindTribeIconClick(artistsIcon);
        bindTribeIconClick(gatherersIcon);
        bindTribeIconClick(buildersIcon);
        bindTribeIconClick(huntersIcon);
        bindTribeIconClick(inventorsIcon);
        bindTribeIconClick(shamansIcon);
        bindTribeIconClick(buildingsIcon);
    }

    /**
     * Makes a tribe icon clickable and opens the tribe overlay on click.
     */
    private void bindTribeIconClick(ImageView icon) {
        if (icon == null) return;
        icon.setOnMouseClicked(e -> openTribeOverlay("YOUR TRIBE", myTribe));
        icon.setPickOnBounds(true);
        icon.setStyle("-fx-cursor: hand;");
    }

    /**
     * Renders temporary card backs in the upper and lower board rows in order to show times meanwhile the first BoardDTO is loading
     */
    private void renderStaticCardBacks() {
        upperCardsContainer.getChildren().clear();
        lowerCardsContainer.getChildren().clear();

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


    //Write only on the state lines (white one)
    @Override
    public void showNotification(String msg) {
        Platform.runLater(() -> setNotification(msg, false));
    }

    //Write on the error lines (red one)
    @Override
    public void showErrorMessage(String msg) {
        Platform.runLater(() -> {
            setNotification(msg, true);
            if(!cardSelectionEnabled) {
                setCardSelectionEnabled(true);
            }

            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(3),
                    ae -> {
                        setNotification("", true);
                    }
            ));

            timeline.setCycleCount(1);
            timeline.play();
        });
    }

    @Override
    public void setPlayersInfo(Map<String, Color> playersInfo) {
        this.playersInfo.clear();
        this.playersInfo.putAll(playersInfo);
    }

    @Override
    public void updatePlayersOrder(List<String> order) {
        Platform.runLater(() -> {
            playersContainer.getChildren().clear();

            for (int i = 0; i < order.size(); i++) {
                String nickname = order.get(i);

                ImageView playerImageView = new ImageView();
                playerImageView.setFitWidth(30);
                playerImageView.setFitHeight(30);
                playerImageView.setPreserveRatio(true);
                playerImageView.setSmooth(true);

                playerImageView.setMouseTransparent(true);

                try {
                    Color playerColor = playersInfo.get(nickname);
                    String imagePath = PLAYER_TOTEM.get(playerColor);
                    Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
                    playerImageView.setImage(image);
                } catch (Exception e) {
                    System.err.println("Image loading error for player: " + nickname);
                }

                Label playerLabel = new Label((i + 1) + ". " + nickname);
                playerLabel.setStyle("""
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-text-fill: WHITE;
                """);

                playerLabel.setMouseTransparent(true);

                HBox playerRow = new HBox(10);
                playerRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                playerRow.setMaxWidth(Double.MAX_VALUE);

                playerRow.setPickOnBounds(true);

                String baseStyle = """
                -fx-background-color: rgba(255,255,255,0.15);
                -fx-background-radius: 5;
                -fx-padding: 6 10 6 10;
                -fx-cursor: hand;
                """;
                playerRow.setStyle(baseStyle);

                playerRow.setOnMouseEntered(e -> playerRow.setStyle(baseStyle + "-fx-background-color: rgba(255,255,255,0.3);"));
                playerRow.setOnMouseExited(e -> playerRow.setStyle(baseStyle));

                playerRow.setOnMouseClicked(event -> {
                    if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                        handlePlayerClick(nickname);
                    }
                });

                playerRow.getChildren().addAll(playerImageView, playerLabel);
                playersContainer.getChildren().add(playerRow);
            }
        });
    }

    private void handlePlayerClick(String clickedNickname) {
        if (clickedNickname.equals(view.getMyNickname())) {
            return;
        }

        TribeStatusDTO tribe = otherPlayerTribes.get(clickedNickname);

        if (tribe != null) {
            openTribeOverlay(clickedNickname + "'S TRIBE", tribe);
        }
    }

    /**
    Method that creates the offerTrack, is it called just one time at the start of the game beacuse the offerTrack will never change
     */
    @Override
    public void displayOfferTrack(List<OfferTileDTO> tiles, int total) {
        Platform.runLater(() -> {
            offerTrackContainer.getChildren().clear();

            List <String> map_images = OFFER_TILE_IMAGES.get(total);


            for (int i = 0; i < map_images.size(); i++) {

                String imagePath = map_images.get(i);
                OfferTileDTO tile = tiles.get(i);

                offerTrackContainer.getChildren().add(createOfferTileButton(tile, imagePath));

            }

            setOfferTrackEnabled(false);
            });
        }

    /**
     *
      * Methot that creates the TurnOrerTile which is at the first position of the offerTrackContainer
     * TODO: Forse sarebbe meglio creare un container apposta per la turnOrderTile
     */
    @Override
    public void displayTurnOrderTile(List<TurnOrderTileDTO> turnOrderTile) {

        this.lastTurnOrderTile = turnOrderTile;

        Platform.runLater(() -> {

            turnOrderContainer.getChildren().clear();

            //if the conditions inside the if is true means that the number of player is not ready, the method will be re-called later
            if(totalPlayers <= 0 ) {
                return;
            }

            String imagePath = TURN_ORDER_TILE_IMAGES.get(totalPlayers);

            if (imagePath == null) {
                return;
            }

            //createStaticStartMapTile return a stackpane which is a static container, the player don't need to click on the turnOrderTile
            StackPane startTile = createStaticStartMapTile(imagePath);

            turnOrderContainer.getChildren().add(startTile);

        });
    }

    /**
        method that saves a new board when it comes from server and put it in "lastBoard", at this moment the player can't click it
     */
    public void displayBoardCards() {
        if (lastBoard == null) {
            renderStaticCardBacks();
            return;
        }

        upperCardsContainer.getChildren().clear();
        lowerCardsContainer.getChildren().clear();

        renderCardRow(upperCardsContainer, lastBoard.getUpperTribeRow(), "T");
        renderCardRow(upperCardsContainer, lastBoard.getUpperBuildingRow(), "B");

        renderCardRow(lowerCardsContainer, lastBoard.getLowerTribeRow(), "L");
        renderCardRow(lowerCardsContainer, lastBoard.getLowerBuildingRow(), "G");

        String imagePath = BACK_CARD_ERA.get(lastBoard.getFirstCardEra());
        if (imagePath == null) {
            return;
        }
        StackPane startTile = createStaticStartMapTile(imagePath);
        firstDeckCardContainer.getChildren().add(startTile);
    }

    /**
     * Adds all cards from a DTO row to a JavaFX container.
     */
    private void renderCardRow(HBox container, List<CardDTO> cards, String prefix) {
        for (int i = 0; i < cards.size(); i++) {
            container.getChildren().add(createBoardCard(cards.get(i), prefix, i));
        }
    }

    /**
    method that saves a new board when it comes from server and put it in "lastBoard", in this moment the player can't click it
     */
    @Override
    public void displayBoard(BoardDTO board) {
        Platform.runLater(() -> {
            this.lastBoard = board;
            this.cardSelectionEnabled = false;
            displayBoardCards();
        });
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
     */
    @Override
    public void displayChoosableCards() {
        Platform.runLater(() -> {
            setNotification("Your turn: choose a card position.", false);
            setCardSelectionEnabled(true);
        });
    }

    @Override
    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;

        //if lastTurnOrderTile is not null means that the methos displayTurnOrderTile was called when the number of player was not ready, so we have to re-call it
        if(lastTurnOrderTile != null) {
            displayTurnOrderTile(lastTurnOrderTile);
        }
    }

    /**
     * Updates the fixed tribe icon slots by setting each counter to the current size
     * of the corresponding category list contained in the received DTO
     */
    @Override
    public void showTribe(String playerNickname, TribeStatusDTO tribe) {
        if (!playerNickname.equals(view.getMyNickname())) {
            otherPlayerTribes.put(playerNickname, tribe);
            return;
        }

        this.myTribe = tribe;

        Map<String, List<CardDTO>> cols = tribe.getCharactersByColumn();

        updateTribeSlot("BUILDING", tribe.getBuildingIds(), buildingsIcon, buildingsCount);
        updateTribeSlot("ARTIST", cols.get("ARTISTS"), artistsIcon, artistsCount);
        updateTribeSlot("GATHERER", cols.get("GATHERERS"), gatherersIcon, gatherersCount);
        updateTribeSlot("BUILDER", cols.get("BUILDERS"), buildersIcon, buildersCount);
        updateTribeSlot("HUNTER", cols.get("HUNTERS"), huntersIcon, huntersCount);
        updateTribeSlot("INVENTOR", cols.get("INVENTORS"), inventorsIcon, inventorsCount);
        updateTribeSlot("SHAMAN", cols.get("SHAMANS"), shamansIcon, shamansCount);

        totalPrestigePointsLabel.setText(String.valueOf(tribe.getTotalPrestigePoints()));
        currentFoodLabel.setText(String.valueOf(tribe.getCurrentFood()));
        totalSustenanceDiscountLabel.setText(String.valueOf(tribe.getTotalSustenanceDiscount()));
        totalBuildingsFoodDiscountLabel.setText(String.valueOf(tribe.getTotalBuildingsFoodDiscount()));
        shamanStarsLabel.setText(String.valueOf(tribe.getShamanStars()));
        updateBooleanLabel(extraCardFromUpperLabel, tribe.hasExtraCardFromUpper());
        updateBooleanLabel(extraFoodFromBonusLabel, tribe.hasExtraFoodFromBonus());

        initializeTribeIconClicks();
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
            tileButton.setCursor(Cursor.DEFAULT);
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

        //if the tile is not available opacity is low, in order to make the name of player more visible
        if(!tile.isAvailable()){
            imageView.setOpacity(0.4);
        }

        tilePane.getChildren().add(imageView);

        // Add the player's nickname if the tile is occupied
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
     * Enables or disables card selection on the board. True to enable card selection, false to disable.
     */
    private void setCardSelectionEnabled(boolean enabled) {
        this.cardSelectionEnabled = enabled;
        displayBoardCards(); // Re-render cards to apply style changes
    }

    /**
     * Updates the top notification label.
     * If it's an error, it populates the log label.
     * If it's a standard message, it updates the main status and clears old logs.
     */
    private void setNotification(String msg, boolean error) {
        if (gameNotificationLabel == null || gameLogLabel == null) {
            return;
        }

        if (error) {
            String errorMsg = msg.isEmpty() ? msg : "Error: " + msg;
            gameLogLabel.setText(errorMsg);
        } else {

            gameNotificationLabel.setText(msg);
            gameNotificationLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        }
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

    /**
     * Creates a visual representation of a card on the board, making it interactive if card selection is enabled.
     *  * Uses LocalCardDictionary to resolve the card ID into the correct image path.
     */
    private StackPane createBoardCard(CardDTO card, String prefix, int index) {
        StackPane cardPane = new StackPane();
        cardPane.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        cardPane.setMinSize(CARD_WIDTH, CARD_HEIGHT);
        cardPane.setMaxSize(CARD_WIDTH, CARD_HEIGHT);

        //Start operations of translation
        String cardId = card.getCardId();

        // Asks the dictionary for the image path corresponding to the ID
        String imagePath = LocalCardDictionary.getInstance().getImagePath(cardId);

        // If the image path is null (empty slot or ID not found)
        if (imagePath == null || imagePath.equals("[Empty]")) {
            cardPane.setStyle(disabledCardStyle());
            cardPane.setOpacity(0); // Makes the slot completely transparent
            return cardPane;
        }

        ImageView imageView = createImageView(imagePath, CARD_WIDTH, CARD_HEIGHT);
        cardPane.getChildren().add(imageView);


        //Choose if a card is a building or a tribe card
        boolean isEventCard = cardId != null && cardId.startsWith("event");


        if (cardSelectionEnabled) {
            if (isEventCard) {
                cardPane.setStyle(nonSelectableCardStyle());
                cardPane.setOnMouseClicked(event -> {
                    showErrorMessage("You cannot select event cards.");
                });
            } else {
                    cardPane.setStyle(selectableCardStyle());

                    cardPane.setOnMouseClicked(event -> {
                        try {
                            setCardSelectionEnabled(false);

                            // Forwards the input (e.g., "T0") to the NetworkManager regex parser
                            network.cardSelection(prefix + index);
                        } catch (Exception e) {
                            setCardSelectionEnabled(true);
                            showErrorMessage(handleNetworkError(e));

                        }
                    });
            }
        } else { //if is not player turn, all card are not clickable
            cardPane.setStyle(disabledCardStyle());
            cardPane.setOnMouseClicked(null);
        }

        return cardPane;
    }

    /**
     * Opens the in-scene tribe overlay and renders all owned cards.
     */
    private void openTribeOverlay(String titleText, TribeStatusDTO tribe) {
        if (tribeOverlay == null || tribeCardsContainer == null || tribe == null) return;

        if (tribeOverlayTitle != null) {
            tribeOverlayTitle.setText("🎪 " + titleText.toUpperCase());
        }

        tribeCardsContainer.getChildren().clear();

        tribeCardsContainer.setAlignment(Pos.TOP_CENTER);

        Map<String, List<CardDTO>> cols = tribe.getCharactersByColumn();
        if (cols != null) {
            for (Map.Entry<String, List<CardDTO>> entry : cols.entrySet()) {
                String category = entry.getKey();
                List<CardDTO> cards = entry.getValue();
                tribeCardsContainer.getChildren().add(createTribeSection(category, cards));
            }
        }

        // Buildings section
        tribeCardsContainer.getChildren().add(createTribeSection("BUILDINGS", tribe.getBuildingIds()));

        tribeOverlay.setManaged(true);
        tribeOverlay.setVisible(true);
    }

    /**
     * Closes the in-scene tribe overlay.
     */
    private void closeTribeOverlay() {
        if (tribeOverlay == null)
            return;

        tribeOverlay.setVisible(false);
        tribeOverlay.setManaged(false);
    }


    /**
     * Modify the style of the cards, when "cardSelectionEnabled" is true the card id normal, when is false the card is visible but neutral
     *
     */
    private String selectableCardStyle() {
        return """
        -fx-background-color: #fff8e6;
        -fx-border-color: #d7b35a;
        -fx-border-width: 2;
        -fx-border-radius: 6;
        -fx-background-radius: 6;
        -fx-cursor: hand;
        -fx-padding: 3;
        -fx-opacity: 1.0;
        """;
    }

    private String disabledCardStyle() {
        return """
        -fx-background-color: transparent;
        -fx-border-color: transparent;
        -fx-border-width: 2;
        -fx-padding: 3;
        -fx-opacity: 0.6;
        """;
    }

    private String nonSelectableCardStyle() {
        return """
    -fx-background-color: transparent;
    -fx-border-color: transparent;
    -fx-border-width: 2;
    -fx-padding: 3;
    -fx-opacity: 0.6;
    -fx-cursor: not-allowed;
    """;
    }

    /**
     * Updates a single tribe slot with its icon and current count.
     *
     */
    private void updateTribeSlot(String categoryKey, List<CardDTO> cards, ImageView iconView, Label countLabel) {

        int count = (cards == null) ? 0 : cards.size();
        countLabel.setText(String.valueOf(count));

        String iconPath = "/images/Icons/" + categoryKey + "_ICON.png";
        ImageView loaded = createImageView(iconPath, 40, 40);
        if (loaded.getImage() != null) {
            iconView.setImage(loaded.getImage());
            iconView.setOpacity(1.0);
        }
    }

    /**
     * Creates one overlay section: a label plus an overlapped stack of card images.
     */
    private Node createTribeSection(String title, List<CardDTO> cards) {
        VBox section = new VBox(12);
        section.setAlignment(Pos.TOP_CENTER);
        section.setMinWidth(CARD_WIDTH + 16);
        section.setPrefWidth(CARD_WIDTH + 16);

        Label label = new Label(title.toUpperCase());
        label.setTextFill(javafx.scene.paint.Color.WHITE);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-letter-spacing: 0.5px;");

        Pane stack = createOverlappedCardStack(cards);

        section.getChildren().addAll(label, stack);
        return section;
    }

    /**
     * Renders cards as an overlapped vertical stack where only a small top strip is visible.
     */
    private Pane createOverlappedCardStack(List<CardDTO> cards) {
        final int stackOffsetPx = 24;

        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: transparent;");

        if (cards == null || cards.isEmpty()) {
            pane.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
            return pane;
        }

        double height = CARD_HEIGHT + (double) (cards.size() - 1) * stackOffsetPx;
        pane.setPrefSize(CARD_WIDTH, height);
        pane.setMinSize(CARD_WIDTH, height);
        pane.setMaxWidth(CARD_WIDTH);

        for (int i = 0; i < cards.size(); i++) {
            CardDTO dto = cards.get(i);
            if (dto == null) continue;

            String imagePath = LocalCardDictionary.getInstance().getImagePath(dto.getCardId());
            ImageView view = createImageView(imagePath, CARD_WIDTH, CARD_HEIGHT);
            view.setLayoutX(0);
            view.setLayoutY((double) i * stackOffsetPx);

            pane.getChildren().add(view);
        }

        return pane;
    }

    /**
     * Updates a boolean status label with a green checkmark or a red cross.
     */
    private void updateBooleanLabel(Label label, boolean isActive) {
        if (isActive) {
            label.setText("✓");
            label.setTextFill(javafx.scene.paint.Color.web("#4caf50")); // Verde JavaFX
        } else {
            label.setText("✕");
            label.setTextFill(javafx.scene.paint.Color.web("#f44336")); // Rosso JavaFX
        }
    }

    /**
     * Displays a toast-style notification in the top-right corner.
     * Stacks vertically and automatically disappears after 5 seconds.
     *
     * @param message The text message to display inside the toast
     */
    @Override
    public void showToast(String message) {
        HBox toast = new HBox();
        toast.setPadding(new javafx.geometry.Insets(10, 16, 10, 16));
        toast.setSpacing(10);
        toast.setAlignment(Pos.CENTER_LEFT);

        toast.setStyle(
                "-fx-background-color: rgba(33, 25, 22, 0.9);" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #f4d58d;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 4);"
        );

        Label label = new Label(message);
        label.setTextFill(javafx.scene.paint.Color.WHITE);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        label.setWrapText(true);

        Label icon = new Label("🔔");
        icon.setStyle("-fx-font-size: 14px;");

        toast.getChildren().addAll(icon, label);

        toast.setMouseTransparent(true);

        toastContainer.getChildren().addFirst(toast);

        FadeTransition fadeOut = new FadeTransition(javafx.util.Duration.millis(500), toast);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(e -> toastContainer.getChildren().remove(toast));

        Timeline timeline = new Timeline(
                new KeyFrame(javafx.util.Duration.seconds(6), event -> fadeOut.play())
        );
        timeline.play();
    }

    @Override
    public void showEndTurn() {
        endTurnButton.setVisible(true);
        endTurnButton.setManaged(true);

        showToast("You can only buy buildings. If you want to buy them, click on them; otherwise, click END TURN button.");
    }

    @FXML
    private void handleEndTurn(ActionEvent event) {
        System.out.println("Ending turn!");

        endTurnButton.setVisible(false);
        endTurnButton.setManaged(false);

        try {
            network.endTurnRequest();
        } catch (Exception ex) {
            endTurnButton.setVisible(true);
            endTurnButton.setManaged(true);
            showErrorMessage(handleNetworkError(ex));
        }
    }
}