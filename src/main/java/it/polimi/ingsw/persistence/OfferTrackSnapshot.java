package it.polimi.ingsw.persistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Serializable snapshot of the offer track.
 */
public class OfferTrackSnapshot {
    /** Saved offer tiles in track order. */
    public List<OfferTileSnapshot> tiles = new ArrayList<>();
}
