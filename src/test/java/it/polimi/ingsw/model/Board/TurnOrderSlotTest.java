package it.polimi.ingsw.model.Board;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TurnOrderSlotTest {

    @Test
    void constructor_shouldStoreFoodModifier() {
        TurnOrderSlot slot = new TurnOrderSlot(-1);

        assertEquals(-1, slot.getFoodModifier());
        assertFalse(slot.isOccupied());
    }

    @Test
    void occupy_shouldMarkSlotAsOccupied() {
        TurnOrderSlot slot = new TurnOrderSlot(2);
        Player player = new Player(Color.RED, "red");

        slot.occupy(player);

        assertTrue(slot.isOccupied());
        assertEquals(player, slot.getOccupyingPlayer().orElseThrow(IllegalStateException::new));
    }

    @Test
    void occupy_shouldThrowIfPlayerIsNull() {
        TurnOrderSlot slot = new TurnOrderSlot(0);

        assertThrows(IllegalArgumentException.class, () -> slot.occupy(null));
    }

    @Test
    void occupy_shouldThrowIfSlotAlreadyOccupied() {
        TurnOrderSlot slot = new TurnOrderSlot(0);
        Player p1 = new Player(Color.RED, "p1");
        Player p2 = new Player(Color.BLUE, "p2");

        slot.occupy(p1);

        assertThrows(IllegalStateException.class, () -> slot.occupy(p2));
    }
}