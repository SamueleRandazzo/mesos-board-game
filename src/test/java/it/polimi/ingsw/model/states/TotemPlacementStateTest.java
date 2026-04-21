package it.polimi.ingsw.model.states;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Board.OfferTile;
import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Enum.TileId;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TotemPlacementStateTest {

    private static Game newGame(List<Player> players) {
        List<OfferTile> tiles = new ArrayList<>();
        tiles.add(new OfferTile(0, TileId.A, 1, 0, 2));
        tiles.add(new OfferTile(1, TileId.B, 0, 1, 2));

        return new Game(
                players,
                new ArrayList<TribeDeck>(),
                new ArrayList<BuildingCard>(),
                new ArrayList<BuildingCard>(),
                new ArrayList<BuildingCard>(),
                new OfferTrack(tiles)
        );
    }

    @SuppressWarnings("unchecked")
    private static <T> T getPrivateField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void placeTotem_shouldOccupyTileWhenValid() {
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2")
        );

        Game game = newGame(players);
        game.initializeGame();
        game.setState(new TotemPlacementState());

        TotemPlacementState state = new TotemPlacementState();

        state.placeTotem(game, players.get(0), 0);

        assertFalse(game.getOfferTrack().getTiles().get(0).isAvailable());
    }

    @Test
    void placeTotem_shouldThrowIfTileIndexInvalid() {
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2")
        );

        Game game = newGame(players);
        game.initializeGame();
        game.setState(new TotemPlacementState());

        TotemPlacementState state = new TotemPlacementState();

        assertThrows(IllegalStateException.class,
                () -> state.placeTotem(game, players.get(0), -1));
    }

    @Test
    void placeTotem_shouldThrowIfTileAlreadyOccupied() {
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2")
        );

        Game game = newGame(players);
        game.initializeGame();
        game.setState(new TotemPlacementState());

        TotemPlacementState state = new TotemPlacementState();

        state.placeTotem(game, players.get(0), 0);

        assertThrows(IllegalStateException.class,
                () -> state.placeTotem(game, players.get(1), 0));
    }

    @Test
    void placeTotem_shouldSwitchStateWhenAllPlayersPlacedTotem() {
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2")
        );

        Game game = newGame(players);
        game.initializeGame();
        game.setState(new TotemPlacementState());

        TotemPlacementState state = new TotemPlacementState();

        state.placeTotem(game, players.get(0), 0);
        state.placeTotem(game, players.get(1), 1);

        Object currentState = getPrivateField(game, "currentState");

        assertTrue(currentState instanceof ActionResolutionState);
    }
}