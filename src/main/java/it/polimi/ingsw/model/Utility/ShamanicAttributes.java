package it.polimi.ingsw.model.Utility;

import it.polimi.ingsw.model.BuildingCards.InstantEffectBuilding;

public class ShamanicAttributes {
    private int stars;
    private boolean preventLoss;
    private boolean doubleOnWinning;

    public ShamanicAttributes() {
        stars = 0;
        preventLoss = false;
        doubleOnWinning = false;
    }

    public int getStars() {
        return stars;
    }

    public boolean isPreventLoss() {
        return preventLoss;
    }

    public boolean isDoubleOnWinning() {
        return doubleOnWinning;
    }

    public void addStars(int stars) {
        this.stars += stars;
    }

    public void setPreventLoss(boolean preventLoss) {
        this.preventLoss = preventLoss;
    }

    public void setDoubleOnWinning(boolean doubleOnWinning) {
        this.doubleOnWinning = doubleOnWinning;
    }

    public void setParamByBuilding(InstantEffectBuilding card) {
        addStars(card.getExtraStars());
        setPreventLoss(this.preventLoss || card.isPreventLoss());
        setDoubleOnWinning(this.doubleOnWinning || card.isDoubleOnWinning());
    }
}
