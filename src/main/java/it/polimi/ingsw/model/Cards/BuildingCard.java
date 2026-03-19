package it.polimi.ingsw.model.Cards;

import it.polimi.ingsw.model.Enum.BuildingType;
import it.polimi.ingsw.model.Interfaces.BuildingEffect;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;

public class BuildingCard extends Card {
    private final int foodCost;
    private final int prestigePoints;
    private final BuildingEffect buildingEffect;
    private final boolean isFinalBuilding;

    BuildingCard(int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints, boolean isFinalBuilding, BuildingEffect buildingEffect) {
        super(era, minPlayer, isObtainable);
        this.foodCost = foodCost;
        this.prestigePoints = prestigePoints;
        this.isFinalBuilding = isFinalBuilding;
        this.buildingEffect = buildingEffect;
    }

    /** Return building food cost */
    public int getFoodCost() {
        return this.foodCost;
    }

    /** Return building prestige points */
    public int getPrestigePoints() {
        return  this.prestigePoints;
    }

    /** Return final building flag */
    public boolean isFinalBuilding() {
        return isFinalBuilding;
    }

    /** Activate all effect */
    public void activateAllEffect(Player p, BuildingType l) {
        buildingEffect.applyEffect(p, l);
    }
}
