package it.polimi.ingsw.model.BuildingCards;

import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Interfaces.CharacterTypeCount;

public class HuntBuilding extends BuildingCard {
    private final int extraFood;
    private final int extraPoints;
    private final CharacterTypeCount typeCount;

    public HuntBuilding(int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints,
                        int extraFood, int extraPoints, CharacterTypeCount typeCount) {
        super(era, minPlayer, isObtainable, foodCost, prestigePoints);
        this.extraFood = extraFood;
        this.extraPoints = extraPoints;
        this.typeCount = typeCount;
    }

    public int getBonusFood(Tribe t) {
        return extraFood * typeCount.cardNumber(t);
    }

    public int getExtraPoints(Tribe t) {
        return extraPoints * typeCount.cardNumber(t);
    }

    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}
