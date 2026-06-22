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
import java.util.concurrent.CopyOnWriteArrayList;
import static it.polimi.ingsw.exception.CustomException.cleanRemoteException;

/**
 * Command Line Interface (CLI) implementation of the game view.
 * <p>
 * This class handles all user interactions through the console, following the MVC pattern.
 * It displays the game state to the user and forwards user inputs to the server
 * via the {@link NetworkManager}. All visuals are constrained to standard ASCII.
 * * @author YourName
 * @version 1.0
 */
public class CLIView implements View {
    /** Reader used to capture user input from the standard input stream. */
    private final BufferedReader reader;

    /** The network manager used to communicate with the server. */
    private final NetworkManager network;

    /** Cache of the last received board state. */
    private BoardDTO lastBoard;

    /** Thread-safe map storing the status of all tribes in the game, indexed by player nickname. */
    private final Map<String, TribeStatusDTO> allTribes = new java.util.concurrent.ConcurrentHashMap<>();

    /** * Local map used to translate dynamic user-friendly letters ('A', 'B', 'C')
     * into server-expected regex coordinates ('T0', 'L1', 'B0', 'G1').
     */
    private volatile Map<String, String> letterToCodeMap = new java.util.concurrent.ConcurrentHashMap<>();

    /** Stores the last error message to ensure it survives console clearing events. */
    private volatile String lastErrorMessage = null;

    /** ANSI Escape Codes to format text in the console */
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";

    /** ANSI sequence to clear the screen and home the cursor. */
    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    /** Fixed width for the left column layout to ensure visual alignment. */
    private static final int LEFT_COLUMN_WIDTH = 80;

    /** Cache of the last received offer track state. */
    private volatile List<OfferTileDTO> lastOfferTrack = null;

    /** The nickname of the player associated with this CLI client view. */
    private String myNickname = "MY_NICKNAME_HERE";

    private volatile List<TurnOrderTileDTO> lastTurnOrderTiles = null;

    /** List containing active live event notifications. */
    private volatile List<String> activeEventMessages = new CopyOnWriteArrayList<>();

    /** Cached list containing the order of active players for the current game session. */
    private volatile List<String> lastPlayersOrder = null;

    /**
     * Clears the terminal screen reliably across different operating systems.
     * Uses OS-specific terminal commands if possible, falling back to printing blank lines.
     */
    private void clearConsole() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print(CLEAR_SCREEN);
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    /**
     * Adds spaces to the right of a string to reach the target width,
     * ignoring ANSI color codes when calculating the visible length.
     *
     * @param s     The string to pad.
     * @param width The target visible width.
     * @return The padded string.
     */
    private String padRightAnsi(String s, int width) {
        String clean = s.replaceAll("\u001B\\[[;\\d]*m", "");
        int visibleLen = clean.length();
        if (visibleLen >= width) {
            return s;
        }
        return s + " ".repeat(width - visibleLen);
    }

    /**
     * Colorizes the card text description based on its content type.
     *
     * @param details       The text details of the card.
     * @param isBuildingRow True if the card belongs to a building row, false otherwise.
     * @return The colorized string ready for terminal display.
     */
    private String colorizeCard(String details, boolean isBuildingRow) {
        if (isBuildingRow) {
            return ANSI_GREEN + details + RESET;
        }
        if (details.startsWith("Hunt") || details.startsWith("Sustenance") ||
                details.startsWith("Shamanic Ritual") || details.startsWith("Cave Paintings") ||
                details.startsWith("Event")) {
            return ANSI_PURPLE + details + RESET;
        }
        return ANSI_CYAN + details + RESET;
    }

    /**
     * Generates sequential letters (A, B... Z, AA, AB...) for indexing choices
     * based on a 0-indexed integer.
     *
     * @param index The 0-based index of the option.
     * @return The spreadsheet-style letter label representing the index.
     */
    private String getLetterLabel(int index) {
        int quotient = index / 26;
        int remainder = index % 26;
        char letter = (char) ('A' + remainder);
        if (quotient == 0) {
            return String.valueOf(letter);
        } else {
            return String.valueOf((char) ('A' + quotient - 1)) + letter;
        }
    }

