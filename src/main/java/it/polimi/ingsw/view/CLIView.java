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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.polimi.ingsw.exception.CustomException.cleanRemoteException;

/**
 * Command Line Interface (CLI) implementation of the game view.
 * <p>
 * This class handles all user interactions through the console, following the MVC pattern.
 * It displays the game state to the user and forwards user inputs to the server
 * via the {@link NetworkManager}. All visuals are constrained to standard ASCII.
 */
public class CLIView implements View {
    private final BufferedReader reader;
    private final NetworkManager network;
    private BoardDTO lastBoard;
    private final Map<String, TribeStatusDTO> allTribes = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * Thread-safe blocking queue to synchronize the background reader thread
     * with standard blocking requests like askCardChoose or askTotemPlacement.
     */
    private final BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();

    /**
     * Flag used to track whether the interface is actively prompting the user for a game decision.
     * When false, inputs are filtered as background actions (like commands).
     */
    private volatile boolean isGamePromptActive = false;

    /** * Local map used to translate dynamic user-friendly letters ('A', 'B', 'C')
     * into server-expected regex coordinates ('T0', 'L1', 'B0', 'G1').
     */
    private volatile Map<String, String> letterToCodeMap = new java.util.concurrent.ConcurrentHashMap<>();

    /** Stores the last error message to ensure it survives console clearing events. */
    private volatile String lastErrorMessage = null;

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";

    private static final String CLEAR_SCREEN = "\033[H\033[2J";
    private static final int LEFT_COLUMN_WIDTH = 75; // Increased width to fit full card descriptions smoothly
    private volatile List<OfferTileDTO> lastOfferTrack = null;

    private String myNickname = "MY_NICKNAME_HERE";

    /**
     * Instantiates the CLI View and immediately initializes a daemon thread
     * to constantly capture terminal keyboard inputs asynchronously.
     */
    public CLIView(NetworkManager network) {
        this.network = network;
        this.reader = new BufferedReader(new InputStreamReader(System.in));

        // Start the background listening thread immediately
        Thread inputListener = new Thread(this::asynchronousInputLoop);
        inputListener.setDaemon(true);
        inputListener.start();
    }

