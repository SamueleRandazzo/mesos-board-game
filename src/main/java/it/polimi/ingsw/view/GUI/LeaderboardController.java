package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.DTO.LeaderboardDTO;
import it.polimi.ingsw.network.DTO.PlayerRankDTO;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Controller class for the final Leaderboard scene in the GUI.
 * <p>
 * This class handles the visualization of the match results inside a modern, dynamic
 * list layout, highlighting the winner(s), and managing the conditional interaction
 * logic to fetch and display global ranking databases with centered columns.
 * </p>
 *
 * @see SceneController
 */
public class LeaderboardController extends SceneController {

    /** Cache holding the total number of players registered in the completed game session. */
    private int playerSize = 0;

    /** Scrollable VBox container designated to host dynamically generated rows for each player rank. */
    @FXML private VBox leaderboardRowsContainer;

    /** Text label declaring the overall match winner or an eventual draw status. */
    @FXML private Label winnerText;

    /** Text area designated to output global database metrics and historical status messages. */
    @FXML private Label globalRankText;

    /** Interactive action button to request historical global rankings data from the server. */
    @FXML private Button globalRankButton;

    /**
     * Populates the graphical leaderboard components with the final standings data.
     */
    @Override
    public void displayLeaderboard(LeaderboardDTO leaderboard, String globalRankMessage) {
        playerSize = leaderboard.getRankings().size();

        // 1. Clean previous items to ensure clean redrawing
        leaderboardRowsContainer.getChildren().clear();

        // 2. Generate custom UI components for each ranking entry
        for (PlayerRankDTO dto : leaderboard.getRankings()) {
            HBox row = createRankRow(dto);
            leaderboardRowsContainer.getChildren().add(row);
        }

        updateWinnerLabel(leaderboard);

        // Manage visibility and layout budgeting of global rankings components
        if (globalRankMessage != null && !globalRankMessage.isEmpty()) {
            globalRankText.setText(globalRankMessage);

            globalRankButton.setVisible(true);
            globalRankButton.setManaged(true);
        } else {
            globalRankText.setText("");
            globalRankButton.setVisible(false);
            globalRankButton.setManaged(false);
        }
    }

    /**
     * Factory utility to programmatically build an elegant horizontal rank card.
     * Divides the row into 3 perfectly balanced and centered macro-columns.
     *
     * @param dto data token representing individual player status metrics
     * @return a fully styled, centered {@link HBox} element ready for node trees
     */
    private HBox createRankRow(PlayerRankDTO dto) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(14.0, 20.0, 14.0, 20.0));

        // Assemble modern inline graphics styling parameters
        String style = "-fx-background-radius: 12; -fx-border-radius: 12; ";
        if (dto.isWinner()) {
            style += "-fx-background-color: rgba(244, 213, 141, 0.14); " +
                    "-fx-border-color: rgba(244, 213, 141, 0.7); " +
                    "-fx-border-width: 2.0;";
        } else {
            style += "-fx-background-color: rgba(20, 20, 20, 0.55); " +
                    "-fx-border-color: rgba(244, 213, 141, 0.15); " +
                    "-fx-border-width: 1.0;";
        }
        row.setStyle(style);


        // 1: POSITION
        Label posLabel = new Label(dto.getPosition() + "°");
        posLabel.setAlignment(Pos.CENTER);
        posLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(posLabel, Priority.ALWAYS);
        posLabel.setStyle("-fx-font-family: 'System'; -fx-font-size: 18px; -fx-font-weight: 900; " +
                (dto.isWinner() ? "-fx-text-fill: #f4d58d;" : "-fx-text-fill: #b3b3b3;"));

        // 2: NICKNAME
        String nickname = dto.isWinner() ? dto.getNickname().toUpperCase() : dto.getNickname();
        Label nameLabel = new Label(nickname);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        // 3: FOOD & PP CONTAINER
        HBox statsContainer = new HBox();
        statsContainer.setAlignment(Pos.CENTER);
        statsContainer.setSpacing(25.0);
        statsContainer.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(statsContainer, Priority.ALWAYS);

        // PP
        Label prestigeLabel = new Label("🏆 " + dto.getPrestigePoints() + " P. Points");
        prestigeLabel.setStyle("-fx-text-fill: #f4d58d; -fx-font-size: 14px; -fx-font-weight: bold;");

        // FOOD
        Label foodLabel = new Label("🍖 " + dto.getFoodAmount() + " Food");
        foodLabel.setStyle("-fx-text-fill: #ffb3a7; -fx-font-size: 14px; -fx-font-weight: bold;");

        statsContainer.getChildren().addAll(prestigeLabel, foodLabel);

        row.getChildren().addAll(posLabel, nameLabel, statsContainer);
        return row;
    }

    /**
     * Handles the action event triggered when the user requests global standings information.
     */
    @FXML
    private void handleShowGlobalRankings() {
        try {
            network.seeGlobalLeaderboard(playerSize);
        } catch (Exception e) {
            showErrorMessage(handleNetworkError(e));
        }
    }

    /**
     * Internal utility parsing the leaderboard context to determine the proper victory headline.
     */
    private void updateWinnerLabel(LeaderboardDTO leaderboard) {
        if (leaderboard.isSharedVictory()) {
            winnerText.setText("IT'S A DRAW! VICTORY IS SHARED");
        } else if (!leaderboard.getRankings().isEmpty()) {
            winnerText.setText("PLAYER " + leaderboard.getRankings().getFirst().getNickname() + " is the winner!");
        }
    }
}