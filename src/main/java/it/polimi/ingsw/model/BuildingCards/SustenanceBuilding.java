package it.polimi.ingsw.model.BuildingCards;

import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Interfaces.CharacterTypeCount;
import it.polimi.ingsw.model.Player;

public class SustenanceBuilding extends BuildingCard {
    private final int foodDiscountPerCard;
    private final CharacterTypeCount discountType;

    public SustenanceBuilding(String id, int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints,
                              int foodDiscountPerCard, CharacterTypeCount discountType) {
        super(id, era, minPlayer, isObtainable, foodCost, prestigePoints);
        this.foodDiscountPerCard = foodDiscountPerCard;
        this.discountType = discountType;
    }

    public int getDiscount(Tribe t) {
        return foodDiscountPerCard * discountType.cardNumber(t);
    }

    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}