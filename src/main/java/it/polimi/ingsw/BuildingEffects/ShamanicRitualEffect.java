package it.polimi.ingsw.BuildingEffects;

import it.polimi.ingsw.Interfaces.BuildingEffect;

public class ShamanicRitualEffect implements BuildingEffect {
    private final int extraStars;
    private final boolean preventLoss;
    private final boolean doubleOnWinning;

    ShamanicRitualEffect(int extraStars, boolean preventLoss, boolean doubleOnWinning) {
        this.extraStars = extraStars;
        this.preventLoss = preventLoss;
        this.doubleOnWinning = doubleOnWinning;
    }

    // Return extra stars
    public int getExtraStars() {
        return this.extraStars;
    }

    // Return double points on winning flag
    public boolean isDoubleOnWinning() {
        return this.doubleOnWinning;
    }

    // Return prevent loss flag
    public boolean isPreventLoss() {
        return preventLoss;
    }

    // Apply building effect
    public void applyEffect() {

    }
}
