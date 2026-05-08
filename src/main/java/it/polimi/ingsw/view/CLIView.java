package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.BoardDTO;
import it.polimi.ingsw.network.DTO.CardDTO;
import it.polimi.ingsw.network.DTO.OfferTileDTO;
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
    private List<OfferTileDTO> lastTiles;
    private BoardDTO lastBoard; // Added to store the last received board state

    /**
     * Constructs a new CLIView.
     * @param network the network manager used to communicate with the server.
     */
    public CLIView(NetworkManager network) {
        this.network = network;
        this.reader = new BufferedReader(new InputStreamReader(System.in));

        // Warm up the dictionary at startup to load JSON files
        LocalCardDictionary.getInstance();
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
                System.out.println("Invalid color. Please try again.");
            }
        }

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
     * @param current the current number of players in the lobby.
     * @param total   the total number of players required to start the game.
     */
    @Override
    public void showLobby(int current, int total) {
        System.out.println("Lobby Update: " + current + "/" + total + " players.");
    }

    /**
     * Notifies the user that the game has started.
     * @param controller the remote controller provided by the server to handle game actions.
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
     * Displays the current offer track and asks the player to choose a tile for totem placement.
     * This is an interactive method that forwards the user's choice to the server.
     */
    @Override
    public void askTotemPlacement() {
        clearInputBuffer();

        System.out.print("Choose the offer tile: ");
        int tileIndex = Integer.parseInt(readLine());
        try {
            network.tileSelection(tileIndex);
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
        this.lastTiles = tiles;

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
        System.out.println("=========================================================\n");
    }

    @Override
    public void retryTotemPlacement() {
        if (lastTiles != null) {
            displayOfferTrack(lastTiles);
            askTotemPlacement();
        }
    }

    /**
     * Called by the server when the Board is updated.
     * Translates CardDTO IDs into readable text using the LocalCardDictionary.
     */
    public void displayBoard(BoardDTO board) {
        this.lastBoard = board;
        System.out.println("\n======================== BOARD ========================");

        printRow("Upper Tribe Row (T)", board.getUpperTribeRow(), "T");
        printRow("Lower Tribe Row (L)", board.getLowerTribeRow(), "L");
        System.out.println("---------------------------------------------------------");
        printRow("Upper Building Row (B)", board.getUpperBuildingRow(), "B");
        printRow("Lower Building Row (G)", board.getLowerBuildingRow(), "G");

        System.out.println("=========================================================\n");
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
            String id = row.get(i).getId();
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

        System.out.println("\n====================== CHOOSE YOUR CARD ======================");
        if (lastBoard != null) {
            // Actively print all 4 rows using the dictionary parser
            printRow("Upper Tribe Row (T)", lastBoard.getUpperTribeRow(), "T");
            printRow("Lower Tribe Row (L)", lastBoard.getLowerTribeRow(), "L");
            System.out.println("--------------------------------------------------------------");
            printRow("Upper Building Row (B)", lastBoard.getUpperBuildingRow(), "B");
            printRow("Lower Building Row (G)", lastBoard.getLowerBuildingRow(), "G");
        } else {
            System.out.println("  [!] Board state not received yet. Cannot display cards.");
        }
        System.out.println("==============================================================\n");

        System.out.println("Format: [Row Prefix][Index] (e.g., T0 for first Upper Tribe, G2 for third Lower Building)");
        System.out.print("Enter your choice: ");
        String cardPosition = readLine().trim().toUpperCase();

        try {
            // Forwards the input (e.g., "T0") to the NetworkManager regex parser
            network.cardSelection(cardPosition);
        } catch (Exception e) {
            showError(handleNetworkError(e));
            askCardChoose(); // Retry on failure
        }
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

        String finalMessage = "Players order is:\n" + orderMessage;

        System.out.println(finalMessage);
    }

    @Override
    public void showPlayersInfo(Map<String,Color> playersInfo) {
        //CLI does not need to know player color
    }
}