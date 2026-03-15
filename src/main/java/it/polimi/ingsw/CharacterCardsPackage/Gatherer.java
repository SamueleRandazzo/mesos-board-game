package it.polimi.ingsw.CharacterCardsPackage;
import it.polimi.ingsw.Cards.CharacterCard;
import it.polimi.ingsw.Enum.CharacterType;

public class Gatherer extends CharacterCard {
    public Gatherer(int era, int minPlayer, boolean isObtainable){
        super(era, minPlayer, isObtainable, CharacterType.GATHERER);
    }
    //
}
