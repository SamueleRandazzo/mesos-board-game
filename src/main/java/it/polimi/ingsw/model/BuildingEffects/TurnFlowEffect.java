package it.polimi.ingsw.model.BuildingEffects;

import it.polimi.ingsw.model.Cards.*;
import it.polimi.ingsw.model.Enum.BuildingType;
import it.polimi.ingsw.model.Interfaces.*;
import it.polimi.ingsw.model.*;

import java.util.List;

public class TurnFlowEffect implements BuildingEffect {
    private final boolean extraCardFromUpper;
    private final boolean extraFoodFromBonus;

    TurnFlowEffect(boolean extraCardFromUpper, boolean extraFoodFromBonus) {
        this.extraCardFromUpper = extraCardFromUpper;
        this.extraFoodFromBonus = extraFoodFromBonus;
    }

    /** Return extra card from upper flag */
    public boolean isExtraCardFromUpper() {
        return extraCardFromUpper;
    }

    /** Return is extra food from bonus flag */
    public boolean isExtraFoodFromBonus() {
        return extraFoodFromBonus;
    }

    /** Apply building effect */
    public void applyEffect(Player p, BuildingType l) {
        if (l != BuildingType.TURN_FLOW)
            return;
    }
}