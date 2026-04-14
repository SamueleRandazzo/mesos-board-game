package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.network.RemoteController;

import java.util.Scanner;

public class CLIView implements View {
    private Scanner scanner = new Scanner(System.in);
    private NetworkManager network;
    private GameObserver myObserver;

    public void setObserver(GameObserver observer) {
        this.myObserver = observer;
    }

    @Override
    public void showLogin() {
        System.out.println("=== WELCOME TO MESOS ===");

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
        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
            showLogin();
        }
    }

    @Override
    public void showLobby(int current, int total) {
        System.out.println("Lobby Update: " + current + "/" + total + " players.");
    }

    @Override
    public void showTimer(int seconds) {
        System.out.println("Game starting in: " + seconds + "s");
    }

    @Override
    public void startGame(RemoteController controller) {
        System.out.println("THE GAME HAS STARTED!");
    }

    @Override
    public void showError(String error) {
        System.out.println("error");
    }
}