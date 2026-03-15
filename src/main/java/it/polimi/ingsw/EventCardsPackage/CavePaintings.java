package it.polimi.ingsw.EventCardsPackage;
import it.polimi.ingsw.Cards.*;
import it.polimi.ingsw.Enum.CharacterType;
import it.polimi.ingsw.Player;

public class CavePaintings extends EventCard {
    public CavePaintings(int era, int minPlayer, boolean isObtainable, boolean isFinal){
        super(era, minPlayer, isObtainable, isFinal);
    }

    @Override
    public void eventHandler(Player player) {
        int numberOfArtists=player.getTribe().numberOf(CharacterType.ARTIST);
        if(numberOfArtists<getEra())
            player.changePrestigePoints(-2);
        else
            player.changePrestigePoints(getEra() * numberOfArtists);
    }
}
