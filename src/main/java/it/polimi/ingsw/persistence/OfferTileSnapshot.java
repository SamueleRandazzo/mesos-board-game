package it.polimi.ingsw.persistence;

/**
 * Serializable snapshot of a single offer track tile.
 */
public class OfferTileSnapshot {
    /** Food bonus granted by the tile. */
    public int foodBonus;
    /** Identifier of the tile. */
    public String tileId;
    /** Number of upper-row draws granted by the tile. */
    public int topRowDraws;
    /** Number of lower-row draws granted by the tile. */
    public int bottomRowDraws;
    /** Minimum player count required for this tile. */
    public int minPlayers;
    /** Nickname of the player occupying the tile, or {@code null} if empty. */
    public String placedPlayerNickname;
}
