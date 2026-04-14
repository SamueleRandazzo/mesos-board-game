package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Board.OfferTile;
import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Enum.TileId;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.states.TotemPlacementState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameFlowTest {

    private static List<TribeDeck> emptyTribeDeckList() {
        return new ArrayList<>();
    }

    private static List<BuildingCard> emptyBuildingList() {
        return new ArrayList<>();
    }

    private static OfferTrack newOfferTrack() {
        List<OfferTile> tiles = new ArrayList<>();
        tiles.add(new OfferTile(0, TileId.A, 1, 0));
        tiles.add(new OfferTile(1, TileId.B, 0, 1));
        return new OfferTrack(tiles);
    }

    private static Game newGame(List<Player> players) {
        return new Game(
                players,
                emptyTribeDeckList(),
                emptyBuildingList(),
                emptyBuildingList(),
                emptyBuildingList(),
                newOfferTrack()
        );
    }

    @Test
    void advanceTurn_shouldMoveToNextPlayer() {
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2"),
                new Player(Color.YELLOW, "p3")
        );

        Game game = newGame(players);

        assertEquals(players.get(0), game.getCurrentActivePlayer());

        game.advanceTurn();
        assertEquals(players.get(1), game.getCurrentActivePlayer());

        game.advanceTurn();
        assertEquals(players.get(2), game.getCurrentActivePlayer());
    }

    @Test
    void advanceTurn_shouldWrapAroundToFirstPlayer() {
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2")
        );

        Game game = newGame(players);

        assertEquals(players.get(0), game.getCurrentActivePlayer());

        game.advanceTurn();
        assertEquals(players.get(1), game.getCurrentActivePlayer());

        game.advanceTurn();
        assertEquals(players.get(0), game.getCurrentActivePlayer());
    }

    @Test
    void placePlayerTotem_shouldOccupyTileAndAdvanceTurn() {
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2")
        );

        Game game = newGame(players);
        game.initializeGame();
        game.setState(new TotemPlacementState());

        assertTrue(game.getOfferTrack().getTiles().get(0).isAvailable());
        assertEquals(players.get(0), game.getCurrentActivePlayer());

        game.placePlayerTotem(0);

        assertFalse(game.getOfferTrack().getTiles().get(0).isAvailable());
        assertEquals(players.get(1), game.getCurrentActivePlayer());
    }

    @Test
    void placePlayerTotem_shouldThrowIfTileAlreadyOccupied() {
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2")
        );

        Game game = newGame(players);
        game.initializeGame();
        game.setState(new TotemPlacementState());

        game.placePlayerTotem(0);

        assertThrows(IllegalStateException.class, () ->
                game.placePlayerTotem(0)
        );
    }

    @Test
    void nextRound_shouldIncrementRoundAndResetCurrentPlayer() {
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2")
        );

        Game game = newGame(players);

        game.advanceTurn();
        assertEquals(players.get(1), game.getCurrentActivePlayer());

        int roundBefore = game.getCurrentRound();
        game.nextRound();

        assertEquals(roundBefore + 1, game.getCurrentRound());
        assertEquals(players.get(0), game.getCurrentActivePlayer());
    }

    @Test
    void getCurrentActivePlayer_shouldReturnFirstPlayerInitially() {
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2")
        );

        Game game = newGame(players);

        assertEquals(players.get(0), game.getCurrentActivePlayer());
    }

    @Test
    void placePlayerTotem_shouldThrowIfTileIndexIsInvalid() {
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2")
        );

        Game game = newGame(players);
        game.initializeGame();
        game.setState(new TotemPlacementState());

        assertThrows(IllegalStateException.class, () -> game.placePlayerTotem(-1));
        assertThrows(IllegalStateException.class, () -> game.placePlayerTotem(99));
    }

    @Test
    void initializeGame_shouldResetRoundAndCurrentPlayer() {
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2")
        );

        Game game = newGame(players);

        game.advanceTurn();
        assertEquals(players.get(1), game.getCurrentActivePlayer());

        game.nextRound();
        assertTrue(game.getCurrentRound() >= 2);

        game.initializeGame();

        assertEquals(1, game.getCurrentRound());
        assertEquals(players.get(0), game.getCurrentActivePlayer());
    }
}