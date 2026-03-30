package it.polimi.ingsw.model.Board;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Enum.TileId;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OfferTileTest {

    @Test
    void constructor_shouldInitializeAllFieldsCorrectly() {
        OfferTile tile = new OfferTile(2, TileId.A, 1, 2);

        assertEquals(2, tile.getFoodBonus());
        assertEquals(TileId.A, tile.getTileId());
        assertEquals(1, tile.getTopRowDraws());
        assertEquals(2, tile.getBottomRowDraws());
        assertTrue(tile.isAvailable());
    }

    @Test
    void placeTotem_shouldMakeTileUnavailable() {
        OfferTile tile = new OfferTile(0, TileId.B, 1, 0);
        Player player = new Player(Color.RED, "player-red");

        tile.placeTotem(player);

        assertFalse(tile.isAvailable());
    }

    @Test
    void removeTotem_shouldMakeTileAvailableAgain() {
        OfferTile tile = new OfferTile(0, TileId.C, 0, 1);
        Player player = new Player(Color.BLUE, "player-blue");

        tile.placeTotem(player);
        tile.removeTotem();

        assertTrue(tile.isAvailable());
    }

    @Test
    void placeTotem_shouldOverwriteAvailabilityStateAsExpected() {
        OfferTile tile = new OfferTile(1, TileId.D, 1, 1);
        Player player = new Player(Color.YELLOW, "player-yellow");

        assertTrue(tile.isAvailable());
        tile.placeTotem(player);
        assertFalse(tile.isAvailable());
    }
}