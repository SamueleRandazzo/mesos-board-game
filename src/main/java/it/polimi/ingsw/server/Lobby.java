package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exception.CustomException;
import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.factories.GameDataLoader;
import it.polimi.ingsw.network.DTO.TribeStatusDTO;
import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.ModelToRemoteViewAdapter;
import org.jetbrains.annotations.NotNull;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import it.polimi.ingsw.database.*;

public class Lobby {

    private static final int MAX_PLAYERS = 5;
    private static final int MIN_PLAYERS = 2;

    private int targetPlayers = -1;
    private final List<GameObserver> remoteObservers = new ArrayList<>();
    private final List<String> nicknames = new ArrayList<>();
    private final List<Color> colors = new ArrayList<>();
    private final Map<String, GameObserver> playerObservers = new HashMap<>();
    private final Map<String, Color> playersInfo = new LinkedHashMap<>();
    private Game currentGame;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private boolean healthCheckStarted = false;
    private final ExecutorService pingExecutor = Executors.newCachedThreadPool();

    /**
     * Handles player login requests. Adds players to the lobby and manages
     * the start of the countdown or the game.
     * @param color the chosen color for the player's pieces.
     * @param nickname the unique name chosen by the player.
     * @param observer the remote observer for client-side updates.
     * @throws RemoteException if the game is full or the server is unreachable.
     */
    public synchronized void addPlayer(String nickname, Color color, GameObserver observer) throws Exception {
        if (targetPlayers != -1 && nicknames.size() >= targetPlayers) {
            throw new CustomException.LobbyFullException();
        }

        if (!nicknames.isEmpty() && targetPlayers == -1) {
            throw new CustomException.HostStillSettingLobbyException();
        }

        if (nicknames.contains(nickname)) {
            throw new CustomException.NicknameAlreadyUsedException();
        }

        if (colors.contains(color)){
            throw new CustomException.ColorAlreadyUsedException();
        }

        try {
            if (DatabaseManager.isAvailable()) {
                PlayerDAO.saveOrGetPlayer(nickname);
                System.out.println("[DB] Player " + nickname + " registered/verified.");
            }
        } catch (Exception e) {
            System.err.println("[DB] Could not register player: " + e.getMessage());
        }

        nicknames.add(nickname);
        colors.add(color);
        remoteObservers.add(observer);
        playerObservers.put(nickname, observer);
        playersInfo.put(nickname, color);

        if (!healthCheckStarted)
            startNetworkHealthCheck();

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
                        playerObservers.remove(nickname);
                        playersInfo.remove(nickname);
                    }
                }
            }).start();
        } else {
            System.out.println(nickname + " joined.");
            new Thread(() ->  {
                try {
                    checkStartCondition();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    public void setTargetPlayers(int num) throws Exception {
        if (num < MIN_PLAYERS || num > MAX_PLAYERS) {
            throw new CustomException.InvalidTargetPlayersNumberException(MIN_PLAYERS, MAX_PLAYERS);
        }

        this.targetPlayers = num;
        System.out.println("Game set for " + num + " players.");

        new Thread(() -> {
            try {
                checkStartCondition();
            } catch (Exception e) {
                System.err.println(e);
            }
        }).start();
    }

    private synchronized void checkStartCondition() throws Exception {
        if (targetPlayers != -1 && nicknames.size() == targetPlayers) {
            System.out.println("Game starting!");

            List<Player> players = new ArrayList<>();
            for (int i = 0; i < nicknames.size(); i++) {
                players.add(new Player(colors.get(i), nicknames.get(i)));
            }

            Collections.shuffle(players);

            currentGame = getGame(players, targetPlayers);
            GameController gameController = new GameController(currentGame);

            currentGame.startGame();

            for (GameObserver o : remoteObservers) {
                o.onGameStarted(gameController, targetPlayers);
                o.onShowPlayersInfo(playersInfo);
            }

            // Broadcast the initial tribe status of every player to all connected observers
            for (Player p : players) {
                TribeStatusDTO tribeDTO = p.getTribe().toDTO();
                for (GameObserver o : remoteObservers) {
                    try {
                        o.onShowTribe(p.getNickname(), tribeDTO);
                    } catch (RemoteException e) {
                        System.err.println("Error distributing initial tribe of " + p.getNickname());
                    }
                }
            }

            ModelToRemoteViewAdapter adapter = new ModelToRemoteViewAdapter(this.playerObservers);
            currentGame.addListener(adapter);

            currentGame.notifyDisplayTurnOrderTile();
            currentGame.notifyShowPlayerOrder();
            currentGame.notifyOnShowBoard();
            currentGame.notifyOnShowOfferTrack();
            currentGame.notifyTotemPlacementTurnChanged();
        } else {
            for (GameObserver o : remoteObservers) {
                o.onPlayerJoined(nicknames.size(), targetPlayers == -1 ? 0 : targetPlayers);
            }
        }
    }

    private static @NotNull Game getGame(List<Player> players, int targetPlayers) {
        GameDataLoader loader = new GameDataLoader();

        List<TribeDeck> decks = loader.loadDecks(targetPlayers);
        List<BuildingCard> era1Buildings = loader.loadBuildings(1, targetPlayers);
        List<BuildingCard> era2Buildings = loader.loadBuildings(2, targetPlayers);
        List<BuildingCard> era3Buildings = loader.loadBuildings(3, targetPlayers);
        OfferTrack offerTrack = loader.loadOfferTrack(players.size());

        return new Game(players, decks, era1Buildings, era2Buildings, era3Buildings, offerTrack);
    }

    public void handleDisconnection(String nickname) {
        if (nickname == null || !nicknames.contains(nickname)) {
            return;
        }

        synchronized (this) {
            if (!nicknames.contains(nickname)) return;

            int index = nicknames.indexOf(nickname);
            if (index != -1) {
                remoteObservers.remove(index);
                playerObservers.remove(nickname);
                nicknames.remove(nickname);
                playersInfo.remove(nickname);
            }
        }

        terminateGame(nickname);
    }

    private void terminateGame(String disconnectedNickname) {
        System.err.println("FATAL: Player " + disconnectedNickname + " disconnected. Terminating match.");

        if (currentGame != null)
            currentGame.setEndGameStatus();

        for (GameObserver observer : remoteObservers) {
            try {
                observer.onShowFatalError("Game ended: player " + disconnectedNickname + " disconnected.");
            } catch (RemoteException e) {
                // ignore
            }
        }

        this.currentGame = null;
        this.nicknames.clear();
        this.colors.clear();
        this.remoteObservers.clear();
        this.playerObservers.clear();
        this.playersInfo.clear();
        this.targetPlayers = -1;
    }

    public void startNetworkHealthCheck() {
        Runnable healthCheckTask = () -> {
            try {
                checkConnections();
            } catch (Exception e) {
                System.err.println("Health check error: " + e.getMessage());
            }
        };

        this.healthCheckStarted = true;
        scheduler.scheduleAtFixedRate(healthCheckTask, 5, 5, TimeUnit.SECONDS);
    }

    private void checkConnections() {
        List<Map.Entry<String, GameObserver>> observersCopy;
        synchronized (this) {
            if (playerObservers.isEmpty()) return;
            observersCopy = new ArrayList<>(playerObservers.entrySet());
        }

        for (Map.Entry<String, GameObserver> entry : observersCopy) {
            String nickname = entry.getKey();
            GameObserver observer = entry.getValue();

            pingExecutor.submit(() -> {
                try { observer.ping(); }
                catch (RemoteException e) { handleDisconnection(nickname); }
            });
        }
    }

    public TribeStatusDTO getTribeStatusByNickname(String nickname) {
        return currentGame.getPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst()
                .map(p -> p.getTribe().toDTO())
                .orElse(null);
    }
}
