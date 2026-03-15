package it.polimi.ingsw.Cards;
import it.polimi.ingsw.Enum.CharacterType;
import it.polimi.ingsw.Enum.InventionIcon;

public class Inventor extends CharacterCard{
    private final InventionIcon inventionIcon;
    public Inventor(int era, int minPlayer, boolean isObtainable, InventionIcon inventionIcon){
        super(era, minPlayer, isObtainable, CharacterType.INVENTOR);
        this.inventionIcon = inventionIcon;
    }
    public InventionIcon getInventionIcon(){
        return this.inventionIcon;
    }
}