    /**
     * Builds the left column of the dashboard containing the Board, and the Offer Track and the Turn Order Tile.
     * Dynamically assigns single-letter selection codes to draftable cards in a thread-safe manner.
     *
     * @return A list of strings representing the formatted left column lines.
     */
    private List<String> buildLeftColumn() {
        List<String> lines = new java.util.ArrayList<>();
        Map<String, String> tempLetterMap = new java.util.concurrent.ConcurrentHashMap<>();
        int letterIndex = 0;

        lines.add("");

        // TURN ORDER TILE
        lines.add(ANSI_YELLOW + BOLD + "=".repeat(30) + " TURN ORDER TILE " + "=".repeat(31) + RESET);
        lines.add("");

        if (lastTurnOrderTiles != null && !lastTurnOrderTiles.isEmpty() && lastPlayersOrder != null && !lastPlayersOrder.isEmpty()) {
            lines.add(" Tile | Food | Player ");
            lines.add(" " + "-".repeat(77));

            int pos = 1;

            for (TurnOrderTileDTO tile : lastTurnOrderTiles) {
                lines.add(String.format(" %-4d | %-4d | %s", pos, tile.getFoodBonus(), lastPlayersOrder.get(pos-1)));
                pos++;
            }
        } else {
            lines.add(" Waiting for turn order data...");
        }

        // BOARD
        lines.add("");
        lines.add(ANSI_YELLOW + BOLD + "=".repeat(35) + " BOARD " + "=".repeat(36) + RESET);
        lines.add("");

        if (lastBoard != null) {
            // --- UPPER TRIBE ROW (Server code: T) ---
            lines.add("Upper Tribe Row:");
            for (int i = 0; i < lastBoard.getUpperTribeRow().size(); i++) {
                CardDTO c = lastBoard.getUpperTribeRow().get(i);
                String details = LocalCardDictionary.getInstance().getCardDetails(c.getCardId());

                boolean isEvent = details.startsWith("Hunt") || details.startsWith("Sustenance") ||
                        details.startsWith("Shamanic Ritual") || details.startsWith("Cave Paintings") ||
                        details.startsWith("Event");
                if (isEvent) {
                    lines.add("      " + colorizeCard(details, false));
                } else {
                    String letterStr = getLetterLabel(letterIndex++);
                    lines.add("  [" + letterStr + "] " + colorizeCard(details, false));
                    tempLetterMap.put(letterStr, "T" + i);
                }
            }

            // --- LOWER TRIBE ROW (Server code: L) ---
            lines.add("");
            lines.add("Lower Tribe Row:");
            for (int i = 0; i < lastBoard.getLowerTribeRow().size(); i++) {
                CardDTO c = lastBoard.getLowerTribeRow().get(i);
                String details = LocalCardDictionary.getInstance().getCardDetails(c.getCardId());

                boolean isEvent = details.startsWith("Hunt") || details.startsWith("Sustenance") ||
                        details.startsWith("Shamanic Ritual") || details.startsWith("Cave Paintings") ||
                        details.startsWith("Event");
                if (isEvent) {
                    lines.add("      " + colorizeCard(details, false));
                } else {
                    String letterStr = getLetterLabel(letterIndex++);
                    lines.add("  [" + letterStr + "] " + colorizeCard(details, false));
                    tempLetterMap.put(letterStr, "L" + i);
                }
            }

            // --- UPPER BUILDINGS (Server code: B) ---
            lines.add("");
            lines.add("Upper Buildings Row:");
            for (int i = 0; i < lastBoard.getUpperBuildingRow().size(); i++) {
                CardDTO c = lastBoard.getUpperBuildingRow().get(i);
                String details = LocalCardDictionary.getInstance().getCardDetails(c.getCardId());
                String letterStr = getLetterLabel(letterIndex++);

                lines.add("  [" + letterStr + "] " + colorizeCard(details, true));
                tempLetterMap.put(letterStr, "B" + i);
            }

            // --- LOWER BUILDINGS (Server code: G)
            lines.add("");
            lines.add("Lower Buildings Row:");
            for (int i = 0; i < lastBoard.getLowerBuildingRow().size(); i++) {
                CardDTO c = lastBoard.getLowerBuildingRow().get(i);
                String details = LocalCardDictionary.getInstance().getCardDetails(c.getCardId());
                String letterStr = getLetterLabel(letterIndex++);

                lines.add("  [" + letterStr + "] " + colorizeCard(details, true));
                tempLetterMap.put(letterStr, "G" + i);
            }
        } else {
            lines.add(" Waiting for board data...");
        }

        // Atomically replace the map to prevent Race Conditions during input reading
        this.letterToCodeMap = tempLetterMap;

        lines.add("");
        // 5. OFFERS
        lines.add(ANSI_YELLOW + BOLD + "=".repeat(32) + " OFFER TRACK " + "=".repeat(32) + RESET);
        lines.add("");

        if (lastOfferTrack != null) {
            lines.add(" Tile | Food | Draw Top/Btm | Player");
            lines.add(" " + "-".repeat(76));
            for (OfferTileDTO tile : lastOfferTrack) {
                String owner = tile.getNickname() != null ? ANSI_RED + "[" + tile.getNickname() + "]" + RESET : ANSI_GREEN + "[Free]" + RESET;
                lines.add(String.format(" %d    | %d    | %d / %d        | %s",
                        tile.getIndex(), tile.getFoodBonus(), tile.getTopRowDraws(), tile.getBottomRowDraws(), owner));
            }
        } else {
            lines.add(" Waiting for offer track...");
        }

        return lines;
    }

