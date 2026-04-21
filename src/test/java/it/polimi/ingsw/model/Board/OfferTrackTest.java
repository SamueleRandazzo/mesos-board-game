package it.polimi.ingsw.model.Board;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Enum.TileId;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OfferTrackTest {

    @Test
    void getAvailableOffers_shouldReturnOnlyFreeTiles() {
        OfferTile tile1 = new OfferTile(0, TileId.A, 1, 0, 2);
        OfferTile tile2 = new OfferTile(1, TileId.B, 0, 1, 2);
        OfferTile tile3 = new OfferTile(2, TileId.C, 1, 1, 2);

        tile2.placeTotem(new Player(Color.RED, "red"));

        OfferTrack track = new OfferTrack(List.of(tile1, tile2, tile3));

        List<OfferTile> available = track.getAvailableOffers();

        assertEquals(2, available.size());
        assertTrue(available.contains(tile1));
        assertTrue(available.contains(tile3));
        assertFalse(available.contains(tile2));
    }

    @Test
    void getTiles_shouldReturnCopyAndNotOriginalList() {
        OfferTile tile1 = new OfferTile(0, TileId.A, 1, 0, 2);
        OfferTile tile2 = new OfferTile(1, TileId.B, 0, 1, 2);

        OfferTrack track = new OfferTrack(List.of(tile1, tile2));

        List<OfferTile> copy = track.getTiles();
        copy.clear();

        assertEquals(2, track.getTiles().size());
    }

    @Test
    void getAvailableOffers_shouldReturnEmptyListIfAllTilesOccupied() {
        OfferTile tile1 = new OfferTile(0, TileId.A, 1, 0, 2);
        OfferTile tile2 = new OfferTile(1, TileId.B, 0, 1, 2);

        tile1.placeTotem(new Player(Color.RED, "red"));
        tile2.placeTotem(new Player(Color.BLUE, "blue"));

        OfferTrack track = new OfferTrack(List.of(tile1, tile2));

        assertTrue(track.getAvailableOffers().isEmpty());
    }
}