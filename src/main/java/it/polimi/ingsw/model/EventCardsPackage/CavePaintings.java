package it.polimi.ingsw.model.EventCardsPackage;
import it.polimi.ingsw.Cards.*;
import it.polimi.ingsw.model.Cards.EventCard;
import it.polimi.ingsw.model.Enum.CharacterType;
import it.polimi.ingsw.model.Player;

public class CavePaintings extends EventCard {
    public CavePaintings(int era, int minPlayer, boolean isObtainable, boolean isFinal){
        super(era, minPlayer, isObtainable, isFinal);
    }


    @Override
    protected void eventHandler(Player player) {
        int numberOfArtists=player.getTribe().numberOf(CharacterType.ARTIST);
        if(numberOfArtists<getEra())
            player.changePrestigePoints(-2);
        else
            player.changePrestigePoints(getEra() * numberOfArtists);
    }
}

/*
Cave Painting Event card structure (1 for each era)
era 1
	0: -2
	1+: 1 * numberOfArtists

era 2
	0-1: -2
	2+: 2 * numberOfArtists

era 3
	0-2: -2
	3+: 3 * numberOfArtists

era X
    <X: -2
    X+: X * numberOfArtists
 */
