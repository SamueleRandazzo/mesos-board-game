package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Board.OfferTile;
import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Enum.TileId;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.factories.GameDataLoader;
import it.polimi.ingsw.persistence.PersistenceManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceControllerIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void controllerShouldSaveAfterTotemPlacementAndCardPick() throws RemoteException {
        PersistenceManager persistenceManager = new PersistenceManager(tempDir.resolve("current-game.json"));
        Game game = newGame();
        GameController controller = new GameController(game, persistenceManager);
        game.startGame();

        controller.handleTileSelection(0);

        assertTrue(Files.exists(persistenceManager.getSavePath()));
        Game afterFirstTotem = persistenceManager.loadGame();
        assertEquals("p1", afterFirstTotem.getOfferTrack().getTiles().get(0).getPlacedPlayer().getNickname());
        assertEquals(1, afterFirstTotem.getCurrentPlayerIndex());

        controller.handleTileSelection(1);
        controller.handleLowerCardSelection(0);

        Game afterCardPick = persistenceManager.loadGame();
        Player restoredPlayer = afterCardPick.getPlayers().stream()
                .filter(player -> player.getNickname().equals("p1"))
                .findFirst()
                .orElseThrow();

        assertFalse(restoredPlayer.getTribe().getOwnedCards().isEmpty());
        assertEquals(2, afterCardPick.getCurrentRound());
        assertEquals("p1", afterCardPick.getCurrentActivePlayer().getNickname());
        assertTrue(afterCardPick.getOfferTrack().getTiles().stream().allMatch(OfferTile::isAvailable));
    }

    private static Game newGame() {
        GameDataLoader loader = new GameDataLoader();
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2")
        );
        List<TribeDeck> tribeDeck = loader.loadDecks(players.size());
        List<BuildingCard> era1Buildings = loader.loadBuildings(1, players.size());
        List<BuildingCard> era2Buildings = loader.loadBuildings(2, players.size());
        List<BuildingCard> era3Buildings = loader.loadBuildings(3, players.size());

        return new Game(
                players,
                tribeDeck,
                era1Buildings,
                era2Buildings,
                era3Buildings,
                offerTrackForSingleLowerPick()
        );
    }

    private static OfferTrack offerTrackForSingleLowerPick() {
        return new OfferTrack(List.of(
                new OfferTile(0, TileId.B, 0, 1, 2),
                new OfferTile(0, TileId.A, 0, 0, 2)
        ));
    }
}
