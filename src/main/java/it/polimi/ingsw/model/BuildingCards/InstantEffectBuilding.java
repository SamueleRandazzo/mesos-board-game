package it.polimi.ingsw.model.BuildingCards;

import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Player;

public class InstantEffectBuilding extends BuildingCard {
    private final int extraStars;
    private final boolean preventLoss;
    private final boolean doubleOnWinning;
    private final boolean extraCardFromUpper;
    private final boolean extraFoodFromBonus;

    public InstantEffectBuilding(int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints,
                          int extraStars, boolean preventLoss, boolean doubleOnWinning, boolean extraCardFromUpper, boolean extraFoodFromBonus) {
        super(era, minPlayer, isObtainable, foodCost, prestigePoints);
        this.extraStars = extraStars;
        this.preventLoss = preventLoss;
        this.doubleOnWinning = doubleOnWinning;
        this.extraCardFromUpper = extraCardFromUpper;
        this.extraFoodFromBonus = extraFoodFromBonus;
    }

    public int getExtraStars() {
        return extraStars;
    }

    public boolean isPreventLoss() {
        return preventLoss;
    }

    public boolean isDoubleOnWinning() {
        return doubleOnWinning;
    }

    public boolean isExtraCardFromUpper() {
        return extraCardFromUpper;
    }

    public boolean isExtraFoodFromBonus() {
        return extraFoodFromBonus;
    }

    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}