    /**
     * Builds the right column of the dashboard containing all Tribes info.
     *
     * @return A list of strings representing the formatted right column lines.
     */
    private List<String> buildRightColumn() {
        List<String> lines = new java.util.ArrayList<>();

        // MY TRIBE
        lines.add("");
        lines.add(ANSI_YELLOW + BOLD + "=".repeat(30) + " MY TRIBE " + "=".repeat(30) + RESET);
        if (myNickname != null && allTribes.containsKey(myNickname)) {
            lines.addAll(formatTribeBlock(myNickname, allTribes.get(myNickname), true));
        } else {
            lines.add(" Waiting for your tribe data...");
        }
        lines.add("");


        //OPPONENTS
        lines.add(ANSI_YELLOW + BOLD + "=".repeat(30) + " OPPONENTS " + "=".repeat(30) + RESET);

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
     * Formats a single player's tribe status data block into text lines.
     * Extracts and maps card information into human-readable strings.
     *
     * @param playerName The name of the player whose tribe is being formatted.
     * @param t          The status DTO containing resource levels and cards.
     * @param isMe       True if the tribe belongs to the current user, false if an opponent.
     * @return A list of formatted descriptive strings for that specific tribe.
     */
    private List<String> formatTribeBlock(String playerName, TribeStatusDTO t, boolean isMe) {
        List<String> lines = new java.util.ArrayList<>();
        String headerColor = isMe ? ANSI_GREEN + BOLD : ANSI_RED + BOLD;
        String title = isMe ? "> PLAYER: " : "> OPPONENT: ";

        lines.add("");
        lines.add(headerColor + title + playerName.toUpperCase() + RESET);
        lines.add(String.format("   " + ANSI_RED +  " Food " + RESET + "|" + ANSI_YELLOW + " PP " + RESET + "|"
                        + ANSI_PURPLE + " Shaman Stars " + RESET + "|" + ANSI_CYAN + " Sust " + RESET + "|" + ANSI_CYAN + " Build " + RESET + "|" + ANSI_CYAN + " Extra Draw " + RESET + "|" + ANSI_CYAN + " Food Bonus " + RESET));
        lines.add(String.format("    %-4d | %-2d | %-12d | %-4d | %-5d | %-10s | %-10s",
                                t.getCurrentFood(), t.getTotalPrestigePoints(), t.getShamanStars(),
                                -t.getTotalSustenanceDiscount(), -t.getTotalBuildingsFoodDiscount(), t.hasExtraCardFromUpper() ? "Yes" : "No", t.hasExtraFoodFromBonus() ? "Yes" : "No"));

        // --- CHARACTERS ---
        lines.add("");
        lines.add("   CHARACTERS:");
        boolean hasChars = false;
        if (t.getCharactersByColumn() != null) {
            for (List<CardDTO> cardDTOs : t.getCharactersByColumn().values()) {
                if (cardDTOs != null && !cardDTOs.isEmpty()) {
                    for (CardDTO c : cardDTOs) {
                        String details = LocalCardDictionary.getInstance().getCardDetails(c.getCardId());
                        lines.add("     - " + colorizeCard(details, false));
                        hasChars = true;
                    }
                }
            }
        }
        if (!hasChars) {
            lines.add(ANSI_RED + "     None" + RESET);
        }

        // --- BUILDINGS ---
        lines.add("");
        lines.add("   BUILDINGS:");
        if (t.getBuildingIds() != null && !t.getBuildingIds().isEmpty()) {
            for (CardDTO c : t.getBuildingIds()) {
                String details = LocalCardDictionary.getInstance().getCardDetails(c.getCardId());
                lines.add("     - " + colorizeCard(details, true));
            }
        } else {
            lines.add(ANSI_RED + "     None built." + RESET);
        }

        return lines;
    }

    /**
     * Master rendering function. Clears the console screen and side-by-side merges
     * the text lines generated from both the left and right columns.
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

            System.out.println(paddedLeft + BOLD + " | " + RESET + rLine);
        }
        System.out.println("\n");

        // --- EVENT NOTIFICATIONS ---
        if (!activeEventMessages.isEmpty()) {
            System.out.println(ANSI_PURPLE + BOLD + "--- RECENT GAME EVENTS ---" + RESET);
            for (String msg : activeEventMessages) {
                System.out.println(" " + msg);
            }
            System.out.println();
        }

        // Appends the error message to the bottom of the board so it doesn't get cleared
        if (lastErrorMessage != null) {
            System.out.println(ANSI_RED + BOLD + "[ERROR]: " + lastErrorMessage + RESET);
            lastErrorMessage = null;
        }
    }

    /**
     * Constructs a new CLI View linked to the specified network manager.
     *
     * @param network The network manager instance used for handling remote calls.
     */
    public CLIView(NetworkManager network) {
        this.network = network;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Discards any residual character bytes currently waiting in the input buffer
     * to prevent stale inputs from automatically submitting actions.
     */
    private void clearInputBuffer() {
        try {
            while (reader.ready()) {
                readLine();
            }
        } catch (IOException e) {
            // Silence
        }
    }

    /**
     * Reads a full text line input from the reader console.
     *
     * @return The text entered by the user, or an empty string if an error occurs.
     */
    private String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Safely translates the user's flexible selection (e.g. letters) into a clean,
     * single-space separated coordinate string expected by the server backend (e.g., "T0").
     * If the text already contains standard numbers/coordinates, it formats them cleanly.
     *
     * @param input The raw input string typed by the user.
     * @return A space-separated backend coordinate code string.
     */
    private String translateInputToServerCode(String input) {
        // If the user inputs legacy coordinates with numbers (e.g., "T0" or "T0, G1")
        if (input.matches(".*\\d.*")) {
            return input.replaceAll("[,;]", " ").replaceAll("\\s+", " ").trim();
        }

        StringBuilder translated = new StringBuilder();

        // Iterates through every character to extract the letters flawlessly
        for (char c : input.toCharArray()) {
            if (Character.isLetter(c)) {
                String letter = String.valueOf(c);
                // Fallback if letter not found
                translated.append(letterToCodeMap.getOrDefault(letter, letter)).append(" ");
            }
        }

        return translated.toString().trim();
    }

    /**
     * Prompts the current active user to choose a card from the display grid.
     * Forwards the parsed code directly to the network architecture.
     */
    @Override
    public void askCardChoose() {
        // Clear past events if present
        if (!activeEventMessages.isEmpty())
            activeEventMessages.clear();

        clearInputBuffer();

        System.out.print("Enter your choice (e.g. A) or manual code (e.g. T0): ");
        String input = readLine().trim().toUpperCase();

        // Translates the chosen letter into the server format immediately
        String serverCode = translateInputToServerCode(input);

        try {
            // Forwards the immediate selection straight to the server Model
            network.cardSelection(serverCode);
        } catch (Exception e) {
            showError(handleNetworkError(e));
            if (lastBoard != null) {
                displayBoard(lastBoard);
                askCardChoose();
            }
        }
    }

    /**
     * Renders the welcoming onboarding login text interface. Collects the player's
     * custom nickname and unique pawn token color setup selection.
     */
    @Override
    public void showLogin() {
        System.out.println(ANSI_YELLOW + BOLD + "=".repeat(30) + " WELCOME TO MESOS" + "=".repeat(30) + RESET);
        System.out.print("Enter your nickname: ");
        String nickname = readLine();

        System.out.println("Available colors: " + ANSI_RED + "RED " + ANSI_CYAN + "BLUE " + ANSI_PURPLE + "BLACK " + ANSI_YELLOW + "YELLOW " + BOLD + "WHITE" + RESET);
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
     * Updates and logs the current sizing details of the pre-game matchmaking room.
     *
     * @param current Current connected player count.
     * @param total   Total players required to automatically initialize the game match.
     */
    @Override
    public void showLobby(int current, int total) {
        System.out.println("Lobby Update: " + current + "/" + total + " players.");
    }

    /**
     * Signals that the game has successfully launched and injects the controller reference.
     *
     * @param controller   The remote controller instance for action dispatching.
     * @param totalPlayers Total amount of users involved.
     */
    @Override
    public void startGame(RemoteController controller, int totalPlayers) {
        network.setController(controller);
        System.out.println("THE GAME HAS STARTED!");
    }

    /**
     * Stores and appends an error notification log securely onto the dashboard console view.
     *
     * @param error String payload detailing the operational error.
     */
    @Override
    public void showError(String error) {
        this.lastErrorMessage = error;
        // Prints it immediately in case a screen refresh doesn't immediately follow
        System.out.println(ANSI_RED + BOLD + "[ERROR]: " + error + RESET);
        System.out.flush();
    }

    /**
     * Handles unrecoverable critical failures, printing the message and terminating the runtime.
     *
     * @param error The details of the fatal error.
     */
    @Override
    public void showFatalError(String error) {
        System.out.print("\r\033[K");
        System.out.println(ANSI_RED + BOLD + "[FATAL ERROR]: " + error + RESET);
        System.out.flush();
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
        System.exit(1);
    }

    /**
     * Prompts the lobby room's master host client to explicitly determine total game player seats.
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
     * Prompts the current player to input a valid target index to position their action totem.
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
     * Updates the local cache of the offer track state data and triggers a screen refresh.
     *
     * @param tiles A collection of {@link OfferTileDTO} components.
     */
    @Override
    public void displayOfferTrack(List<OfferTileDTO> tiles) {
        this.lastOfferTrack = tiles;
        renderDashboard();
    }

    /**
     * Normalizes and cleans localized or remote exception messaging headers.
     *
     * @param e The exception caught.
     * @return Clean text string describing the failure cause.
     */
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
     * Outputs a standard message line directly to the terminal system out stream.
     *
     * @param message Text string to display.
     */
    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Caches the full initial turn order list mapping and triggers a console
     * dashboard refresh to display it inside the CLI.
     *
     * @param playersOrder An ordered list containing player nicknames.
     */
    @Override
    public void showPlayersOrder(List<String> playersOrder) {
        this.lastPlayersOrder = playersOrder;
        renderDashboard();
    }

    /**
     * Callback method for visualizing maps of active player tokens and colors.
     * Currently left blank as color states are managed natively within individual dashboards.
     *
     * @param playersInfo Map detailing player names and token assignments.
     */
    @Override
    public void showPlayersInfo(Map<String,Color> playersInfo) {}

    /**
     * Integrates or updates the specific tribe state of a player and triggers a screen refresh.
     *
     * @param nickname The username key associated with the status update.
     * @param tribe    The structured status block values.
     */
    @Override
    public void showTribe(String nickname, TribeStatusDTO tribe) {
        if (nickname != null && tribe != null) {
            allTribes.put(nickname, tribe);
            renderDashboard();
        }
    }

    /**
     * Caches the central board state information and completely re-renders the dashboard UI.
     *
     * @param board The updated board state DTO.
     */
    public void displayBoard(BoardDTO board) {
        this.lastBoard = board;
        renderDashboard();
    }

    /**
     * Generates and outputs the structured end-of-game local scoreboard table summary.
     * Offers options to query and display global database scoreboard records if requested.
     *
     * @param leaderboard Match records structure detailing rankings, points, and final standing tallies.
     * @param globalRank  Optional text data illustrating global player status summaries.
     */
    @Override
    public void displayLeaderboard(LeaderboardDTO leaderboard, String globalRank) {
        clearConsole();
        System.out.println("\n" + "=".repeat(60));
        System.out.println(centerText("FINAL LEADERBOARD", 60));
        System.out.println("=".repeat(60));
        System.out.printf("%-5s | %-20s | %-12s | %-8s%n", "POS", "PLAYER", "PRESTIGE", "FOOD");
        System.out.println("-".repeat(60));

        for (PlayerRankDTO entry : leaderboard.getRankings()) {
            String posString = entry.getPosition() + ".";
            System.out.printf("%-5s | %-20s | %-12d | %-8d%n", posString, entry.getNickname(), entry.getPrestigePoints(), entry.getFoodAmount());
        }
        System.out.println("-".repeat(60));

        if (leaderboard.isSharedVictory()) {
            System.out.println(" It's a draw! Victory is shared among the leaders.");
        } else if (!leaderboard.getRankings().isEmpty()) {
            System.out.println(" PLAYER  " + leaderboard.getRankings().getFirst().getNickname() + " is the winner!");
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
     * Updates the local cache of the turn order tiles and triggers a screen refresh.
     *
     * @param turnOrderTiles A collection of {@link TurnOrderTileDTO} components.
     */
    @Override
    public void displayTurnOrderTile(List<TurnOrderTileDTO> turnOrderTiles) {
        this.lastTurnOrderTiles = turnOrderTiles;
        renderDashboard();
    }

    /**
     * Utility alignment assistant that returns a center-aligned version of a string
     * relative to a targeted bounding box length.
     *
     * @param text  The source text string.
     * @param width Target bounding padding container width.
     * @return Center-aligned space-padded string.
     */
    private String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int totalPadding = width - text.length();
        int leftPadding = totalPadding / 2;
        return " ".repeat(leftPadding) + text + " ".repeat(totalPadding - leftPadding);
    }

    /**
     * Listens for the explicit 'GLOBAL_LEADERBOARD' terminal keyword to submit
     * a remote server query asking for permanent database ranking records.
     *
     * @param targetPlayers The player scale count filter category.
     */
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
     * Renders the persistent historical Global Hall of Fame records list.
     *
     * @param leaderboard The network wrapper enclosing the comprehensive top global players list.
     */
    @Override
    public void displayGlobalLeaderboard(GlobalLeaderboardDTO leaderboard) {
        System.out.println("\n" + "+" + "-".repeat(58) + "+");
        System.out.println("|" + centerText("GLOBAL HALL OF FAME", 58) + "|");
        System.out.println("+" + "-".repeat(58) + "+");
        System.out.println("|" + String.format("%-58s", String.format("  %-6s  %-25s  %15s", "RANK", "NICKNAME", "TOTAL POINTS")) + "|");
        System.out.println("+" + "-".repeat(58) + "+");

        List<GlobalPlayerRankDTO> entries = leaderboard.getRankings();
        if (entries == null || entries.isEmpty()) {
            System.out.println("|" + centerText("No records found for this player count.", 58) + "|");
        } else {
            for (GlobalPlayerRankDTO entry : entries) {
                System.out.println("|" + String.format("%-58s", String.format("  #%-5d  %-25s  %15d", entry.getRank(), entry.getNickname(), entry.getTotalPoints())) + "|");
            }
        }
        System.out.println("+" + "-".repeat(58) + "+\n");
    }

    /**
     * Formats and prints specialized real-time in-game engine narrative events.
     * Adds the message to a temporary notification list, schedules its automatic
     * removal after a set time, and refreshes the dashboard interface.
     *
     * @param message Structured notification log detailing events.
     */
    @Override
    public void showEventMessage(String message) {
        if (message == null || message.isBlank()) return;

        // Add the new message to the end of the active list
        activeEventMessages.add(ANSI_PURPLE + "[EVENT]: " + message + RESET);
    }

    /**
     * Prompts users during secondary optional resolution phases where they can either
     * spend leftover resources buying structural buildings or pass via the 'END_TURN' directive.
     */
    @Override
    public void askEndTurnOrBuyBuilding() {
        clearInputBuffer();

        System.out.println("You can only buy buildings. If you want to buy them, enter the building code; otherwise, type END_TURN.");
        System.out.print("Enter your choice (e.g. A) or manual code (e.g. T0): ");
        String input = readLine().trim().toUpperCase();

        // Translates the chosen letter into the server format immediately
        String serverCode = translateInputToServerCode(input);

        try {
            // Forwards the immediate selection straight to the server Model
            if (input.equals("END_TURN")) {
                network.endTurnRequest();
            } else {
                network.cardSelection(serverCode);
            }
        } catch (Exception e) {
            showError(handleNetworkError(e));
            if (lastBoard != null) {
                displayBoard(lastBoard);
                askEndTurnOrBuyBuilding();
            }
        }
    }
}