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
import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.ModelToRemoteViewAdapter;
import org.jetbrains.annotations.NotNull;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class Lobby {

    private static final int MAX_PLAYERS = 5;
    private static final int MIN_PLAYERS = 2;

    private int targetPlayers = -1;
    private final List<GameObserver> remoteObservers = new ArrayList<>();
    private final List<String> nicknames = new ArrayList<>();
    private final List<Color> colors = new ArrayList<>();
    private final Map<String, GameObserver> playerObservers = new HashMap<>();

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

        nicknames.add(nickname);
        colors.add(color);
        remoteObservers.add(observer);
        playerObservers.put(nickname, observer);

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
                e.printStackTrace();
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

            Game game = getGame(players, targetPlayers);
            GameController gameController = new GameController(game);

            List<String> playerOrders = players.stream().map(Player::getNickname).collect(Collectors.toList());

            for (GameObserver o : remoteObservers) {
                o.onGameStarted(gameController);
                o.onShowPlayersOrder(playerOrders);
            }

            ModelToRemoteViewAdapter adapter = new ModelToRemoteViewAdapter(this.playerObservers);
            game.addListener(adapter);

            game.notifyOnShowOfferTrack();
            game.notifyTotemPlacementTurnChanged();
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
}
