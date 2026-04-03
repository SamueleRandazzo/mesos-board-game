package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Board.Board;
import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Cards.EventCard;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Interfaces.EventEffect;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameAdvancedTest {

    private static List<TribeDeck> emptyTribeDeckList() {
        return new ArrayList<>();
    }

    private static List<BuildingCard> emptyBuildingList() {
        return new ArrayList<>();
    }

    private static List<BuildingCard> buildingPlaceholders(int n) {
        List<BuildingCard> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(null);
        }
        return list;
    }

    private static Player newPlayer(Color color, int prestige, int food) {
        Player p = new Player(color, "player-" + color);
        p.setPrestigePoints(prestige);
        p.setFoodAmount(food);
        return p;
    }

    private static OfferTrack newOfferTrack() {
        return new OfferTrack(new ArrayList<>());
    }

    private static Game newGameWithPlayersAndBuildings(List<Player> players,
                                                       List<BuildingCard> era1,
                                                       List<BuildingCard> era2,
                                                       List<BuildingCard> era3) {
        return new Game(
                players,
                emptyTribeDeckList(),
                era1,
                era2,
                era3,
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

    private static void invokePrivateVoid(Object target, String methodName) {
        try {
            Method method = target.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finta carta tribù usata solo per controllare l'era nei test.
     */
    private static class FakeTribeCard extends Card implements TribeDeck {
        protected FakeTribeCard(int era) {
            super(era, 2, true);
        }

        @Override
        public void applyTo(Player player) {
            // niente: è solo una fake per i test
        }
    }

    @Test
    void checkEraTransition_shouldNotAdvanceEraWhenTopRowHasOnlyCurrentEraCards() {
        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0)
        );

        Game game = newGameWithPlayersAndBuildings(
                players,
                emptyBuildingList(),
                buildingPlaceholders(3),
                buildingPlaceholders(4)
        );

        Board board = game.getBoard();
        setPrivateField(board, "upperTribeCards", List.of(new FakeTribeCard(1)));

        invokePrivateVoid(game, "checkEraTransition");

        assertEquals(1, game.getCurrentEra());
        assertEquals(0, board.getUpperBuildingCards().size());
        assertEquals(0, board.getLowerBuildingCards().size());
    }

    @Test
    void checkEraTransition_shouldAdvanceEraAndLoadNewBuildingsWhenHigherEraCardAppears() {
        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0)
        );

        Game game = newGameWithPlayersAndBuildings(
                players,
                emptyBuildingList(),
                buildingPlaceholders(3),
                buildingPlaceholders(4)
        );

        Board board = game.getBoard();
        setPrivateField(board, "upperTribeCards", List.of(new FakeTribeCard(2)));

        invokePrivateVoid(game, "checkEraTransition");

        assertEquals(2, game.getCurrentEra());
        assertEquals(3, board.getUpperBuildingCards().size());
        assertEquals(0, board.getLowerBuildingCards().size());
    }

    @Test
    void eventCard_shouldBeCompatibleWithBoardRows() {
        assertTrue(
                TribeDeck.class.isAssignableFrom(EventCard.class),
                "EventCard should implement TribeDeck so it can appear in Board rows typed as List<TribeDeck>."
        );
    }

    @Test
    void resolveRemainingEvents_shouldResolveOnlyEventsFromBothRows() {
        List<Player> players = List.of(
                newPlayer(Color.RED, 0, 0),
                newPlayer(Color.BLUE, 0, 0)
        );

        Game game = newGameWithPlayersAndBuildings(
                players,
                emptyBuildingList(),
                emptyBuildingList(),
                emptyBuildingList()
        );

        Board board = game.getBoard();

        final int[] counter = {0};
        EventEffect effect = ps -> counter[0] += ps.size();

        EventCard bottomEvent = new EventCard(1, 2, false, effect);
        EventCard topEvent = new EventCard(1, 2, false, effect);

        List<TribeDeck> bottomRow = new ArrayList<>();
        bottomRow.add(bottomEvent);
        bottomRow.add(new FakeTribeCard(1)); // non-evento, da ignorare

        List<TribeDeck> topRow = new ArrayList<>();
        topRow.add(topEvent);

        setPrivateField(board, "lowerTribeCards", bottomRow);
        setPrivateField(board, "upperTribeCards", topRow);

        invokePrivateVoid(game, "resolveRemainingEvents");

        // 2 eventi x 2 giocatori = 4 chiamate totali all'effect
        assertEquals(4, counter[0]);
    }
}