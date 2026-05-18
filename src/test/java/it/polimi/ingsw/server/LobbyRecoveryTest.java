package it.polimi.ingsw.server;

import it.polimi.ingsw.model.Board.Board;
import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.Board.TurnOrderSlot;
import it.polimi.ingsw.model.Board.TurnOrderTile;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.factories.GameDataLoader;
import it.polimi.ingsw.model.states.TotemPlacementState;
import it.polimi.ingsw.network.DTO.*;
import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.persistence.PersistenceManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LobbyRecoveryTest {

    @TempDir
    Path tempDir;

    @Test
    void unknownNicknameCannotReconnectDuringRecovery() {
        PersistenceManager persistenceManager = new PersistenceManager(tempDir.resolve("current-game.json"));
        persistenceManager.saveGame(createSavedGame());

        Lobby lobby = new Lobby(persistenceManager);

        assertThrows(
                Exception.class,
                () -> lobby.addPlayer("Intruder", Color.YELLOW, new DummyObserver())
        );
    }

    @Test
    void savedNicknameCannotReconnectWithWrongColorDuringRecovery() {
        PersistenceManager persistenceManager = new PersistenceManager(tempDir.resolve("current-game.json"));
        persistenceManager.saveGame(createSavedGame());

        Lobby lobby = new Lobby(persistenceManager);

        assertThrows(
                Exception.class,
                () -> lobby.addPlayer("Bob", Color.RED, new DummyObserver())
        );
    }

    @Test
    void allSavedPlayersReconnectShouldResumeSavedGame() throws Exception {
        PersistenceManager persistenceManager = new PersistenceManager(tempDir.resolve("current-game.json"));
        persistenceManager.saveGame(createSavedGame());
        Lobby lobby = new Lobby(persistenceManager);
        RecordingObserver aliceObserver = new RecordingObserver();
        RecordingObserver bobObserver = new RecordingObserver();

        lobby.addPlayer("Alice", Color.RED, aliceObserver);
        lobby.addPlayer("Bob", Color.BLUE, bobObserver);

        assertTrue(aliceObserver.gameStarted);
        assertTrue(bobObserver.gameStarted);
        assertEquals(2, aliceObserver.totalPlayers);
        assertEquals(2, bobObserver.totalPlayers);
        assertNotNull(aliceObserver.controller);
        assertNotNull(bobObserver.controller);
        assertEquals(Color.RED, aliceObserver.playersInfo.get("Alice"));
        assertEquals(Color.BLUE, aliceObserver.playersInfo.get("Bob"));
        assertTrue(aliceObserver.askedTotemPlacement);
    }

    private Game createSavedGame() {
        Player alice = new Player(Color.RED, "Alice");
        Player bob = new Player(Color.BLUE, "Bob");
        List<Player> players = List.of(alice, bob);

        GameDataLoader loader = new GameDataLoader();
        Map<String, TribeDeck> tribeCards = loader.loadAllTribeCardsById(2);
        Map<String, BuildingCard> buildingCards = loader.loadAllBuildingCardsById(2);

        Board board = new Board(
                List.of(tribeCards.get("tribeCard_1")),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        return new Game(
                players,
                board,
                new OfferTrack(new ArrayList<>()),
                new TurnOrderTile(List.of(new TurnOrderSlot(0), new TurnOrderSlot(0))),
                List.of(new ArrayList<>(), List.of(buildingCards.get("building_7"))),
                1,
                1,
                0,
                players,
                new TotemPlacementState()
        );
    }

    private static class DummyObserver implements GameObserver {
        @Override public void onPlayerJoined(int currentPlayers, int totalNeeded) {}
        @Override public void onGameStarted(RemoteController controller, int totalPlayers) {}
        @Override public void askMaxPlayers() {}
        @Override public void askTotemPlacement() {}
        @Override public void askCardChoose() {}
        @Override public void onShowMessage(String message) {}
        @Override public void onShowPlayersOrder(List<String> playersOrder) {}
        @Override public void onDisplayOfferTrack(List<OfferTileDTO> tiles) {}
        @Override public void onShowPlayersInfo(Map<String, Color> playersInfo) {}
        @Override public void onDisplayBoard(BoardDTO board) {}
        @Override public void onDisplayLeaderboard(LeaderboardDTO leaderboard, String globalRank) {}
        @Override public void ping() throws RemoteException {}
        @Override public void onShowFatalError(String error) {}
        @Override public void onDisplayGlobalLeaderboard(GlobalLeaderboardDTO globalLeaderboard) {}
        @Override public void onDisplayTurnOrderTile(List<TurnOrderTileDTO> turnOrderTile) {}
        @Override public void onShowEventMessage(String message) {}
        @Override public void askEndTurnOrBuyBuilding() throws RemoteException {}
        @Override public void onShowTribe(String nickname, TribeStatusDTO tribe) {}
    }

    private static class RecordingObserver extends DummyObserver {
        private boolean gameStarted;
        private boolean askedTotemPlacement;
        private int totalPlayers;
        private RemoteController controller;
        private Map<String, Color> playersInfo;

        @Override
        public void onGameStarted(RemoteController controller, int totalPlayers) {
            this.gameStarted = true;
            this.controller = controller;
            this.totalPlayers = totalPlayers;
        }

        @Override
        public void onShowPlayersInfo(Map<String, Color> playersInfo) {
            this.playersInfo = playersInfo;
        }

        @Override
        public void askTotemPlacement() {
            this.askedTotemPlacement = true;
        }
    }
}
