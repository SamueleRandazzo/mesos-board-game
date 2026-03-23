package it.polimi.ingsw.model.Board;


import it.polimi.ingsw.model.Enum.TileId;
import it.polimi.ingsw.model.Player;

public class OfferTile {

    private final int foodBonus;
    private final TileId tileId;
    private final int topRowDraws;
    private final int bottomRowDraws;
    private Player placedPlayer;

    /** Constructor for OfferTile */
    public OfferTile(int foodBonus, TileId tileId, int topRowDraws, int bottomRowDraws){
        this.foodBonus = foodBonus;
        this.tileId = tileId;
        this.topRowDraws = topRowDraws;
        this.bottomRowDraws = bottomRowDraws;
        this.placedPlayer = null;
    }

    /** returns the eventual food bonus of the tile*/
    public int getFoodBonus(){
        return this.foodBonus;
    }

    /** returns the number of cards that the player can draw from the top row.*/
    public int getTopRowDraws(){
        return this.topRowDraws;
    }
    /** returns the number of cards that the player can draw from the bottom row.*/
    public int getBottomRowDraws(){
        return this.bottomRowDraws;
    }

    /** returns the id of the Tile which is a letter*/
    public TileId getTileId(){
        return this.tileId;
    }
    /** checks if the tile is available (a tile is available if no totem placed on it)
       return true if available, false otherwise.
     */
    public boolean isAvailable(){
        return placedPlayer == null;
    }

    /** places a totem on this tile, param "totem" is the totem to place*/
    public void placeTotem(Player player){
        this.placedPlayer = player;

    }

    /** remove the totem from this tile at the end of the phase*/
    public void removeTotem(){

        this.placedPlayer = null;
    }
}
