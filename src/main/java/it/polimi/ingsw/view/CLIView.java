package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.OfferTileDTO;
import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.network.RemoteController;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

import static it.polimi.ingsw.exception.CustomException.cleanRemoteException;

/**
 * Command Line Interface (CLI) implementation of the game view.
 * <p>
 * This class handles all user interactions through the console, following the MVC pattern.
 * It displays the game state to the user and forwards user inputs to the server
 * via the {@link NetworkManager}.
 */
public class CLIView implements View {
    private final Scanner scanner;
    private final NetworkManager network;
    private GameObserver myObserver;

    /**
     * Constructs a new CLIView.
     * * @param network the network manager used to communicate with the server.
     */
    public CLIView(NetworkManager network) {
        this.network = network;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Sets the observer associated with this view.
     * The observer is used by the server to send asynchronous notifications to the client.
     * * @param observer the game observer instance.
     */
    public void setObserver(GameObserver observer) {
        this.myObserver = observer;
    }

    /**
     * Displays the login screen and handles the nickname and color selection process.
     * This method blocks until a valid login request has been sent to the server.
     */
    @Override
    public void showLogin() {
        System.out.println("=== WELCOME TO MESOS ===");

        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.print("Enter your nickname: ");
            String nickname = scanner.nextLine();

            System.out.println("Available colors: RED, BLUE, BLACK, YELLOW, WHITE");
            Color selectedColor = null;
            while (selectedColor == null) {
                System.out.print("Choose your color: ");
                try {
                    selectedColor = Color.valueOf(scanner.nextLine().toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid color. Please try again.");
                }
            }

            try {
                network.login(selectedColor, nickname, myObserver);
                System.out.println("Login request sent. Waiting for server confirmation...");
                loggedIn = true;
            } catch (RemoteException e) {
                System.out.println(cleanRemoteException(e));
            }
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
    public void startGame(RemoteController controller) {
        network.setController(controller);
        System.out.println("THE GAME HAS STARTED!");
    }

    /**
     * Displays an error message to the user.
     * * @param error the error message to display.
     */
    @Override
    public void showError(String error) {
        System.out.println(error);
    }

    /**
     * Prompts the host player to set the maximum number of players for the game session.
     * Only the first player to join (the host) will trigger this method.
     */
    @Override
    public void askMaxPlayers() {
        boolean success = false;
        while (!success) {
            System.out.print("You are the host! How many players do you want (2-5)? ");
            try {
                int n = Integer.parseInt(scanner.nextLine());
                network.setTotalPlayers(n);
                success = true;
            } catch (NumberFormatException e) {
                System.out.println("Insert a valid number!");
            } catch (RemoteException e) {
                System.out.println(cleanRemoteException(e));
            }
        }
    }

    /**
     * Displays the current offer track and asks the player to choose a tile for totem placement.
     * This is an interactive method that forwards the user's choice to the server.
     * * @param tiles a list of {@link OfferTileDTO} representing the current state of the offer track.
     */
    @Override
    public void askTotemPlacement(List<OfferTileDTO> tiles) {
        displayOfferTrack(tiles);

        System.out.print("Choose the offer tile: ");
        int tileIndex = Integer.parseInt(scanner.nextLine());
        try {
            network.tileSelection(tileIndex);
        } catch (RemoteException e) {
            System.out.println(cleanRemoteException(e));
        }
    }

    /**
     * Formats and prints the offer track table to the console.
     * It displays indices, bonuses, and the occupancy status of each tile.
     * * @param tiles the list of data transfer objects containing tile information.
     */
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
        System.out.println("=========================================================\n");
    }
}