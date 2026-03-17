package it.polimi.ingsw.model.Board;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OfferTrack {

    private final List<OfferTile> tiles;

    /*Constructor: initializes the track with the correct number ot tiles,
                  param is the list of OfferTile for the current game */
    public OfferTrack(List<OfferTile> initialTiles){

        this.tiles = new ArrayList<>(initialTiles);
    }

    /*Identifies tiles that do not currently have a totem placed on them.
      returns a list of available OfferTiles*/
    public List<OfferTile> getAvailableOffers(){

        return tiles.stream()
                .filter(OfferTile::isAvailable).collect(Collectors.toList());
    }

    /*Returns a copy of the tiles on the track*/
    public List<OfferTile> getTiles(){
        return new ArrayList<>(this.tiles);
    }
}
