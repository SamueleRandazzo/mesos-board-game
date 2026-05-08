package it.polimi.ingsw.network.DTO;

import java.io.Serial;
import java.io.Serializable;

/**
 * Data Transfer Object representing an Offer Tile in the game.
 * <p>
 * Offer tiles provide bonuses (food or card draws) when chosen by a player.
 * This DTO tracks the tile's rewards and whether it is currently occupied by a player.
 */
public class OfferTileDTO implements Serializable {

    /**
     * Unique identifier for serialization interoperability.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    private int index;
    private int foodBonus;
    private int topRowDraws;
    private int bottomRowDraws;
    private String nickname;

    /**
     * Default constructor for serialization frameworks.
     */
    protected OfferTileDTO() {
    }

    /**
     * Constructs an OfferTileDTO with the specified state.
     *
     * @param index             The position index of the tile on the board.
     * @param foodBonus         The amount of food awarded by this tile.
     * @param topRowDraws       Number of cards the player can draw from the top row.
     * @param bottomRowDraws    Number of cards the player can draw from the bottom row.
     * @param occupyingNickname The nickname of the player occupying this tile, or null if empty.
     */
    public OfferTileDTO(int index, int foodBonus, int topRowDraws, int bottomRowDraws, String occupyingNickname) {
        this.index = index;
        this.foodBonus = foodBonus;
        this.topRowDraws = topRowDraws;
        this.bottomRowDraws = bottomRowDraws;
        this.nickname = occupyingNickname;
    }

    /** @return The tile's index on the board. */
    public int getIndex() { return index; }

    /** @return The food bonus provided by the tile. */
    public int getFoodBonus() { return foodBonus; }

    /** @return The number of card draws from the top row. */
    public int getTopRowDraws() { return topRowDraws; }

    /** @return The number of card draws from the bottom row. */
    public int getBottomRowDraws() { return bottomRowDraws; }

    /** @return The nickname of the player on this tile, or null if it is free. */
    public String getNickname() { return nickname; }

    /**
     * Checks if the tile is available for a player to occupy.
     *
     * @return true if no player is currently on this tile.
     */
    public boolean isAvailable() {
        return nickname == null;
    }
}