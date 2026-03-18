package it.polimi.ingsw.model.BuildingCards;

import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Enum.CharacterType;
import it.polimi.ingsw.model.Interfaces.CharacterTypeCount;
import org.jetbrains.annotations.Nullable;

public class ScoringBuilding extends BuildingCard {
    private final int fixedPoints;
    private final int multiplier;
    private final int pointsPerUnit;
    private final int setDim;

    @Nullable
    private final CharacterTypeCount countType;

    public ScoringBuilding(int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints, boolean isFinalBuilding,
                           int fixedPoints, int multiplier, int pointsPerUnit, int setDim, @Nullable CharacterTypeCount countType) {
        super(era, minPlayer, isObtainable, foodCost, prestigePoints, isFinalBuilding);
        this.fixedPoints = fixedPoints;
        this.multiplier = multiplier;
        this.pointsPerUnit = pointsPerUnit;
        this.setDim = setDim;
        this.countType = countType;
    }

    public int getTotalPoints(Tribe t) {
        int i = 0;
        // Edificio che da 25 punti: Fixed Points = 25
        i += fixedPoints;

        // Edificio che raddoppia i punti delle carte dei builder
        i += multiplier * t.getTotalBuildersPoints();

        // Edificio che da tot punti per set di tot carte
        i += t.getSetNumOfDifferentCard(setDim) * pointsPerUnit;

        if (countType != null) {
            // Edificio che da tot punti in base al numero di carte di quel tipo
            i += pointsPerUnit * countType.cardNumber(t);
        }

        return i;
    }
}
