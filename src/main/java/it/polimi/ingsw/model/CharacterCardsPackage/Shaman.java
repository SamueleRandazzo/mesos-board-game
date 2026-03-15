package it.polimi.ingsw.model.CharacterCardsPackage;
import it.polimi.ingsw.model.Cards.CharacterCard;
import it.polimi.ingsw.model.Enum.CharacterType;

public class Shaman extends CharacterCard {
    private final int shamanStars;
    public Shaman(int era, int minPlayer, boolean isObtainable, int shamanStars){
        super(era, minPlayer, isObtainable, CharacterType.SHAMAN);
        this.shamanStars = shamanStars;
    }

    @Override
    public int getShamanStars(){
        return this.shamanStars;
    }
}
