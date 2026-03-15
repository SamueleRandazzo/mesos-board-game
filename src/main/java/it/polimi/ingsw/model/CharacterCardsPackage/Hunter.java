package it.polimi.ingsw.model.CharacterCardsPackage;
import it.polimi.ingsw.model.Cards.CharacterCard;
import it.polimi.ingsw.model.Enum.CharacterType;

public class Hunter extends CharacterCard {
    private final boolean foodIcon;
    public Hunter(int era, int minPlayer, boolean isObtainable, boolean foodIcon) {
        super(era, minPlayer, isObtainable, CharacterType.HUNTER);
        this.foodIcon = foodIcon;
    }
    public boolean hasFoodIcon(){
        return this.foodIcon;
    }
}
