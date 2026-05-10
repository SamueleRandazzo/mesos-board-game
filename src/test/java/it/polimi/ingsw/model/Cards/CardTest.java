package it.polimi.ingsw.model.Cards;

import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    /**
     * A concrete Dummy class used exclusively to test the abstract Card class.
     * This isolates the test from specific implementations like EventCard or BuildingCard.
     */
    private static class DummyCard extends Card {
        public DummyCard(int era, int minPlayer, boolean isObtainable) {
            super("dummy_card", era, minPlayer, isObtainable);
        }

        @Override
        public void applyTo(Player player) {
            // Dummy implementation, not needed for testing Card's base logic
        }
    }

    @Test
    void testConstructor_ValidParameters() {
        // SETUP & EXECUTE: Testing the lowest accepted boundaries (Era 1, 2 Players)
        DummyCard cardMin = new DummyCard(1, 2, true);

        // ASSERT
        assertEquals(1, cardMin.getEra(), "Era should be exactly 1.");
        assertEquals(2, cardMin.getMinPlayer(), "Minimum players should be exactly 2.");
        assertTrue(cardMin.getIsObtainable(), "Card should be obtainable.");

        // SETUP & EXECUTE: Testing the highest accepted boundaries (Era 3, 5 Players)
        DummyCard cardMax = new DummyCard(3, 5, false);

        // ASSERT
        assertEquals(3, cardMax.getEra(), "Era should be exactly 3.");
        assertEquals(5, cardMax.getMinPlayer(), "Minimum players should be exactly 5.");
        assertFalse(cardMax.getIsObtainable(), "Card should not be obtainable.");

        // SETUP & EXECUTE: Testing middle boundaries (Era 2, 4 Players)
        DummyCard cardMid = new DummyCard(2, 4, true);
        assertEquals(2, cardMid.getEra(), "Era should be exactly 2.");
        assertEquals(4, cardMid.getMinPlayer(), "Minimum players should be exactly 4.");
    }

    @Test
    void testConstructor_InvalidEraThrowsException() {
        // EXECUTE & ASSERT: Eras must be strictly 1, 2, or 3.

        // Era < 1
        assertThrows(IllegalArgumentException.class, () -> new DummyCard(0, 3, true),
                "Should throw an exception for Era 0.");

        assertThrows(IllegalArgumentException.class, () -> new DummyCard(-1, 3, true),
                "Should throw an exception for negative Era.");

        // Era > 3
        assertThrows(IllegalArgumentException.class, () -> new DummyCard(4, 3, true),
                "Should throw an exception for Era 4.");
    }

    @Test
    void testConstructor_InvalidMinPlayerThrowsException() {
        // EXECUTE & ASSERT: MinPlayer must be strictly between 2 and 5.

        // minPlayer < 2
        assertThrows(IllegalArgumentException.class, () -> new DummyCard(2, 1, true),
                "Should throw an exception if minimum players is 1.");

        assertThrows(IllegalArgumentException.class, () -> new DummyCard(2, 0, true),
                "Should throw an exception if minimum players is 0.");

        // minPlayer > 5
        assertThrows(IllegalArgumentException.class, () -> new DummyCard(2, 6, true),
                "Should throw an exception if minimum players is 6.");
    }
}