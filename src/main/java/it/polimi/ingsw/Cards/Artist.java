package it.polimi.ingsw.Cards;
import it.polimi.ingsw.Enum.CharacterType;

public class Artist extends CharacterCard{
    public Artist(int era, int minPlayer, boolean isObtainable) {
        super(era, minPlayer, isObtainable, CharacterType.ARTIST);
    }
}
