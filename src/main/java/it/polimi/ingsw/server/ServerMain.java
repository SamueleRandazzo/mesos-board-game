package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.Loggable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Main entry point for the server application.
 * Manages the RMI registry, player logins, and game initialization logic.
 * This class handles the lobby phase, including a countdown that starts
 * when the minimum number of players is reached.
 */
public class ServerMain extends UnicastRemoteObject implements Loggable {
    private static int PORT = 1234;

    private List<Player> waitingPlayers = new ArrayList<>();
    private final int MAX_PLAYERS = 5;
    private final int MIN_PLAYERS = 2;

    private Map<String, GameObserver> remoteObservers = new HashMap<>();
    private GameController gameController;

    private Timer startTimer;
    private int COUNTDOWN = 45;
    private int currentCountdown;

    protected ServerMain() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        try {
            ServerMain server = new ServerMain();
            Registry registry = LocateRegistry.createRegistry(PORT);
            registry.rebind("Loggable", server);
            System.out.println("Server ready on port " + PORT);
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles player login requests. Adds players to the lobby and manages
     * the start of the countdown or the game.
     * @param color the chosen color for the player's pieces.
     * @param nickname the unique name chosen by the player.
     * @param observer the remote observer for client-side updates.
     * @throws RemoteException if the game is full or the server is unreachable.
     */
    @Override
    public synchronized void login(Color color, String nickname, GameObserver observer) throws RemoteException {
        if (waitingPlayers.size() >= MAX_PLAYERS) {
            throw new RemoteException("Game is full!");
        }

        remoteObservers.put(nickname, observer);
        waitingPlayers.add(new Player(color, nickname));

        System.out.println(nickname + " connected.");

        // Notify everyone that a new player joined
        broadcastPlayerCount();

        // Start countdown if minimum threshold reached
        if (waitingPlayers.size() == MIN_PLAYERS) {
            startCountdown();
        }

        // Start game immediately if maximum capacity reached
        if (waitingPlayers.size() == MAX_PLAYERS) {
            startGame();
        }
    }

    /**
     * Initializes the game model and controller, then notifies all
     * connected clients that the match has started.
     */
    private void startGame() {
        if (startTimer != null) {
            startTimer.cancel();
        }

        // TODO: Properly load decks, cards, and initialize the Game instance here.
        /*
        // loading data
        List<TribeDeck> decks = loadDecks();
        List<BuildingCard> e1 = loadBuildings(1);
        List<BuildingCard> e2 = loadBuildings(2);
        List<BuildingCard> e3 = loadBuildings(3);
        OfferTrack track = new OfferTrack();

        this.game = new Game(waitingPlayers, decks, e1, e2, e3, track);
        this.gameController = new GameController(this.game);
        */

        System.out.println("Initializing Game and Controller...");

        // Notifying all observers about the game start
        for (GameObserver obs : remoteObservers.values()) {
            try {
                obs.onGameStarted(this.gameController);
            } catch (RemoteException e) {
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }

    /**
     * Starts a countdown timer. When it reaches zero, the game starts with
     * the currently connected players (if they are at least MIN_PLAYERS).
     */
    private void startCountdown() {
        currentCountdown = COUNTDOWN;
        startTimer = new Timer();
        startTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                synchronized (ServerMain.this) {
                    if (gameController != null) { // Game already started by full lobby
                        this.cancel();
                        return;
                    }

                    broadcastTimer(currentCountdown);

                    if (currentCountdown <= 0) {
                        this.cancel();
                        startGame();
                    }
                    currentCountdown--;
                }
            }
        }, 0, 1000);
    }

    /**
     * Broadcasts the current number of players in the lobby to all connected clients.
     */
    private void broadcastPlayerCount() {
        remoteObservers.values().forEach(obs -> {
            try {
                obs.onPlayerJoined(waitingPlayers.size(), MAX_PLAYERS);
            } catch (RemoteException e) {
                // Handle or log disconnected client if necessary
            }
        });
    }

    /**
     * Broadcasts the current countdown value to all connected clients.
     * @param seconds the remaining seconds until the game starts.
     */
    private void broadcastTimer(int seconds) {
        remoteObservers.values().forEach(obs -> {
            try {
                obs.onTimerUpdate(seconds);
            } catch (RemoteException e) {
                // Handle or log disconnected client if necessary
            }
        });
    }
}