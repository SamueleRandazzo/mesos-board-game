package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.Board.OfferTile;
import it.polimi.ingsw.model.Board.TurnOrderTile;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Enum.Color;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    /**
     * Utility: creates a list of null placeholders.
     * Useful when the tested method only needs the list size / structure.
     */
    private static <T> List<T> placeholders(int n) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(null);
        }
        return list;
    }

    /**
     * Utility: creates a valid player.
     */
    private static Player newPlayer(Color color, int prestige, int food) {
        Player p = new Player(color);
        p.setPrestigePoints(prestige);
        p.setFoodAmount(food);
        return p;
    }

    /**
     * Utility: creates a minimal OfferTrack.
     * Empty is fine for the tests below because we are not exercising round logic yet.
     */
    private static OfferTrack newOfferTrack() {
        return new OfferTrack(new ArrayList<OfferTile>());
    }

    /**
     * Utility: creates a valid Game instance with the provided players.
     * TurnOrderTile is passed as null because current tested methods do not use it.
     */
    private static Game newGameWithPlayers(List<Player> players) {
        return new Game(
                players,
                placeholders(0),   // tribeDeck
                placeholders(0),   // era1Buildings
                placeholders(0),   // era2Buildings
                placeholders(0),   // era3Buildings
                newOfferTrack(),
                null               // turnOrderTile
        );
    }

    // -------------------------------------------------------------------------
    // Constructor / creation tests
    // -------------------------------------------------------------------------

    @Test
    void constructor_shouldCreateGameWithMinimumValidPlayers() {
        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0)
        );

        Game game = newGameWithPlayers(players);

        assertNotNull(game);
        assertEquals(2, game.getNumPlayers());
        assertEquals(1, game.getCurrentEra());
        assertEquals(1, game.getCurrentRound());
        assertNotNull(game.getBoard());
        assertNotNull(game.getOfferTrack());
    }

    @Test
    void constructor_shouldCreateGameWithMaximumValidPlayers() {
        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0),
                newPlayer(Color.YELLOW, 0, 0),
                newPlayer(Color.WHITE, 0, 0),
                newPlayer(Color.BLACK, 0, 0)
        );

        Game game = newGameWithPlayers(players);

        assertNotNull(game);
        assertEquals(5, game.getNumPlayers());
    }

    @Test
    void constructor_shouldThrowExceptionWhenPlayersAreTooFew() {
        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0)
        );

        assertThrows(IllegalArgumentException.class, () -> newGameWithPlayers(players));
    }

    @Test
    void constructor_shouldThrowExceptionWhenPlayersAreTooMany() {
        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0),
                newPlayer(Color.YELLOW, 0, 0),
                newPlayer(Color.WHITE, 0, 0),
                newPlayer(Color.BLACK, 0, 0),
                newPlayer(Color.RED, 0, 0)
        );

        assertThrows(IllegalArgumentException.class, () -> newGameWithPlayers(players));
    }

    @Test
    void constructor_shouldThrowExceptionWhenPlayersIsNull() {
        assertThrows(NullPointerException.class, () ->
                new Game(
                        null,
                        placeholders(0),
                        placeholders(0),
                        placeholders(0),
                        placeholders(0),
                        newOfferTrack(),
                        null
                )
        );
    }

    @Test
    void constructor_shouldThrowExceptionWhenTribeDeckIsNull() {
        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0)
        );

        assertThrows(NullPointerException.class, () ->
                new Game(
                        players,
                        null,
                        placeholders(0),
                        placeholders(0),
                        placeholders(0),
                        newOfferTrack(),
                        null
                )
        );
    }

    @Test
    void constructor_shouldThrowExceptionWhenEra1BuildingsIsNull() {
        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0)
        );

        assertThrows(NullPointerException.class, () ->
                new Game(
                        players,
                        placeholders(0),
                        null,
                        placeholders(0),
                        placeholders(0),
                        newOfferTrack(),
                        null
                )
        );
    }

    @Test
    void constructor_shouldThrowExceptionWhenOfferTrackIsNull() {
        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0)
        );

        assertThrows(NullPointerException.class, () ->
                new Game(
                        players,
                        placeholders(0),
                        placeholders(0),
                        placeholders(0),
                        placeholders(0),
                        null,
                        null
                )
        );
    }

    // -------------------------------------------------------------------------
    // Getter tests
    // -------------------------------------------------------------------------

    @Test
    void getPlayers_shouldReturnUnmodifiableList() {
        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0)
        );

        Game game = newGameWithPlayers(players);

        assertThrows(UnsupportedOperationException.class, () ->
                game.getPlayers().add(newPlayer(Color.YELLOW, 0, 0))
        );
    }

    @Test
    void getters_shouldReturnExpectedInitialValues() {
        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0)
        );

        Game game = newGameWithPlayers(players);

        assertEquals(2, game.getNumPlayers());
        assertEquals(1, game.getCurrentEra());
        assertEquals(1, game.getCurrentRound());
        assertNotNull(game.getBoard());
        assertNotNull(game.getOfferTrack());
    }

    // -------------------------------------------------------------------------
    // Winner tests
    // -------------------------------------------------------------------------

    @Test
    void getWinner_shouldReturnSingleWinnerWhenOnePlayerHasMorePrestige() {
        Player p1 = newPlayer(Color.RED, 20, 1);
        Player p2 = newPlayer(Color.BLUE, 10, 99);

        Game game = newGameWithPlayers(List.of(p1, p2));

        List<Player> winners = game.getWinner();

        assertEquals(1, winners.size());
        assertSame(p1, winners.get(0));
    }

    @Test
    void getWinner_shouldUseFoodAsTieBreakerWhenPrestigeIsEqual() {
        Player p1 = newPlayer(Color.RED, 20, 3);
        Player p2 = newPlayer(Color.BLUE, 20, 7);

        Game game = newGameWithPlayers(List.of(p1, p2));

        List<Player> winners = game.getWinner();

        assertEquals(1, winners.size());
        assertSame(p2, winners.get(0));
    }

    @Test
    void getWinner_shouldReturnSharedVictoryWhenPrestigeAndFoodAreEqual() {
        Player p1 = newPlayer(Color.RED, 20, 5);
        Player p2 = newPlayer(Color.BLUE, 20, 5);

        Game game = newGameWithPlayers(List.of(p1, p2));

        List<Player> winners = game.getWinner();

        assertEquals(2, winners.size());
        assertTrue(winners.contains(p1));
        assertTrue(winners.contains(p2));
    }

    @Test
    void getWinner_shouldWorkWithMoreThanTwoPlayers() {
        Player p1 = newPlayer(Color.RED, 10, 1);
        Player p2 = newPlayer(Color.BLUE, 15, 2);
        Player p3 = newPlayer(Color.YELLOW, 15, 4);
        Player p4 = newPlayer(Color.WHITE, 8, 10);

        Game game = newGameWithPlayers(List.of(p1, p2, p3, p4));

        List<Player> winners = game.getWinner();

        assertEquals(1, winners.size());
        assertSame(p3, winners.get(0));
    }
}