package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exception.CustomException;
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

    private final int MAX_PLAYERS = 5;
    private int MIN_PLAYERS = 2;

    private int targetPlayers = -1;
    private final List<GameObserver> remoteObservers = new ArrayList<>();
    private final List<String> nicknames = new ArrayList<>();
    private final List<Color> colors = new ArrayList<>();
    private GameController gameController;

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
        if (targetPlayers != -1 && nicknames.size() >= targetPlayers) {
            throw new CustomException.LobbyFullException();
        }

        if (nicknames.contains(nickname)) {
            throw new CustomException.NicknameAlreadyUsedException();
        }

        if (colors.contains(color)){
            throw new CustomException.ColorAlreadyUsedException();
        }

        nicknames.add(nickname);
        colors.add(color);
        remoteObservers.add(observer);

        if (nicknames.size() == 1) {
            System.out.println(nickname + " is the Host. Waiting for target players number");
            new Thread(() -> {
                try {
                    observer.askMaxPlayers();
                } catch (RemoteException e) {
                    System.err.println("Host disconnected while setting players number.");
                    synchronized(this) {
                        nicknames.remove(nickname);
                        colors.remove(color);
                        remoteObservers.remove(observer);
                    }
                }
            }).start();
        } else {
            System.out.println(nickname + " joined.");
            new Thread(() ->  {
                try {
                    checkStartCondition();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    public void setTotalPlayers(int num) throws RemoteException {
        if (num < MIN_PLAYERS || num > MAX_PLAYERS) {
            throw new CustomException.InvalidTargetPlayersNumberException(MIN_PLAYERS, MAX_PLAYERS);
        }

        this.targetPlayers = num;
        System.out.println("Game set for " + num + " players.");

        new Thread(() -> {
            try {
                checkStartCondition();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private synchronized void checkStartCondition() throws RemoteException {
        if (targetPlayers != -1 && nicknames.size() == targetPlayers) {
            System.out.println("Game starting!");

            List<Player> players = new ArrayList<>();
            for (int i = 0; i < nicknames.size(); i++) {
                players.add(new Player(colors.get(i), nicknames.get(i)));
            }

            // TODO: Properly load decks, cards, and initialize the Game instance here.
            /*
            // loading data
            List<TribeDeck> decks = loadDecks();
            List<BuildingCard> e1 = loadBuildings(1);
            List<BuildingCard> e2 = loadBuildings(2);
            List<BuildingCard> e3 = loadBuildings(3);
            OfferTrack track = new OfferTrack();

            this.game = new Game(players, decks, e1, e2, e3, track);
            this.gameController = new GameController(this.game);
            */

            for (GameObserver o : remoteObservers) {
                o.onGameStarted(this.gameController);
            }
        } else {
            for (GameObserver o : remoteObservers) {
                o.onPlayerJoined(nicknames.size(), targetPlayers == -1 ? 0 : targetPlayers);
            }
        }
    }
}