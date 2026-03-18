package it.polimi.ingsw.model.BuildingCards;

import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Interfaces.CharacterTypeCount;
import it.polimi.ingsw.model.Player;

public class CavePaintingBuilding extends BuildingCard {
    private final int extraFood;
    private final CharacterTypeCount typeCount;

    public CavePaintingBuilding(int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints, boolean isFinalBuilding,
                                int extraFood, CharacterTypeCount typeCount) {
        super(era, minPlayer, isObtainable, foodCost, prestigePoints, isFinalBuilding);
        this.extraFood = extraFood;
        this.typeCount = typeCount;
    }

    public int getBonusFood(Tribe t) {
        return extraFood * typeCount.cardNumber(t);
    }
}
