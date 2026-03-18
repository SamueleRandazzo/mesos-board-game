package it.polimi.ingsw.model.BuildingCards;

import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Interfaces.CharacterTypeCount;

public class SustenanceBuilding extends BuildingCard {
    private final int foodDiscountPerCard;
    private final CharacterTypeCount discountType;

    public SustenanceBuilding(int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints, boolean isFinalBuilding,
                              int foodDiscountPerCard, CharacterTypeCount discountType) {
        super(era, minPlayer, isObtainable, foodCost, prestigePoints, isFinalBuilding);
        this.foodDiscountPerCard = foodDiscountPerCard;
        this.discountType = discountType;
    }

    public int getDiscount(Tribe t) {
        return foodDiscountPerCard * discountType.cardNumber(t);
    }
}