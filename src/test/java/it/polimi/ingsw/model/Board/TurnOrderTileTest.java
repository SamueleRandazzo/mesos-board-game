package it.polimi.ingsw.model.Board;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TurnOrderTileTest {

    @Test
    void placeTotem_shouldPlacePlayerInFirstFreeSlotAndReturnFoodModifier() {
        TurnOrderSlot slot1 = new TurnOrderSlot(2);
        TurnOrderSlot slot2 = new TurnOrderSlot(0);
        TurnOrderTile tile = new TurnOrderTile(List.of(slot1, slot2));

        Player player = new Player(Color.RED, "red");

        int modifier = tile.placeTotem(player);

        assertEquals(2, modifier);
        assertTrue(slot1.isOccupied());
        assertFalse(slot2.isOccupied());
        assertEquals(player, slot1.getOccupyingPlayer().orElseThrow(IllegalStateException::new));
    }

    @Test
    void placeTotem_shouldUseNextFreeSlotIfFirstIsOccupied() {
        TurnOrderSlot slot1 = new TurnOrderSlot(2);
        TurnOrderSlot slot2 = new TurnOrderSlot(-1);
        TurnOrderTile tile = new TurnOrderTile(List.of(slot1, slot2));

        Player p1 = new Player(Color.RED, "p1");
        Player p2 = new Player(Color.BLUE, "p2");

        tile.placeTotem(p1);
        int modifier = tile.placeTotem(p2);

        assertEquals(-1, modifier);
        assertEquals(p1, slot1.getOccupyingPlayer().orElseThrow(IllegalStateException::new));
        assertEquals(p2, slot2.getOccupyingPlayer().orElseThrow(IllegalStateException::new));
    }

    @Test
    void placeTotem_shouldThrowIfTrackIsFull() {
        TurnOrderSlot slot1 = new TurnOrderSlot(0);
        TurnOrderTile tile = new TurnOrderTile(List.of(slot1));

        tile.placeTotem(new Player(Color.RED, "red"));

        assertThrows(IllegalStateException.class, () ->
                tile.placeTotem(new Player(Color.BLUE, "blue"))
        );
    }

    @Test
    void getNextRoundOrder_shouldReturnPlayersInSlotOrder() {
        TurnOrderSlot slot1 = new TurnOrderSlot(2);
        TurnOrderSlot slot2 = new TurnOrderSlot(0);
        TurnOrderSlot slot3 = new TurnOrderSlot(-1);

        TurnOrderTile tile = new TurnOrderTile(List.of(slot1, slot2, slot3));

        Player p1 = new Player(Color.RED, "p1");
        Player p2 = new Player(Color.BLUE, "p2");

        tile.placeTotem(p1);
        tile.placeTotem(p2);

        List<Player> order = tile.getNextRoundOrder();

        assertEquals(2, order.size());
        assertEquals(p1, order.get(0));
        assertEquals(p2, order.get(1));
    }
}