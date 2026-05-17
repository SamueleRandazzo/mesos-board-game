package it.polimi.ingsw.persistence;

import it.polimi.ingsw.model.Board.Board;
import it.polimi.ingsw.model.Board.OfferTile;
import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.Board.TurnOrderSlot;
import it.polimi.ingsw.model.Board.TurnOrderTile;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Enum.TileId;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.factories.GameDataLoader;
import it.polimi.ingsw.model.states.TotemPlacementState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void saveGameCreatesJsonFile() {
        PersistenceManager persistenceManager = new PersistenceManager(tempDir.resolve("current-game.json"));

        persistenceManager.saveGame(createRestorableGame());

        assertTrue(Files.exists(persistenceManager.getSavePath()));
    }

    @Test
    void loadGameRestoresRoundEraPlayersResourcesAndCurrentPlayer() {
        PersistenceManager persistenceManager = new PersistenceManager(tempDir.resolve("current-game.json"));
        Game game = createRestorableGame();

        persistenceManager.saveGame(game);
        Game restored = persistenceManager.loadGame();

        assertEquals(2, restored.getCurrentEra());
        assertEquals(4, restored.getCurrentRound());
        assertEquals(1, restored.getCurrentPlayerIndex());
        assertEquals("Bob", restored.getCurrentActivePlayer().getNickname());
        assertEquals(7, restored.getPlayers().get(0).getFoodAmount());
        assertEquals(12, restored.getPlayers().get(0).getPrestigePoints());
        assertEquals(Color.BLUE, restored.getPlayers().get(1).getColor());
    }

    @Test
    void loadGameRestoresBoardAndOfferTrackEnoughToContinue() {
        PersistenceManager persistenceManager = new PersistenceManager(tempDir.resolve("current-game.json"));
        Game game = createRestorableGame();

        persistenceManager.saveGame(game);
        Game restored = persistenceManager.loadGame();

        assertEquals("tribeCard_1", restored.getBoard().getTopRow().getFirst().getId());
        assertEquals("event_1", restored.getBoard().getBottomRow().getFirst().getId());
        assertEquals("building_1", restored.getBoard().getUpperBuildingCards().getFirst().getId());
        assertEquals("Alice", restored.getOfferTrack().getTiles().getFirst().getPlacedPlayer().getNickname());
        assertEquals("Alice", restored.getTurnOrderTile().getSlots().getFirst().getOccupyingPlayer().orElseThrow().getNickname());
    }

    @Test
    void deleteSaveRemovesSavedGame() {
        PersistenceManager persistenceManager = new PersistenceManager(tempDir.resolve("current-game.json"));
        persistenceManager.saveGame(createRestorableGame());

        persistenceManager.deleteSave();

        assertFalse(Files.exists(persistenceManager.getSavePath()));
    }

    private Game createRestorableGame() {
        Player alice = new Player(Color.RED, "Alice");
        alice.setFoodAmount(7);
        alice.setPrestigePoints(12);
        Player bob = new Player(Color.BLUE, "Bob");
        bob.setFoodAmount(3);
        bob.setPrestigePoints(4);
        List<Player> players = List.of(alice, bob);

        GameDataLoader loader = new GameDataLoader();
        Map<String, TribeDeck> tribeCards = loader.loadAllTribeCardsById(2);
        Map<String, BuildingCard> buildingCards = loader.loadAllBuildingCardsById(2);

        Board board = new Board(
                List.of(tribeCards.get("tribeCard_2")),
                new ArrayList<>(),
                List.of(tribeCards.get("tribeCard_1")),
                List.of(tribeCards.get("event_1")),
                List.of(buildingCards.get("building_1")),
                new ArrayList<>()
        );

        OfferTile firstTile = new OfferTile(0, TileId.B, 0, 1, 2);
        firstTile.restorePlacedPlayer(alice);
        OfferTrack offerTrack = new OfferTrack(List.of(
                firstTile,
                new OfferTile(0, TileId.C, 1, 0, 2)
        ));

        TurnOrderSlot firstSlot = new TurnOrderSlot(1);
        firstSlot.occupy(alice);
        TurnOrderTile turnOrderTile = new TurnOrderTile(List.of(
                firstSlot,
                new TurnOrderSlot(0)
        ));

        return new Game(
                players,
                board,
                offerTrack,
                turnOrderTile,
                List.of(new ArrayList<>(), List.of(buildingCards.get("building_7"))),
                2,
                4,
                1,
                List.of(alice, bob),
                new TotemPlacementState()
        );
    }
}
