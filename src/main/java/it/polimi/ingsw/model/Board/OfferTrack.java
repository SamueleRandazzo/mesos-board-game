package it.polimi.ingsw.model.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the Offer Track on the game board.
 * The track manages a collection of {@link OfferTile}s available during the game,
 * providing methods to query their availability and retrieve the current state of the track.
 */
public class OfferTrack {

    private final List<OfferTile> tiles;

    /**
     * Constructs an OfferTrack and initializes it with the specified list of offer tiles.
     *
     * @param initialTiles the list of {@link OfferTile}s to populate the track for the current game
     */
    public OfferTrack(List<OfferTile> initialTiles){
        this.tiles = new ArrayList<>(initialTiles);
    }

    /**
     * Identifies and returns all tiles on the track that do not currently have a totem placed on them.
     *
     * @return a {@link List} of available {@link OfferTile}s
     */
    public List<OfferTile> getAvailableOffers(){
        return tiles.stream()
                .filter(OfferTile::isAvailable).collect(Collectors.toList());
    }

    /**
     * Returns a shallow copy of the tiles currently on the track.
     * Modifying the returned list will not affect the track itself, though the tiles
     * inside the list remain the same references.
     *
     * @return a new {@link List} containing all the {@link OfferTile}s on the track
     */
    public List<OfferTile> getTiles(){
        return new ArrayList<>(this.tiles);
    }
}