package it.polimi.ingsw.Cards;
import it.polimi.ingsw.Enum.CharacterType;

public class Hunter extends CharacterCard{
    private final boolean foodIcon;
    public Hunter(int era, int minPlayer, boolean isObtainable, boolean foodIcon) {
        super(era, minPlayer, isObtainable, CharacterType.HUNTER);
        this.foodIcon = foodIcon;
    }
    public boolean hasFoodIcon(){
        return this.foodIcon;
    }
}
