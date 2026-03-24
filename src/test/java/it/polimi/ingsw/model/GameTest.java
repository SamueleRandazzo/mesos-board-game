package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Board.OfferTile;
import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.Board.TurnOrderSlot;
import it.polimi.ingsw.model.Board.TurnOrderTile;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import org.junit.jupiter.api.Test;

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
        Player p = new Player(color);
        p.setPrestigePoints(prestige);
        p.setFoodAmount(food);
        return p;
    }

    private static OfferTrack newOfferTrack() {
        return new OfferTrack(new ArrayList<OfferTile>());
    }

    private static Game newGameWithPlayers(List<Player> players, TurnOrderTile t) {
        return new Game(
                players,
                emptyTribeDeckList(),
                emptyBuildingList(),
                emptyBuildingList(),
                emptyBuildingList(),
                newOfferTrack(),
                t
        );
    }

    // -------------------------------------------------------------------------
    // Constructor / creation tests
    // -------------------------------------------------------------------------

    @Test
    void constructor_shouldCreateGameWithMinimumValidPlayers() {
        // TODO Fill tos param and t param
        List<TurnOrderSlot> tos = new ArrayList<>();
        TurnOrderTile t = new TurnOrderTile(tos);

        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0)
        );

        Game game = newGameWithPlayers(players, t);

        assertNotNull(game);
        assertEquals(2, game.getNumPlayers());
        assertEquals(1, game.getCurrentEra());
        assertEquals(1, game.getCurrentRound());
        assertNotNull(game.getBoard());
        assertNotNull(game.getOfferTrack());
    }

    @Test
    void constructor_shouldCreateGameWithMaximumValidPlayers() {
        // TODO Fill tos param and t param
        List<TurnOrderSlot> tos = new ArrayList<>();
        TurnOrderTile t = new TurnOrderTile(tos);

        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0),
                newPlayer(Color.YELLOW, 0, 0),
                newPlayer(Color.WHITE, 0, 0),
                newPlayer(Color.BLACK, 0, 0)
        );

        Game game = newGameWithPlayers(players, t);

        assertNotNull(game);
        assertEquals(5, game.getNumPlayers());
    }

    @Test
    void constructor_shouldThrowExceptionWhenPlayersAreTooFew() {
        // TODO Fill tos param and t param
        List<TurnOrderSlot> tos = new ArrayList<>();
        TurnOrderTile t = new TurnOrderTile(tos);

        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0)
        );

        assertThrows(IllegalArgumentException.class, () -> newGameWithPlayers(players, t));
    }

    @Test
    void constructor_shouldThrowExceptionWhenPlayersAreTooMany() {
        // TODO Fill tos param and t param
        List<TurnOrderSlot> tos = new ArrayList<>();
        TurnOrderTile t = new TurnOrderTile(tos);

        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0),
                newPlayer(Color.YELLOW, 0, 0),
                newPlayer(Color.WHITE, 0, 0),
                newPlayer(Color.BLACK, 0, 0),
                newPlayer(Color.RED, 0, 0)
        );

        assertThrows(IllegalArgumentException.class, () -> newGameWithPlayers(players, t));
    }

    @Test
    void constructor_shouldThrowExceptionWhenPlayersIsNull() {
        // TODO Fill tos param and t param
        List<TurnOrderSlot> tos = new ArrayList<>();
        TurnOrderTile t = new TurnOrderTile(tos);

        assertThrows(NullPointerException.class, () ->
                new Game(
                        null,
                        emptyTribeDeckList(),
                        emptyBuildingList(),
                        emptyBuildingList(),
                        emptyBuildingList(),
                        newOfferTrack(),
                        t
                )
        );
    }

    @Test
    void constructor_shouldThrowExceptionWhenTribeDeckIsNull() {
        // TODO Fill tos param and t param
        List<TurnOrderSlot> tos = new ArrayList<>();
        TurnOrderTile t = new TurnOrderTile(tos);

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
                        newOfferTrack(),
                        t
                )
        );
    }

    @Test
    void constructor_shouldThrowExceptionWhenEra1BuildingsIsNull() {
        // TODO Fill tos param and t param
        List<TurnOrderSlot> tos = new ArrayList<>();
        TurnOrderTile t = new TurnOrderTile(tos);

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
                        newOfferTrack(),
                        t
                )
        );
    }

    @Test
    void constructor_shouldThrowExceptionWhenEra2BuildingsIsNull() {
        // TODO Fill tos param and t param
        List<TurnOrderSlot> tos = new ArrayList<>();
        TurnOrderTile t = new TurnOrderTile(tos);

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
                        newOfferTrack(),
                        t
                )
        );
    }

    @Test
    void constructor_shouldThrowExceptionWhenEra3BuildingsIsNull() {
        // TODO Fill tos param and t param
        List<TurnOrderSlot> tos = new ArrayList<>();
        TurnOrderTile t = new TurnOrderTile(tos);

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
                        newOfferTrack(),
                        t
                )
        );
    }

    @Test
    void constructor_shouldThrowExceptionWhenOfferTrackIsNull() {
        // TODO Fill tos param and t param
        List<TurnOrderSlot> tos = new ArrayList<>();
        TurnOrderTile t = new TurnOrderTile(tos);

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
                        null,
                        t
                )
        );
    }

    // -------------------------------------------------------------------------
    // Getter tests
    // -------------------------------------------------------------------------

    @Test
    void getPlayers_shouldReturnUnmodifiableList() {
        // TODO Fill tos param and t param
        List<TurnOrderSlot> tos = new ArrayList<>();
        TurnOrderTile t = new TurnOrderTile(tos);

        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0)
        );

        Game game = newGameWithPlayers(players, t);

        assertThrows(UnsupportedOperationException.class, () ->
                game.getPlayers().add(newPlayer(Color.YELLOW, 0, 0))
        );
    }

    @Test
    void getters_shouldReturnExpectedInitialValues() {
        // TODO Fill tos param and t param
        List<TurnOrderSlot> tos = new ArrayList<>();
        TurnOrderTile t = new TurnOrderTile(tos);

        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0)
        );

        Game game = newGameWithPlayers(players, t);

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
        // TODO Fill tos param and t param
        List<TurnOrderSlot> tos = new ArrayList<>();
        TurnOrderTile t = new TurnOrderTile(tos);

        Player p1 = newPlayer(Color.RED, 20, 1);
        Player p2 = newPlayer(Color.BLUE, 10, 99);

        Game game = newGameWithPlayers(List.of(p1, p2), t);

        List<Player> winners = game.getWinner();

        assertEquals(1, winners.size());
        assertSame(p1, winners.get(0));
    }

    @Test
    void getWinner_shouldUseFoodAsTieBreakerWhenPrestigeIsEqual() {
        // TODO Fill tos param and t param
        List<TurnOrderSlot> tos = new ArrayList<>();
        TurnOrderTile t = new TurnOrderTile(tos);

        Player p1 = newPlayer(Color.RED, 20, 3);
        Player p2 = newPlayer(Color.BLUE, 20, 7);

        Game game = newGameWithPlayers(List.of(p1, p2), t);

        List<Player> winners = game.getWinner();

        assertEquals(1, winners.size());
        assertSame(p2, winners.get(0));
    }

    @Test
    void getWinner_shouldReturnSharedVictoryWhenPrestigeAndFoodAreEqual() {
        // TODO Fill tos param and t param
        List<TurnOrderSlot> tos = new ArrayList<>();
        TurnOrderTile t = new TurnOrderTile(tos);

        Player p1 = newPlayer(Color.RED, 20, 5);
        Player p2 = newPlayer(Color.BLUE, 20, 5);

        Game game = newGameWithPlayers(List.of(p1, p2), t);

        List<Player> winners = game.getWinner();

        assertEquals(2, winners.size());
        assertTrue(winners.contains(p1));
        assertTrue(winners.contains(p2));
    }

    @Test
    void getWinner_shouldWorkWithMoreThanTwoPlayers() {
        // TODO Fill tos param and t param
        List<TurnOrderSlot> tos = new ArrayList<>();
        TurnOrderTile t = new TurnOrderTile(tos);

        Player p1 = newPlayer(Color.RED, 10, 1);
        Player p2 = newPlayer(Color.BLUE, 15, 2);
        Player p3 = newPlayer(Color.YELLOW, 15, 4);
        Player p4 = newPlayer(Color.WHITE, 8, 10);

        Game game = newGameWithPlayers(List.of(p1, p2, p3, p4), t);

        List<Player> winners = game.getWinner();

        assertEquals(1, winners.size());
        assertSame(p3, winners.get(0));
    }
}