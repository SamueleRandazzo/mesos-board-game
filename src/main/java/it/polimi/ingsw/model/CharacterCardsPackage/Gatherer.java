package it.polimi.ingsw.model.CharacterCardsPackage;
import it.polimi.ingsw.model.Cards.CharacterCard;
import it.polimi.ingsw.model.Enum.CharacterType;

public class Gatherer extends CharacterCard {
    public Gatherer(int era, int minPlayer, boolean isObtainable){
        super(era, minPlayer, isObtainable, CharacterType.GATHERER);
    }

}
