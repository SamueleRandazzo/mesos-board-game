package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Interfaces.TribeDeck;

public class Builder extends Card implements TribeDeck {
    private final int foodDiscount;
    private final int prestigePoints;

    public Builder(int era, int minPlayer, boolean isObtainable, int foodDiscount, int prestigePoints) {
        super(era, minPlayer, isObtainable);
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
