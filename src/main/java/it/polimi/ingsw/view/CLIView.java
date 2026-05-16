package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.*;
import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.network.RemoteController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
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
    /** Map containing the most recent tribe status DTO for each player in the game, indexed by nickname. */
    private final Map<String, TribeStatusDTO> allTribes = new java.util.concurrent.ConcurrentHashMap<>();

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";

    private static final String CLEAR_SCREEN = "\033[H\033[2J";
    private static final int LEFT_COLUMN_WIDTH = 60;
    private volatile List<OfferTileDTO> lastOfferTrack = null;

    private String myNickname = "MY_NICKNAME_HERE";

    /**
     * Clears the terminal screen reliably across different operating systems.
     * Uses native commands for Windows and ANSI escape codes for Unix-like systems.
     */
    private void clearConsole() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Native clear screen for Windows CMD / PowerShell
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // ANSI clear screen for Linux / macOS
                System.out.print(CLEAR_SCREEN);
                System.out.flush();
            }
        } catch (Exception e) {
            // Ultimate fallback for unsupported IDE consoles: print 50 empty lines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    /**
     * Adds spaces to the right of a string to reach the target width.
     * It ignores hidden ANSI color codes to keep the layout perfectly aligned.
     */
    private String padRightAnsi(String s, int width) {
        String clean = s.replaceAll("\u001B\\[[;\\d]*m", "");
        int visibleLen = clean.length();

        if (visibleLen >= width) {
            return s;
        }
        return s + " ".repeat(width - visibleLen);
    }


    private String colorizeCard(String details, boolean isBuildingRow) {
        if (isBuildingRow) {
            return BLUE + "  " + details + RESET;
        }
        if (details.toUpperCase().startsWith("EVENT")) {
            return PURPLE + "  " + details + RESET;
        }
        return CYAN + "  " + details + RESET;
    }

    /**
     * Builds the layout lines for the left side of the screen (My Tribe, Board & Offers).
     */
    private List<String> buildLeftColumn() {
        List<String> lines = new java.util.ArrayList<>();

        lines.add(YELLOW + BOLD + "=".repeat(15) + " MY TRIBE " + "=".repeat(15) + RESET);
        if (myNickname != null && allTribes.containsKey(myNickname)) {
            lines.addAll(formatTribeBlock(myNickname, allTribes.get(myNickname), true));
        } else {
            lines.add(" Waiting for your tribe data...");
            lines.add("");
        }

        // 2. LA BOARD CENTRALE
        lines.add(YELLOW + BOLD + "=".repeat(17) + " BOARD " + "=".repeat(17) + RESET);
        lines.add("");

        if (lastBoard != null) {
            lines.add("Upper Tribe Row:");
            lastBoard.getUpperTribeRow().forEach(c -> lines.add(colorizeCard(LocalCardDictionary.getInstance().getCardDetails(c.getCardId()), false)));

            lines.add("\nLower Tribe Row:");
            lastBoard.getLowerTribeRow().forEach(c -> lines.add(colorizeCard(LocalCardDictionary.getInstance().getCardDetails(c.getCardId()), false)));

            lines.add(BLUE + "\nUpper Buildings:" + RESET);
            lastBoard.getUpperBuildingRow().forEach(c -> lines.add(colorizeCard(LocalCardDictionary.getInstance().getCardDetails(c.getCardId()), true)));

            lines.add(BLUE + "\nLower Buildings:" + RESET);
            lastBoard.getLowerBuildingRow().forEach(c -> lines.add(colorizeCard(LocalCardDictionary.getInstance().getCardDetails(c.getCardId()), true)));
        } else {
            lines.add(" Waiting for board data...");
        }

        lines.add("");
        lines.add(YELLOW + BOLD + "=".repeat(16) + " OFFERS " + "=".repeat(16) + RESET);
        lines.add("");

        if (lastOfferTrack != null) {
            for (OfferTileDTO tile : lastOfferTrack) {
                String owner = tile.getNickname() != null ? RED + "[" + tile.getNickname() + "]" + RESET : GREEN + "[Free]" + RESET;
                lines.add(String.format(" Tile %d %s : [Food] %d | Cards(Up/Dw): %d/%d",
                        tile.getIndex(), owner, tile.getFoodBonus(), tile.getTopRowDraws(), tile.getBottomRowDraws()));
            }
        } else {
            lines.add(" Waiting for offer track...");
        }

        return lines;
    }

    /**
     * Builds the layout lines for the right side of the screen (Opponents).
     */
    private List<String> buildRightColumn() {
        List<String> lines = new java.util.ArrayList<>();
        lines.add(YELLOW + BOLD + "=".repeat(15) + " OPPONENTS " + "=".repeat(15) + RESET);

        boolean hasOpponents = false;
        for (Map.Entry<String, TribeStatusDTO> entry : allTribes.entrySet()) {
            if (!entry.getKey().equals(myNickname)) {
                hasOpponents = true;
                lines.addAll(formatTribeBlock(entry.getKey(), entry.getValue(), false));
            }
        }

        if (!hasOpponents) {
            lines.add("");
            lines.add(" Waiting for other players...");
        }

        return lines;
    }

    /**
     * Formats a single tribe block into a list of strings.
     */
    private List<String> formatTribeBlock(String playerName, TribeStatusDTO t, boolean isMe) {
        List<String> lines = new java.util.ArrayList<>();
        String headerColor = isMe ? GREEN + BOLD : RED + BOLD;
        String title = isMe ? "> PLAYER: " : "> OPPONENT: ";

        lines.add("");
        lines.add(headerColor + title + playerName.toUpperCase() + RESET);
        lines.add(String.format("   " + GREEN + "[Food] %d" + RESET + " | " + YELLOW + "[VP] %d" + RESET + " | " + YELLOW + "[Stars] %d" + RESET + " | " + CYAN + "[Sust] -%d" + RESET + " | " + BLUE + "[Build] -%d" + RESET,
                t.getCurrentFood(), t.getTotalPrestigePoints(), t.getShamanStars(), t.getTotalSustenanceDiscount(), t.getTotalBuildingsFoodDiscount()));

        lines.add(CYAN + "   CHARACTERS:" + RESET);
        if (t.getCharactersByColumn() != null) {
            t.getCharactersByColumn().forEach((category, cardDTOs) -> {
                if (cardDTOs != null && !cardDTOs.isEmpty()) {
                    lines.add(String.format("     " + CYAN + "%s" + RESET + " (%d)", category, cardDTOs.size()));
                }
            });
        }

        int buildingsCount = (t.getBuildingIds() != null) ? t.getBuildingIds().size() : 0;
        lines.add(BLUE + "   BUILDINGS: " + RESET + buildingsCount + " built.");
        lines.add("   " + "-".repeat(50));

        return lines;
    }

    /**
     * Master rendering function. Clears the screen and zips the two columns together.
     */
    private synchronized void renderDashboard() {
        clearConsole();
        List<String> left = buildLeftColumn();
        List<String> right = buildRightColumn();

        int maxLines = Math.max(left.size(), right.size());

        for (int i = 0; i < maxLines; i++) {
            String lLine = (i < left.size()) ? left.get(i) : "";
            String paddedLeft = padRightAnsi(lLine, LEFT_COLUMN_WIDTH);

            String rLine = (i < right.size()) ? right.get(i) : "";

            System.out.println(paddedLeft + YELLOW + " | " + RESET + rLine);
        }
        System.out.println("\n");
    }

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
        this.myNickname = nickname;

        try {
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
     * <p>
     * This method clears the input buffer, reads the user's choice from the console,
     * and communicates the selection to the server. It includes recursive error
     * handling for invalid numerical input or network-related issues.
     * </p>
     *
     * @throws NumberFormatException if the user input is not a valid integer
     */
    @Override
    public void askTotemPlacement() {
        clearInputBuffer();

        System.out.print("Choose the offer tile: ");
        try {
            int tileIndex = Integer.parseInt(readLine());
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
        this.lastOfferTrack = tiles;
        renderDashboard();
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
     * Stores the received tribe status into a global map and prints a formatted,
     * colorful layout of all active players' tribes onto the console.
     * Uses the local card dictionary to translate unique identifiers into readable descriptions.
     *
     * @param nickname the nickname of the player owning the tribe
     * @param tribe    the updated TribeStatusDTO object
     */
    @Override
    public void showTribe(String nickname, TribeStatusDTO tribe) {
        if (nickname != null && tribe != null) {
            allTribes.put(nickname, tribe);
            renderDashboard();
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
     * to be displayed
     */
    public void displayBoard(BoardDTO board) {
        this.lastBoard = board;
        renderDashboard();
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
     * It actively reprints the entire board state using the LocalCardDictionary
     * so the user has all available options directly above the input prompt.
     */
    @Override
    public void askCardChoose() {
        clearInputBuffer();

        System.out.println("Format: [Row Prefix][Index] (e.g., T0 for first Upper Tribe, G2 for third Lower Building)");
        System.out.print("Enter your choice: ");
        String cardPosition = readLine().trim().toUpperCase();

        try {
            // Forwards the input (e.g., "T0") to the NetworkManager regex parser
            network.cardSelection(cardPosition);
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
            String posString = entry.getPosition() + ".";
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
     * current turn order; can be null or empty.
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
     * player rankings to be displayed; can be null or contain an empty list.
     */
    @Override
    public void displayGlobalLeaderboard(GlobalLeaderboardDTO leaderboard) {
        System.out.println("\n" + "+" + "-".repeat(58) + "+");
        System.out.println("|" + centerText("GLOBAL HALL OF FAME", 58) + "|");
        System.out.println("+" + "-".repeat(58) + "+");


        String header = String.format("  %-6s  %-25s  %15s", "RANK", "NICKNAME", "TOTAL POINTS");
        System.out.println("|" + String.format("%-58s", header) + "|");
        System.out.println("+" + "-".repeat(58) + "+");

        List<GlobalPlayerRankDTO> entries = leaderboard.getRankings();

        if (entries == null || entries.isEmpty()) {
            System.out.println("|" + centerText("No records found for this player count.", 58) + "|");
        } else {
            for (GlobalPlayerRankDTO entry : entries) {
                String row = String.format("  #%-5d  %-25s  %15d",
                        entry.getRank(),
                        entry.getNickname(),
                        entry.getTotalPoints());
                System.out.println("|" + String.format("%-58s", row) + "|");
            }
        }

        System.out.println("+" + "-".repeat(58) + "+\n");
    }
}