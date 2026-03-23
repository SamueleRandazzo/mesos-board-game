package it.polimi.ingsw.model.BuildingCards;

import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Interfaces.CharacterTypeCount;
import org.jetbrains.annotations.Nullable;

public class ScoringBuilding extends BuildingCard {
    private final int fixedPoints;
    private final int multiplier;
    private final int pointsPerUnit;
    private final int setDim;

    @Nullable
    private final CharacterTypeCount countType;

    public ScoringBuilding(int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints,
                           int fixedPoints, int multiplier, int pointsPerUnit, int setDim, @Nullable CharacterTypeCount countType) {
        super(era, minPlayer, isObtainable, foodCost, prestigePoints);
        this.fixedPoints = fixedPoints;
        this.multiplier = multiplier;
        this.pointsPerUnit = pointsPerUnit;
        this.setDim = setDim;
        this.countType = countType;
    }

    public int getTotalPoints(Tribe t) {
        int i = 0;
        i += fixedPoints;

        i += multiplier * t.totalBuildersPoints();

        i += t.getSetNumOfDifferentCard(setDim) * pointsPerUnit;

        if (countType != null) {
            i += pointsPerUnit * countType.cardNumber(t);
        }

        return i;
    }

    @Override
    public void addToTribe(Tribe tribe) {
        tribe.addCard(this);
    }
}
