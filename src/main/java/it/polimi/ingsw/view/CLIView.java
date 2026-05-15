package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.*;
import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.network.RemoteController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.polimi.ingsw.exception.CustomException.cleanRemoteException;

/**
 * Command Line Interface (CLI) implementation of the game view.
 * <p>
 * This class handles all user interactions through the console, following the MVC pattern.
 * It displays the game state to the user and forwards user inputs to the server
 * via the {@link NetworkManager}.
 */
public class CLIView implements View {
    private final BufferedReader reader;
    private final NetworkManager network;
    private BoardDTO lastBoard;
    private Map<String, TribeStatusDTO> allTribesCache = new HashMap<>();
    private String myNickname;
    private String currentlyViewedPlayer;

    /**
     * Constructs a new CLIView.
     * * @param network the network manager used to communicate with the server.
     */
    public CLIView(NetworkManager network) {
        this.network = network;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    private void clearInputBuffer() {
        try {
            while (reader.ready()) {
                readLine();
            }
        } catch (IOException e) {
            // Silence
        }
    }

    private String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Displays the login screen and handles the nickname and color selection process.
     * This method blocks until a valid login request has been sent to the server.
     */
    @Override
    public void showLogin() {
        System.out.println("=== WELCOME TO MESOS ===");

        System.out.print("Enter your nickname: ");
        String nickname = readLine();

        System.out.println("Available colors: RED, BLUE, BLACK, YELLOW, WHITE");
        Color selectedColor = null;
        while (selectedColor == null) {
            System.out.print("Choose your color: ");
            try {
                selectedColor = Color.valueOf(readLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                showError("Invalid color. Please try again.");
            }
        }

        try {
            // --- ADDED FOR LOCAL IDENTITY ---
            this.myNickname = nickname;
            this.currentlyViewedPlayer = nickname;
            network.setNickname(nickname);

            System.out.println("Login request sent. Waiting for server confirmation...");
            network.login(selectedColor, nickname);
        } catch (Exception e) {
            showError(handleNetworkError(e));
            showLogin();
        }
    }

    /**
     * Updates the UI to show the current number of players connected to the lobby.
     * * @param current the current number of players in the lobby.
     * @param total   the total number of players required to start the game.
     */
    @Override
    public void showLobby(int current, int total) {
        System.out.println("Lobby Update: " + current + "/" + total + " players.");
    }

    /**
     * Notifies the user that the game has started.
     * * @param controller the remote controller provided by the server to handle game actions.
     */
    @Override
    public void startGame(RemoteController controller, int totalPlayers) {
        network.setController(controller);
        System.out.println("THE GAME HAS STARTED!");
    }

    /**
     * Displays an error message to the user.
     * @param error the error message to display.
     */
    @Override
    public void showError(String error) {
        System.out.println("\u001B[31m" + error + "\u001B[0m");
        System.out.flush();
    }

    /**
     * Displays a critical, unrecoverable error message on the CLI and terminates the application.
     * <p>
     * This method clears the current terminal line using ANSI escape codes, prints the provided
     * error message in bright red, and flushes the output stream to ensure immediate visibility.
     * It then briefly pauses execution to allow the OS stream to settle before forcefully
     * shutting down the JVM with an exit status of 1.
     * </p>
     *
     * @param error the descriptive error message to be displayed before termination.
     */
    @Override
    public void showFatalError(String error) {
        System.out.print("\r\033[K");
        System.out.println("\u001B[31m" + error + "\u001B[0m");
        System.out.flush();

        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        System.exit(1);
    }

    /**
     * Prompts the host player to set the maximum number of players for the game session.
     * Only the first player to join (the host) will trigger this method.
     */
    @Override
    public void askMaxPlayers() {
        System.out.print("You are the host! How many players do you want (2-5)? ");
        try {
            String input = readLine();
            int n = Integer.parseInt(input);
            network.setTotalPlayers(n);
        } catch (NumberFormatException e) {
            showError("Insert a valid number!");
            askMaxPlayers();
        } catch (Exception e) {
            showError(handleNetworkError(e));
            askMaxPlayers();
        }
    }

    /**
     * Prompts the user to select an offer tile for totem placement.
     */
    @Override
    public void askTotemPlacement() {
        clearInputBuffer();

        System.out.println("Commands: [index] OR 'view [name]' to inspect a tribe.");
        System.out.print("Choose the offer tile: ");
        try {
            String input = readLine().trim();

            // --- ADDED NAVIGATION CHECK ---
            if (input.toLowerCase().startsWith("view ")) {
                handleViewCommand(input.substring(5));
                askTotemPlacement();
                return;
            }

            int tileIndex = Integer.parseInt(input);
            network.tileSelection(tileIndex);
        } catch (NumberFormatException e) {
            showError("Insert a valid number!");
            askTotemPlacement();
        } catch (Exception e) {
            showError(handleNetworkError(e));
            askTotemPlacement();
        }
    }

    /**
     * Formats and prints the offer track table to the console.
     * It displays indices, bonuses, and the occupancy status of each tile.
     * @param tiles the list of data transfer objects containing tile information.
     */
    @Override
    public void displayOfferTrack(List<OfferTileDTO> tiles) {
        System.out.println("\n====================== OFFER TRACK ======================");

        System.out.printf("%-4s | %-6s | %-10s | %-10s | %-12s%n",
                "ID", "FOOD", "TOP DRAW", "BOT DRAW", "STATUS");
        System.out.println("---------------------------------------------------------");

        for (OfferTileDTO dto : tiles) {
            String status = dto.isAvailable() ? "FREE" : "[" + dto.getNickname() + "]";

            System.out.printf("%-4d | %-6d | %-10d | %-10d | %-12s%n",
                    dto.getIndex(),
                    dto.getFoodBonus(),
                    dto.getTopRowDraws(),
                    dto.getBottomRowDraws(),
                    status);
        }
        System.out.println("-".repeat(56));
    }

    private String handleNetworkError(Exception e) {
        if (e instanceof RemoteException) {
            return cleanRemoteException((RemoteException) e);
        } else {
            return e.getMessage().contains(": ")
                    ? e.getMessage().substring(e.getMessage().lastIndexOf(": ") + 2)
                    : e.getMessage();
        }
    }

    /**
     * Displays a generic message to the user via the standard output.
     *
     * @param message the string message to be printed
     */
    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays the current turn order of the players.
     * The list is formatted as a numbered vertical list for better readability in the terminal.
     *
     * @param playersOrder the list of player nicknames in their respective turn order
     */
    @Override
    public void showPlayersOrder(List<String> playersOrder) {
        String orderMessage = IntStream.range(0, playersOrder.size())
                .mapToObj(i -> (i + 1) + ". " + playersOrder.get(i))
                .collect(Collectors.joining("\n"));

        String finalMessage = "Players order is:\n" + orderMessage;

        System.out.println(finalMessage);
    }

    /**
     * Provides information about player-color associations.
     * <p>
     * Note: This implementation is empty as the CLI version of the game
     * does not currently utilize player colors for its display logic.
     * </p>
     *
     * @param playersInfo a map associating player nicknames with their chosen {@code Color}
     */
    @Override
    public void showPlayersInfo(Map<String,Color> playersInfo) {
        //CLI does not need to know player color
    }

    /**
     * Legacy support: updates the cache for the local player's tribe if a single update is sent.
     */
    @Override
    public void showTribe(TribeStatusDTO tribe) {
        allTribesCache.put(myNickname, tribe);
        if (currentlyViewedPlayer != null && currentlyViewedPlayer.equals(myNickname)) {
            displaySpecificTribe(myNickname);
        }
    }

    /**
     * Renders the current state of the game board to the console.
     * <p>
     * This method updates the local cache of the board state and prints a formatted
     * representation of the four main card rows: Upper Tribe, Lower Tribe,
     * Upper Building, and Lower Building.
     * </p>
     *
     * @param board the {@code BoardDTO} containing the current state of all card rows
     *              to be displayed
     */
    public void displayBoard(BoardDTO board) {
        this.lastBoard = board;
        System.out.println("\n======================== BOARD ========================");

        printRow("Upper Tribe Row (T)", board.getUpperTribeRow(), "T");
        printRow("Lower Tribe Row (L)", board.getLowerTribeRow(), "L");
        System.out.println("---------------------------------------------------------");
        printRow("Upper Building Row (B)", board.getUpperBuildingRow(), "B");
        printRow("Lower Building Row (G)", board.getLowerBuildingRow(), "G");

        System.out.println("-".repeat(56));
    }

    /**
     * Helper method to format and print a single row of cards.
     */
    private void printRow(String label, List<CardDTO> row, String prefix) {
        System.out.println(label + ":");
        if (row == null || row.isEmpty()) {
            System.out.println("  [Empty]");
            return;
        }
        for (int i = 0; i < row.size(); i++) {
            String id = row.get(i).getCardId();
            String description = LocalCardDictionary.getInstance().getCardDetails(id);
            System.out.printf("  [%s%d] %s%n", prefix, i, description);
        }
    }

    /**
     * Prompts the user to choose a card.
     */
    @Override
    public void askCardChoose() {
        clearInputBuffer();

        System.out.println("Format: [Row Prefix][Index] (e.g., T0 for first Upper Tribe, G2 for third Lower Building)");
        System.out.println("Commands: [Action] OR 'view [name]' to inspect a tribe.");
        System.out.print("Enter your choice: ");
        String input = readLine().trim();

        try {
            // --- ADDED NAVIGATION CHECK ---
            if (input.toLowerCase().startsWith("view ")) {
                handleViewCommand(input.substring(5));
                askCardChoose();
                return;
            }

            // --- ADDED SECURITY UI CHECK ---
            if (!currentlyViewedPlayer.equals(myNickname)) {
                showError("You cannot take cards while viewing an opponent! Type 'view me' to go back.");
                askCardChoose();
                return;
            }

            // Forwards the input to the NetworkManager parser
            network.cardSelection(input.toUpperCase());
        } catch (Exception e) {
            showError(handleNetworkError(e));

            if (lastBoard != null) {
                displayBoard(lastBoard);
                askCardChoose();
            }
        }
    }

    /**
     * Displays the final game rankings in the terminal.
     * The leaderboard includes the position, player nickname, prestige points, and food amount.
     * Winners are highlighted with special formatting.
     *
     * @param leaderboard the DTO containing the final standings and victory status
     */
    @Override
    public void displayLeaderboard(LeaderboardDTO leaderboard, String globalRank) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(centerText("FINAL LEADERBOARD", 60));
        System.out.println("=".repeat(60));

        // Table Header: %-5s (Position), %-20s (Nickname), %-12s (Prestige), %-8s (Food)
        String header = String.format("%-5s | %-20s | %-12s | %-8s", "POS", "PLAYER", "PRESTIGE", "FOOD");
        System.out.println(header);
        System.out.println("-".repeat(60));

        for (PlayerRankDTO entry : leaderboard.getRankings()) {
            String posString = entry.getPosition() + "°";
            String name = entry.getNickname();

            String row = String.format("%-5s | %-20s | %-12d | %-8d",
                    posString,
                    name,
                    entry.getPrestigePoints(),
                    entry.getFoodAmount());

            System.out.println(row);
        }

        System.out.println("-".repeat(60));

        // Final Message
        if (leaderboard.isSharedVictory()) {
            System.out.println(" It's a draw! Victory is shared among the leaders.");
        } else if (!leaderboard.getRankings().isEmpty()) {
            String winnerName = leaderboard.getRankings().getFirst().getNickname();
            System.out.println(" PLAYER  " + winnerName + " is the winner!");
        }

        System.out.println("=".repeat(60) + "\n");

        if (globalRank != null) {
            showMessage("\n--- GLOBAL RANKING ---");
            showMessage(globalRank);

            System.out.println("If you want to see the global leaderboard digit 'GLOBAL_LEADERBOARD'.");
            askGlobalLeaderboard(leaderboard.getRankings().size());
        }
    }

    /**
     * Displays the current turn order details in a structured, tabular format on the CLI.
     * <p>
     * This method prints a formatted table containing the position, player nickname,
     * associated color, and the specific food bonus for each tile. If the provided list
     * is null or empty, an appropriate message is displayed instead.
     * </p>
     *
     * @param turnOrderTiles the list of {@link TurnOrderTileDTO} objects representing the
     *                       current turn order; can be null or empty.
     */
    @Override
    public void displayTurnOrderTile(List<TurnOrderTileDTO> turnOrderTiles) {
        if (turnOrderTiles == null || turnOrderTiles.isEmpty()) {
            System.out.println("\n[TURN ORDER TILE]: No data available.");
            return;
        }

        System.out.println("=".repeat(56));
        System.out.println(centerText("TURN ORDER TILE", 56));
        System.out.println("=".repeat(56));
        System.out.printf("%-4s | %-15s | %-10s%n", "Pos", "Player", "Food bonus");
        System.out.println("-".repeat(56));

        int pos = 1;
        for (TurnOrderTileDTO tile : turnOrderTiles) {
            String colorString = tile.getColor() != null ? tile.getColor().toString() : "N/D";

            System.out.printf("%-4d | %-15s | %-10d%n",
                    pos,
                    tile.getNickname() + " (" + colorString + ")",
                    tile.getFoodBonus()
            );
            pos++;
        }
        System.out.println("=".repeat(56));
    }

    /**
     * Helper method to center text within a given width for better CLI aesthetics.
     *
     * @param text The string to center
     * @param width The total width of the line
     * @return The centered string padded with spaces
     */
    private String centerText(String text, int width) {
        if (text.length() >= width) return text;

        int totalPadding = width - text.length();
        int leftPadding = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;

        return " ".repeat(leftPadding) + text + " ".repeat(rightPadding);
    }

    private void askGlobalLeaderboard(int targetPlayers) {
        try {
            boolean ok = false;
            while (!ok) {
                String choice = readLine().trim().toLowerCase();
                if (choice.equalsIgnoreCase("GLOBAL_LEADERBOARD")) {
                    network.seeGlobalLeaderboard(targetPlayers);
                    ok = true;
                }
            }
        } catch (Exception e) {
            askGlobalLeaderboard(targetPlayers);
        }
    }

    /**
     * Displays the global leaderboard within a stylized box-framed layout on the CLI.
     * <p>
     * This method prints the "Global Hall of Fame", formatting the ranking entries into
     * a table with columns for rank position, player nickname, and total accumulated points.
     * If no ranking entries are present (or if the leaderboard data is null), it displays
     * a centered placeholder message indicating that no records were found.
     * </p>
     *
     * @param leaderboard the {@link GlobalLeaderboardDTO} object containing the list of
     *                    player rankings to be displayed; can be null or contain an empty list.
     */
    @Override
    public void displayGlobalLeaderboard(GlobalLeaderboardDTO leaderboard) {
        System.out.println("\n" + "╔" + "═".repeat(58) + "╗");
        System.out.println("║" + centerText("GLOBAL HALL OF FAME", 58) + "║");
        System.out.println("╠" + "═".repeat(58) + "╣");


        String header = String.format("  %-6s  %-25s  %15s", "RANK", "NICKNAME", "TOTAL POINTS");
        System.out.println("║" + String.format("%-58s", header) + "║");
        System.out.println("╠" + "═".repeat(58) + "╣");

        List<GlobalPlayerRankDTO> entries = leaderboard.getRankings();

        if (entries == null || entries.isEmpty()) {
            System.out.println("║" + centerText("No records found for this player count.", 58) + "║");
        } else {
            for (GlobalPlayerRankDTO entry : entries) {
                String row = String.format("  #%-5d  %-25s  %15d",
                        entry.getRank(),
                        entry.getNickname(),
                        entry.getTotalPoints());
                System.out.println("║" + String.format("%-58s", row) + "║");
            }
        }

        System.out.println("╚" + "═".repeat(58) + "╝\n");
    }

    /**
     * Local cache within the CLIView to store all players' status.
     */
    private Map<String, TribeStatusDTO> globalTribesCache = new HashMap<>();

    /**
     * Updates the local cache with the status of all tribes received from the server broadcast.
     * @param allTribes the global DTO containing every player's tribe status.
     */
    @Override
    public void showAllTribes(AllTribesStatusDTO allTribes) {
        this.allTribesCache = allTribes.getAllTribes();
        System.out.println("\n[System] All tribes data updated. Type 'view [name]' to inspect others.");

        // Refresh the current view if the user is already looking at a tribe
        if (allTribesCache.containsKey(currentlyViewedPlayer)) {
            displaySpecificTribe(currentlyViewedPlayer);
        }
    }


    /**
     * Renders a specific player's tribe to the console using the local dictionary.
     * @param nickname the nickname of the player to display.
     */
    private void displaySpecificTribe(String nickname) {
        TribeStatusDTO status = allTribesCache.get(nickname);
        if (status == null) {
            System.out.println("Tribe data for '" + nickname + "' is not available yet.");
            return;
        }

        boolean isMe = nickname.equals(myNickname);
        String title = isMe ? "YOUR TRIBE" : "TRIBE OF " + nickname.toUpperCase();

        System.out.printf("\n[FOOD: %d] | [PRESTIGE: %d] | [STARS: %d] | [SUSTENANCE DISCOUNT: %d] | [BUILDINGS DISCOUNT: %d]%n",
                status.getCurrentFood(),
                status.getTotalPrestigePoints(),
                status.getShamanStars(),
                status.getTotalSustenanceDiscount(),
                status.getTotalBuildingsFoodDiscount());

        System.out.println("\n" + "=".repeat(22) + " " + title + " " + "=".repeat(22));

        System.out.println(" CHARACTERS:");
        status.getCharactersByColumn().forEach((category, cardDTOs) -> {
            if (!cardDTOs.isEmpty()) {
                String joinedIds = cardDTOs.stream()
                        .map(dto -> LocalCardDictionary.getInstance().getCardDetails(dto.getCardId()))
                        .collect(Collectors.joining(", "));

                System.out.printf("  %-12s: %s%n", category, joinedIds);
            }
        });

        System.out.println("-".repeat(56));

        System.out.println(" BUILDINGS:");
        if (status.getBuildingIds().isEmpty()) {
            System.out.println("  [None]");
        } else {
            for (int i = 0; i < status.getBuildingIds().size(); i++) {
                String cardDesc = LocalCardDictionary.getInstance().getCardDetails(status.getBuildingIds().get(i).getCardId());
                System.out.print(cardDesc);
                if (i < status.getBuildingIds().size() - 1) System.out.print(" | ");
                if ((i + 1) % 4 == 0) System.out.print("\n");
            }
            System.out.println();
        }

        System.out.println("=".repeat(56) + "\n");

        if (!isMe) {
            System.out.println("(Viewing Opponent - Type 'view me' to return to your tribe)");
        }
    }

    /**
            * Helper to switch the CLI focus between players.
     * @param target the player name or 'me' keyword.
     */
    private void handleViewCommand(String target) {
        if (target.equalsIgnoreCase("me")) {
            this.currentlyViewedPlayer = myNickname;
        } else {
            this.currentlyViewedPlayer = target;
        }
        displaySpecificTribe(currentlyViewedPlayer);
    }
}