    /**
     * Infinite loop executed in background. Safely intercept every single string
     * written by the user and routes it through the central parser.
     */
    private void asynchronousInputLoop() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                processGlobalInput(line);
            }
        } catch (IOException e) {
            // Background stream reader terminated
        }
    }

    /**
     * Centralized message hub. Discriminates whether the typed text is a universal command
     * or a standard game action intended to answer a running prompt.
     */
    private void processGlobalInput(String input) {
        if (input.startsWith("/")) {
            handleSystemCommand(input);
            return;
        }

        if (isGamePromptActive) {
            inputQueue.offer(input);
        } else {
            System.out.println(YELLOW + "System idle or waiting for your turn. Type /help to see commands." + RESET);
        }
    }

    /**
     * Parses and processes custom terminal text utility commands starting with a slash '/'.
     */
    private void handleSystemCommand(String fullCommand) {
        String[] parts = fullCommand.split(" ", 2);
        String command = parts[0].toLowerCase();

        switch (command) {
            case "/help":
                System.out.println(CYAN + "\n--- AVAILABLE SYSTEM COMMANDS ---" + RESET);
                System.out.println(" /help              - Displays this list of utilities.");
                System.out.println(" /view <nickname>   - Show <nickname> tribe\n");
                break;

            case "/view":
                if (parts.length < 2 || parts[1].trim().isEmpty()) {
                    System.out.println(RED + "Usage: /view <nickname>" + RESET);
                    break;
                }
                String targetPlayer = parts[1].trim();
                System.out.println(YELLOW + "Requesting tribe data for '" + targetPlayer + "'..." + RESET);
                try {
                    network.seeOtherPlayerTribe(targetPlayer);
                } catch (Exception ex) {
                    showError(handleNetworkError(ex));
                }
                break;

            default:
                System.out.println(RED + "Unknown command. Use /help for assistance." + RESET);
                break;
        }
    }

    /**
     * Overrides the synchronous reading logic. Instead of blocking the whole application thread
     * waiting for an I/O hook, it safely awaits a token pushed into the queue by the async listener.
     */
    private String readLine() {
        try {
            return inputQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "";
        }
    }

    private void clearInputBuffer() {
        inputQueue.clear();
    }

    /**
     * Clears the terminal screen reliably across different operating systems.
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
     * Adds spaces to the right of a string to reach the target width, ignoring ANSI color codes.
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
     * Colorizes the card string based on its content type.
     */
    private String colorizeCard(String details, boolean isBuildingRow) {
        if (isBuildingRow) {
            return BLUE + details + RESET; // Buildings in Blue
        }
        if (details.startsWith("Hunt") || details.startsWith("Sustenance") ||
                details.startsWith("Shamanic Ritual") || details.startsWith("Cave Paintings") ||
                details.startsWith("Event")) {
            return PURPLE + details + RESET; // Events in Purple
        }
        return CYAN + details + RESET; // Characters in Cyan
    }

    /**
     * Generates sequential letters (A, B... Z, AA, AB...) for indexing cards.
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
     * Builds the left column of the CLI containing the User's Tribe, the Board, and the Offers.
     * Dynamically assigns selection letters to draftable cards in a thread-safe manner.
     */
    private List<String> buildLeftColumn() {
        List<String> lines = new java.util.ArrayList<>();
        Map<String, String> tempLetterMap = new java.util.concurrent.ConcurrentHashMap<>();
        int letterIndex = 0;

        // 1. MY TRIBE
        lines.add(YELLOW + BOLD + "=".repeat(20) + " MY TRIBE " + "=".repeat(20) + RESET);
        if (myNickname != null && allTribes.containsKey(myNickname)) {
            lines.addAll(formatTribeBlock(myNickname, allTribes.get(myNickname), true));
        } else {
            lines.add(" Waiting for your tribe data...");
            lines.add("");
        }

        // 2. CENTRAL BOARD
        lines.add(YELLOW + BOLD + "=".repeat(22) + " BOARD " + "=".repeat(22) + RESET);
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
                    lines.add("      - " + colorizeCard(details, false));
                } else {
                    String letterStr = getLetterLabel(letterIndex++);
                    lines.add("  [" + letterStr + "] " + colorizeCard(details, false));
                    tempLetterMap.put(letterStr, "T" + i);
                }
            }

            // --- LOWER TRIBE ROW (Server code: L) ---
            lines.add("\nLower Tribe Row:");
            for (int i = 0; i < lastBoard.getLowerTribeRow().size(); i++) {
                CardDTO c = lastBoard.getLowerTribeRow().get(i);
                String details = LocalCardDictionary.getInstance().getCardDetails(c.getCardId());

                boolean isEvent = details.startsWith("Hunt") || details.startsWith("Sustenance") ||
                        details.startsWith("Shamanic Ritual") || details.startsWith("Cave Paintings") ||
                        details.startsWith("Event");
                if (isEvent) {
                    lines.add("      - " + colorizeCard(details, false));
                } else {
                    String letterStr = getLetterLabel(letterIndex++);
                    lines.add("  [" + letterStr + "] " + colorizeCard(details, false));
                    tempLetterMap.put(letterStr, "L" + i);
                }
            }

            // --- UPPER BUILDINGS (Server code: B) ---
            lines.add(BLUE + "\nUpper Buildings:" + RESET);
            for (int i = 0; i < lastBoard.getUpperBuildingRow().size(); i++) {
                CardDTO c = lastBoard.getUpperBuildingRow().get(i);
                String details = LocalCardDictionary.getInstance().getCardDetails(c.getCardId());
                String letterStr = getLetterLabel(letterIndex++);

                lines.add("  [" + letterStr + "] " + colorizeCard(details, true));
                tempLetterMap.put(letterStr, "B" + i);
            }

            // --- LOWER BUILDINGS (Server code: G) ---
            lines.add(BLUE + "\nLower Buildings:" + RESET);
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
        // 3. OFFERS
        lines.add(YELLOW + BOLD + "=".repeat(21) + " OFFERS " + "=".repeat(21) + RESET);
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

    /*
    /**
     * Builds the right column of the CLI containing Opponents' Tribes.
     *
    /*
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
    }*/

    /**
     * Formats a single tribe status into a list of displayable strings.
     * Extracts and prints every single card detail explicitly.
     */
    private List<String> formatTribeBlock(String playerName, TribeStatusDTO t, boolean isMe) {
        List<String> lines = new java.util.ArrayList<>();
        String headerColor = isMe ? GREEN + BOLD : RED + BOLD;
        String title = isMe ? "> PLAYER: " : "> OPPONENT: ";

        lines.add("");
        lines.add(headerColor + title + playerName.toUpperCase() + RESET);
        lines.add(String.format("   " + GREEN + "[Food] %d" + RESET + " | " + YELLOW + "[PP] %d" + RESET + " | " + YELLOW + "[Stars] %d" + RESET + " | " + CYAN + "[Sust] -%d" + RESET + " | " + BLUE + "[Build] -%d" + RESET,
                t.getCurrentFood(), t.getTotalPrestigePoints(), t.getShamanStars(), t.getTotalSustenanceDiscount(), t.getTotalBuildingsFoodDiscount()));

        // --- CHARACTERS ---
        lines.add(CYAN + "   CHARACTERS:" + RESET);
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
            lines.add(CYAN + "     None" + RESET);
        }

        // --- BUILDINGS ---
        lines.add(BLUE + "   BUILDINGS:" + RESET);
        if (t.getBuildingIds() != null && !t.getBuildingIds().isEmpty()) {
            for (CardDTO c : t.getBuildingIds()) {
                String details = LocalCardDictionary.getInstance().getCardDetails(c.getCardId());
                lines.add("     - " + colorizeCard(details, true));
            }
        } else {
            lines.add(BLUE + "     None built." + RESET);
        }

        lines.add("   " + "-".repeat(60));

        return lines;
    }

    /**
     * Master rendering function. Clears the console and visually zips the left and right columns.
     */
    private synchronized void renderDashboard() {
        clearConsole();
        List<String> left = buildLeftColumn();
        //List<String> right = buildRightColumn();

        //int maxLines = Math.max(left.size(), right.size());
        int maxLines = left.size();

        for (int i = 0; i < maxLines; i++) {
            String lLine = (i < left.size()) ? left.get(i) : "";
            String paddedLeft = padRightAnsi(lLine, LEFT_COLUMN_WIDTH);
            // rLine = (i < right.size()) ? right.get(i) : "";

            // System.out.println(paddedLeft + YELLOW + " | " + RESET + rLine);
            System.out.println(paddedLeft + YELLOW + " | " + RESET);
        }
        System.out.println("\n");

        // Appends the error message to the bottom of the board so it doesn't get cleared
        if (lastErrorMessage != null) {
            System.out.println(RED + BOLD + "[ERROR]: " + lastErrorMessage + RESET);
            lastErrorMessage = null;
        }
    }

    /**
     * Safely translates the user's flexible input into a clean, single-space separated
     * string of coordinates for the server backend (e.g., converts "A" into "T0").
     */
    private String translateInputToServerCode(String input) {
        if (input.matches(".*\\d.*")) {
            return input.replaceAll("[,;]", " ").replaceAll("\\s+", " ").trim();
        }

        StringBuilder translated = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (Character.isLetter(c)) {
                String letter = String.valueOf(c);
                translated.append(letterToCodeMap.getOrDefault(letter, letter)).append(" ");
            }
        }

        return translated.toString().trim();
    }

    /**
     * Prompts the user to choose a card. Forwards the choice immediately to the server.
     */
    @Override
    public void askCardChoose() {
        clearInputBuffer();
        isGamePromptActive = true;

        System.out.print("Enter your choice (e.g. A) or manual code (e.g. T0): ");
        String input = readLine().trim().toUpperCase();

        isGamePromptActive = false;

        String serverCode = translateInputToServerCode(input);

        try {
            network.cardSelection(serverCode);
        } catch (Exception e) {
            showError(handleNetworkError(e));
            if (lastBoard != null) {
                displayBoard(lastBoard);
                askCardChoose();
            }
        }
    }

    @Override
    public void showLogin() {
        isGamePromptActive = true;

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

        isGamePromptActive = false;

        try {
            System.out.println("Login request sent. Waiting for server confirmation...");
            network.login(selectedColor, nickname);
        } catch (Exception e) {
            showError(handleNetworkError(e));
            showLogin();
        }
    }

    @Override
    public void showLobby(int current, int total) {
        System.out.println("Lobby Update: " + current + "/" + total + " players.");
    }

    @Override
    public void startGame(RemoteController controller, int totalPlayers) {
        network.setController(controller);
        System.out.println("THE GAME HAS STARTED!");
    }

    /**
     * Stores and displays an error message reliably to the user.
     */
    @Override
    public void showError(String error) {
        this.lastErrorMessage = error;
        System.out.println(RED + BOLD + "[ERROR]: " + error + RESET);
        System.out.flush();
    }

    @Override
    public void showFatalError(String error) {
        System.out.print("\r\033[K");
        System.out.println(RED + BOLD + "[FATAL ERROR]: " + error + RESET);
        System.out.flush();
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
        System.exit(1);
    }

    @Override
    public void askMaxPlayers() {
        isGamePromptActive = true;
        System.out.print("You are the host! How many players do you want (2-5)? ");
        try {
            String input = readLine();
            int n = Integer.parseInt(input);
            isGamePromptActive = false;
            network.setTotalPlayers(n);
        } catch (NumberFormatException e) {
            isGamePromptActive = false;
            showError("Insert a valid number!");
            askMaxPlayers();
        } catch (Exception e) {
            isGamePromptActive = false;
            showError(handleNetworkError(e));
            askMaxPlayers();
        }
    }

    @Override
    public void askTotemPlacement() {
        clearInputBuffer();
        isGamePromptActive = true;

        System.out.print("Choose the offer tile: ");
        try {
            int tileIndex = Integer.parseInt(readLine());
            isGamePromptActive = false;
            network.tileSelection(tileIndex);
        } catch (NumberFormatException e) {
            isGamePromptActive = false;
            showError("Insert a valid number!");
            askTotemPlacement();
        } catch (Exception e) {
            isGamePromptActive = false;
            showError(handleNetworkError(e));
            askTotemPlacement();
        }
    }

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

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void showPlayersOrder(List<String> playersOrder) {
        String orderMessage = IntStream.range(0, playersOrder.size())
                .mapToObj(i -> (i + 1) + ". " + playersOrder.get(i))
                .collect(Collectors.joining("\n"));
        System.out.println("Players order is:\n" + orderMessage);
    }

    @Override
    public void showPlayersInfo(Map<String,Color> playersInfo) {}

    @Override
    public void showTribe(String nickname, TribeStatusDTO tribe) {
        if (nickname != null && tribe != null) {
            allTribes.put(nickname, tribe);
            renderDashboard();
        }
    }

    public void displayBoard(BoardDTO board) {
        this.lastBoard = board;
        renderDashboard();
    }

    @Override
    public void displayLeaderboard(LeaderboardDTO leaderboard, String globalRank) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(centerText("FINAL LEADERBOARD", 60));
        System.out.println("=".repeat(60));
        System.out.println(String.format("%-5s | %-20s | %-12s | %-8s", "POS", "PLAYER", "PRESTIGE", "FOOD"));
        System.out.println("-".repeat(60));

        for (PlayerRankDTO entry : leaderboard.getRankings()) {
            String posString = entry.getPosition() + ".";
            System.out.println(String.format("%-5s | %-20s | %-12d | %-8d", posString, entry.getNickname(), entry.getPrestigePoints(), entry.getFoodAmount()));
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
            System.out.printf("%-4d | %-15s | %-10d%n", pos, tile.getNickname() + " (" + (tile.getColor() != null ? tile.getColor().toString() : "N/D") + ")", tile.getFoodBonus());
            pos++;
        }
        System.out.println("=".repeat(56));
    }

    private String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int totalPadding = width - text.length();
        int leftPadding = totalPadding / 2;
        return " ".repeat(leftPadding) + text + " ".repeat(totalPadding - leftPadding);
    }

    private void askGlobalLeaderboard(int targetPlayers) {
        isGamePromptActive = true;
        try {
            boolean ok = false;
            while (!ok) {
                String choice = readLine().trim().toLowerCase();
                if (choice.equalsIgnoreCase("GLOBAL_LEADERBOARD")) {
                    isGamePromptActive = false;
                    network.seeGlobalLeaderboard(targetPlayers);
                    ok = true;
                }
            }
        } catch (Exception e) {
            isGamePromptActive = false;
            askGlobalLeaderboard(targetPlayers);
        }
    }

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

    @Override
    public void showEventMessage(String message) {
        showMessage(message);
    }
}