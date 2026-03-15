package it.polimi.ingsw.model.CharacterCardsPackage;
import it.polimi.ingsw.model.Cards.CharacterCard;
import it.polimi.ingsw.model.Enum.CharacterType;

public class Builder extends CharacterCard {
    private final int foodDiscount;
    private final int prestigePoints;
    public Builder(int era, int minPlayer, boolean isObtainable, int foodDiscount, int prestigePoints) {
        super(era, minPlayer, isObtainable, CharacterType.BUILDER);
        this.foodDiscount = foodDiscount;
        this.prestigePoints = prestigePoints;
    }
    public int getFoodDiscount(){
        return foodDiscount;
    }
    public int getPrestigePoints(){
        return prestigePoints;
    }
}
