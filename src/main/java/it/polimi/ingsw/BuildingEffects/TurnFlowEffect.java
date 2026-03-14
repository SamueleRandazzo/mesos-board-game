package it.polimi.ingsw.BuildingEffects;

import it.polimi.ingsw.Interfaces.BuildingEffect;

public class TurnFlowEffect implements BuildingEffect {
    private final boolean extraCardFromUpper;
    private final boolean extraFoodFromBonus;

    TurnFlowEffect(boolean extraCardFromUpper, boolean extraFoodFromBonus) {
        this.extraCardFromUpper = extraCardFromUpper;
        this.extraFoodFromBonus = extraFoodFromBonus;
    }

    // Return extra card from upper flag
    public boolean isExtraCardFromUpper() {
        return extraCardFromUpper;
    }

    // Return is extra food from bonus flag
    public boolean isExtraFoodFromBonus() {
        return this.extraFoodFromBonus;
    }

    // Apply building effect
    public void applyEffect() {

    }
}