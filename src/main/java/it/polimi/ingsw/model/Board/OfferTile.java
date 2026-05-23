package it.polimi.ingsw.model.Board;

import it.polimi.ingsw.model.Enum.TileId;
import it.polimi.ingsw.model.Player;

/**
 * Represents an Offer Tile in the game board.
 * An Offer Tile provides specific bonuses (such as food or card draws) when a player
 * claims it by placing their totem on it. It also tracks occupancy and player-count requirements.
 */
public class OfferTile {

    private final int foodBonus;
    private final TileId tileId;
    private final int topRowDraws;
    private final int bottomRowDraws;
    private Player placedPlayer;
    private final int minPlayers;

    /**
     * Constructs a new OfferTile with the specified bonuses and constraints.
     *
     * @param foodBonus      the amount of food bonus granted by this tile
     * @param tileId         the unique identifier (letter) of the tile
     * @param topRowDraws    the number of cards a player can draw from the top row
     * @param bottomRowDraws the number of cards a player can draw from the bottom row
     * @param minPlayers     the minimum number of players required in the game to use this tile
     */
    public OfferTile(int foodBonus, TileId tileId, int topRowDraws, int bottomRowDraws, int minPlayers){
        this.foodBonus = foodBonus;
        this.tileId = tileId;
        this.topRowDraws = topRowDraws;
        this.bottomRowDraws = bottomRowDraws;
        this.placedPlayer = null;
        this.minPlayers = minPlayers;
    }

    /**
     * Returns the food bonus granted by this tile.
     *
     * @return the amount of food bonus
     */
    public int getFoodBonus(){
        return this.foodBonus;
    }

    /**
     * Returns the number of cards that the player can draw from the top row.
     *
     * @return the number of top row card draws
     */
    public int getTopRowDraws(){
        return this.topRowDraws;
    }

    /**
     * Returns the number of cards that the player can draw from the bottom row.
     *
     * @return the number of bottom row card draws
     */
    public int getBottomRowDraws(){
        return this.bottomRowDraws;
    }

    /**
     * Returns the unique identifier of this tile.
     *
     * @return the {@link TileId} representing the tile's letter/ID
     */
    public TileId getTileId(){
        return this.tileId;
    }

    /**
     * Checks if the tile is currently available for placement.
     * A tile is considered available if no player has placed a totem on it yet.
     *
     * @return true if the tile is unoccupied, false otherwise
     */
    public boolean isAvailable(){
        return placedPlayer == null;
    }

    /**
     * Returns the minimum number of players required in the game configuration
     * for this tile to be active or loaded onto the board.
     *
     * @return the minimum player count required
     */
    public int getMinPlayers() {
        return minPlayers;
    }

    /**
     * Places a player's totem on this tile. This action assigns the tile to the player
     * and sets their corresponding drawing capacities for both the upper and lower rows.
     *
     * @param player the {@link Player} who is claiming the tile and placing their totem
     */
    public void placeTotem(Player player){
        this.placedPlayer = player;

        player.setUpperPick(this.topRowDraws);
        player.setLowerPick(this.bottomRowDraws);
    }

    /**
     * Restores a previously placed player onto this tile without altering
     * the player's drawing statistics. Useful during state restoration or undo actions.
     *
     * @param player the {@link Player} to be restored on this tile
     */
    public void restorePlacedPlayer(Player player) {
        this.placedPlayer = player;
    }

    /**
     * Removes the totem from this tile, making it unoccupied.
     * Typically called during cleanup at the end of a game phase.
     */
    public void removeTotem(){
        this.placedPlayer = null;
    }

    /**
     * Returns the player who has currently placed their totem on this tile.
     *
     * @return the {@link Player} instance occupying this tile, or null if it is unoccupied
     */
    public Player getPlacedPlayer(){
        return this.placedPlayer;
    }

}