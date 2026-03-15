package it.polimi.ingsw.CharacterCardsPackage;
import it.polimi.ingsw.Cards.CharacterCard;
import it.polimi.ingsw.Enum.CharacterType;

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
