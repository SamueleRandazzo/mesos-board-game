package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Board.Board;
import it.polimi.ingsw.model.Board.OfferTile;
import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Cards.EventCard;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Interfaces.EventEffect;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.states.TotemPlacementState;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private static List<TribeDeck> emptyTribeDeckList() {
        return new ArrayList<>();
    }

    private static List<BuildingCard> emptyBuildingList() {
        return new ArrayList<>();
    }

    private static Player newPlayer(Color color, int prestige, int food) {
        Player p = new Player(color, "test");
        p.setPrestigePoints(prestige);
        p.setFoodAmount(food);
        return p;
    }

    private static OfferTrack newOfferTrack() {
        return new OfferTrack(new ArrayList<OfferTile>());
    }

    private static Game newGameWithPlayers(List<Player> players) {
        return new Game(
                players,
                emptyTribeDeckList(),
                emptyBuildingList(),
                emptyBuildingList(),
                emptyBuildingList(),
                newOfferTrack()
        );
    }

    private static void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    private static void invokePrivateVoid(Object target, String methodName) {
        try {
            Method method = target.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class FakeTribeCard extends Card implements TribeDeck {
        protected FakeTribeCard(int era) {
            super("fake_tribe_card_" + era, era, 2, true);
        }

        @Override
        public void applyTo(Player player) {
            // no-op
        }
    }

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
                        emptyTribeDeckList(),
                        emptyBuildingList(),
                        emptyBuildingList(),
                        emptyBuildingList(),
                        newOfferTrack()
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
                        emptyBuildingList(),
                        emptyBuildingList(),
                        emptyBuildingList(),
                        newOfferTrack()
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
                        emptyTribeDeckList(),
                        null,
                        emptyBuildingList(),
                        emptyBuildingList(),
                        newOfferTrack()
                )
        );
    }

    @Test
    void constructor_shouldThrowExceptionWhenEra2BuildingsIsNull() {
        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0)
        );

        assertThrows(NullPointerException.class, () ->
                new Game(
                        players,
                        emptyTribeDeckList(),
                        emptyBuildingList(),
                        null,
                        emptyBuildingList(),
                        newOfferTrack()
                )
        );
    }

    @Test
    void constructor_shouldThrowExceptionWhenEra3BuildingsIsNull() {
        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0)
        );

        assertThrows(NullPointerException.class, () ->
                new Game(
                        players,
                        emptyTribeDeckList(),
                        emptyBuildingList(),
                        emptyBuildingList(),
                        null,
                        newOfferTrack()
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
                        emptyTribeDeckList(),
                        emptyBuildingList(),
                        emptyBuildingList(),
                        emptyBuildingList(),
                        null
                )
        );
    }

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

    @Test
    void getWinner_shouldReturnSingleWinnerWhenOnePlayerHasMorePrestige() {
        Player p1 = newPlayer(Color.RED, 20, 1);
        Player p2 = newPlayer(Color.BLUE, 10, 99);

        Game game = newGameWithPlayers(List.of(p1, p2));

        List<Player> winners = game.getLeaderboard();

        assertEquals(1, winners.size());
        assertSame(p1, winners.get(0));
    }

    @Test
    void getWinner_shouldUseFoodAsTieBreakerWhenPrestigeIsEqual() {
        Player p1 = newPlayer(Color.RED, 20, 3);
        Player p2 = newPlayer(Color.BLUE, 20, 7);

        Game game = newGameWithPlayers(List.of(p1, p2));

        List<Player> winners = game.getLeaderboard();

        assertEquals(1, winners.size());
        assertSame(p2, winners.get(0));
    }

    @Test
    void getWinner_shouldReturnSharedVictoryWhenPrestigeAndFoodAreEqual() {
        Player p1 = newPlayer(Color.RED, 20, 5);
        Player p2 = newPlayer(Color.BLUE, 20, 5);

        Game game = newGameWithPlayers(List.of(p1, p2));

        p1.setFoodAmount(5);
        p2.setFoodAmount(5);

        List<Player> winners = game.getLeaderboard();

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

        List<Player> winners = game.getLeaderboard();

        assertEquals(1, winners.size());
        assertSame(p3, winners.get(0));
    }

    @Test
    void initializeGame_shouldSetTotemPlacementState() {
        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0)
        );

        Game game = newGameWithPlayers(players);

        game.initializeGame();

        Object currentState = getPrivateField(game, "currentState");

        assertTrue(currentState instanceof TotemPlacementState);
    }

    @Test
    void resolveEventsInLowerRow_shouldResolveOnlyLowerRowEventsAndIgnoreNonEvents() {
        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0)
        );

        Game game = newGameWithPlayers(players);
        Board board = game.getBoard();

        final int[] counter = {0};
        EventEffect effect = (ps, gameContext) -> counter[0] += ps.size();

        EventCard event1 = new EventCard("event_1", 1, 2, false, effect);
        EventCard event2 = new EventCard("event_2", 1, 2, false, effect);

        List<TribeDeck> bottomRow = new ArrayList<>();
        bottomRow.add(event1);
        bottomRow.add(new FakeTribeCard(1));
        bottomRow.add(event2);

        setPrivateField(board, "lowerTribeCards", bottomRow);

        invokePrivateVoid(game, "resolveEventsInLowerRow");

        assertEquals(4, counter[0]);
    }
}