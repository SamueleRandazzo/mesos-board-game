package it.polimi.ingsw.model.CharacterCardsPackage;
import it.polimi.ingsw.model.Cards.CharacterCard;
import it.polimi.ingsw.model.Enum.CharacterType;
import it.polimi.ingsw.model.Enum.InventionIcon;

public class Inventor extends CharacterCard {
    private final InventionIcon inventionIcon;
    public Inventor(int era, int minPlayer, boolean isObtainable, InventionIcon inventionIcon){
        super(era, minPlayer, isObtainable, CharacterType.INVENTOR);
        this.inventionIcon = inventionIcon;
    }
    public InventionIcon getInventionIcon(){
        return this.inventionIcon;
    }
}
