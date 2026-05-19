package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.DTO.GlobalLeaderboardDTO;
import it.polimi.ingsw.network.DTO.GlobalPlayerRankDTO;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Controller class for the Global Leaderboard scene in the GUI.
 * <p>
 * This class handles the display of historical player rankings accumulated across multiple game sessions.
 * It populates a modern fluid list container using dedicated data transfer objects containing absolute positions,
 * player nicknames, and total points accumulated globally, ensuring all components are centered.
 * </p>
 *
 * @see SceneController
 */
public class GlobalLeaderboardController extends SceneController {

    /** Scrollable VBox container designated to host dynamically generated rows for the Hall of Fame. */
    @FXML private VBox globalLeaderboardRowsContainer;

    /**
     * Populates the global standings table with data packages retrieved from the server.
     * <p>
     * Iterates over the persistent records to build modern centered row cards inside the fluid container.
     * </p>
     *
     * @param globalLeaderboard data packet containing the comprehensive global rankings list
     */
    @Override
    public void displayGlobalLeaderboard(GlobalLeaderboardDTO globalLeaderboard) {
        // Clear old records to guarantee clean redraws
        globalLeaderboardRowsContainer.getChildren().clear();

        // Dynamically build and add each ranking card
        for (GlobalPlayerRankDTO dto : globalLeaderboard.getRankings()) {
            HBox row = createGlobalRankRow(dto);
            globalLeaderboardRowsContainer.getChildren().add(row);
        }
    }

    /**
     * Factory utility to programmatically build an elegant horizontal rank card.
     * Evenly distributes three dynamic columns and centers their inner text properties.
     *
     * @param dto data token representing global structural metrics for an single profile career
     * @return a fully styled, centered {@link HBox} element ready for node trees
     */
    private HBox createGlobalRankRow(GlobalPlayerRankDTO dto) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER); // Center elements vertically inside the card
        row.setPadding(new Insets(14.0, 20.0, 14.0, 20.0));

        // Base background matching the custom original charcoal #282828 and gold accenting
        String style = "-fx-background-radius: 12; -fx-border-radius: 12; ";
        if (dto.getRank() == 1) {
            // Premium radiant layout styling dedicated specifically to the overall global #1 master
            style += "-fx-background-color: rgba(241, 196, 15, 0.12); " +
                    "-fx-border-color: #f1c40f; " +
                    "-fx-border-width: 1.8;";
        } else {
            // Clean custom dark gray framework rows for standard database records
            style += "-fx-background-color: rgba(40, 40, 40, 0.65); " +
                    "-fx-border-color: rgba(241, 196, 15, 0.25); " +
                    "-fx-border-width: 1.0;";
        }
        row.setStyle(style);

        // --- EQUAL THREE-COLUMN DISTRIBUTION WITH TOTAL CENTERING ---

        // 1. POSITION / RANK COLUMN
        Label posLabel = new Label(dto.getRank() + "°");
        posLabel.setAlignment(Pos.CENTER);
        posLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(posLabel, Priority.ALWAYS); // Claim equal fractional horizontal row space
        posLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                (dto.getRank() == 1 ? "-fx-text-fill: #f1c40f;" : "-fx-text-fill: #bdc3c7;"));

        // 2. PLAYER IDENTIFIER COLUMN
        Label nameLabel = new Label(dto.getNickname());
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS); // Claim equal fractional horizontal row space
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        // 3. CUMULATIVE PRESTIGE SCORE COLUMN
        Label scoreLabel = new Label("✨ " + dto.getTotalPoints() + " PTS");
        scoreLabel.setAlignment(Pos.CENTER);
        scoreLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(scoreLabel, Priority.ALWAYS); // Claim equal fractional horizontal row space
        scoreLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 15px; -fx-font-weight: bold;");

        // Populate and combine components into unified tree branch layout
        row.getChildren().addAll(posLabel, nameLabel, scoreLabel);
        return row;
    }
}