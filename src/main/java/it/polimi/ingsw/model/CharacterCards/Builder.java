package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;

public class Builder extends Card implements TribeDeck {
    private final int foodDiscount;
    private final int prestigePoints;

    public Builder(String id, int era, int minPlayer, boolean isObtainable, int foodDiscount, int prestigePoints) {
        super(id, era, minPlayer, isObtainable);
        this.foodDiscount = foodDiscount;
        this.prestigePoints = prestigePoints;
    }

    public int getFoodDiscount(){
        return foodDiscount;
    }

    public int getPrestigePoints(){
        return prestigePoints;
    }

    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}
