package it.polimi.ingsw.model.Board;

import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private static <T> List<T> placeholders(int n) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(null);
        }
        return list;
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

    @Test
    void fillRows_shouldFillUpperRowUpToNumPlayersPlusFour() {
        Board board = new Board(placeholders(10), placeholders(0));

        board.fillRows(3);

        assertEquals(7, board.getTopRow().size());
        assertEquals(3, ((List<?>) getPrivateField(board, "tribeDeck")).size());
    }

    @Test
    void takeCardFromTopRow_shouldRemoveOneCard() {
        Board board = new Board(placeholders(10), placeholders(0));
        board.fillRows(2); // 2 + 4 = 6

        int before = board.getTopRow().size();
        board.takeCardFromTopRow(0);

        assertEquals(before - 1, board.getTopRow().size());
    }

    @Test
    void takeCardFromTopRow_withInvalidIndex_shouldThrowException() {
        Board board = new Board(placeholders(3), placeholders(0));

        assertThrows(IndexOutOfBoundsException.class, () -> board.takeCardFromTopRow(0));
    }

    @Test
    void cleanUpAtRoundEnd_shouldMoveUpperToLowerAndRefillUpper() {
        Board board = new Board(placeholders(20), placeholders(0));

        board.fillRows(2); // upper = 6
        setPrivateField(board, "lowerTribeCards", placeholders(3)); // simulate old lower row

        board.cleanUpAtRoundEnd(2);

        assertEquals(6, board.getBottomRow().size()); // old upper moved down
        assertEquals(6, board.getTopRow().size());    // refilled again
    }

    @Test
    void updateEra_shouldClearLowerBuildingsMoveUpperDownAndLoadNewUpper() {
        Board board = new Board(placeholders(0), placeholders(0));

        setPrivateField(board, "lowerBuildingCards", placeholders(3));
        setPrivateField(board, "upperBuildingCards", placeholders(2));

        board.setBuildingDeck(placeholders(4));
        board.updateEra();

        assertEquals(2, board.getLowerBuildingCards().size());
        assertEquals(4, board.getUpperBuildingCards().size());
    }

    @Test
    void setBuildingDeck_shouldReplaceCurrentDeck() {
        Board board = new Board(placeholders(0), placeholders(2));

        board.setBuildingDeck(placeholders(5));

        List<BuildingCard> deck = getPrivateField(board, "buildingDeck");
        assertEquals(5, deck.size());
    }

    @Test
    void isTribeDeckEmpty_shouldReturnTrueWhenDeckIsEmpty() {
        Board board = new Board(placeholders(0), placeholders(0));

        assertTrue(board.isTribeDeckEmpty());
    }

    @Test
    void fillRows_shouldStopIfDeckIsShorterThanTarget() {
        Board board = new Board(placeholders(3), placeholders(0));

        board.fillRows(3); // target would be 7, but deck has only 3 cards

        assertEquals(3, board.getTopRow().size());
        assertTrue(board.isTribeDeckEmpty());
    }

    @Test
    void takeCardFromBottomRow_withInvalidIndex_shouldThrowException() {
        Board board = new Board(placeholders(0), placeholders(0));

        assertThrows(IndexOutOfBoundsException.class, () -> board.takeCardFromBottomRow(0));

        setPrivateField(board, "lowerTribeCards", placeholders(2));

        assertThrows(IndexOutOfBoundsException.class, () -> board.takeCardFromBottomRow(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> board.takeCardFromBottomRow(2));
    }

    @Test
    void cleanUpAtRoundEnd_shouldHandleEmptyUpperRow() {
        Board board = new Board(placeholders(0), placeholders(0));

        setPrivateField(board, "lowerTribeCards", placeholders(3));
        setPrivateField(board, "upperTribeCards", placeholders(0));

        board.cleanUpAtRoundEnd(2);

        assertEquals(0, board.getBottomRow().size());
        assertEquals(0, board.getTopRow().size());
    }
}