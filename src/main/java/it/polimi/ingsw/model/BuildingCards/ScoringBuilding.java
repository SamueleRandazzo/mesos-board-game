package it.polimi.ingsw.model.BuildingCards;

import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Enum.CharacterType;
import org.jetbrains.annotations.Nullable;

public class ScoringBuilding extends BuildingCard {
    private final int fixedPoints;
    private final int multiplier;
    private final int pointsPerUnit;
    private final int setDim;

    @Nullable
    private final CharacterType characterType;

    public ScoringBuilding(int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints, boolean isFinalBuilding,
                           int fixedPoints, int multiplier, @Nullable CharacterType characterType, int pointsPerUnit, int setDim) {
        super(era, minPlayer, isObtainable, foodCost, prestigePoints, isFinalBuilding);
        this.fixedPoints = fixedPoints;
        this.multiplier = multiplier;
        this.characterType = characterType;
        this.pointsPerUnit = pointsPerUnit;
        this.setDim = setDim;
    }

    public int getTotalPoints(Tribe t) {
        int i = 0;
        // Edificio che da 25 punti: Fixed Points = 25, Multiplier = 1
        i += fixedPoints * multiplier;

        if (characterType != null) {
            // Edificio che da tot punti in base al numero di carte di quel tipo
            i += pointsPerUnit * t.numberOf(characterType);

            // Edificio che raddoppia i punti delle carte di un determinato tipo (Multiplier = 2)
            i += multiplier * t.getTotalPrestigePointsByType(characterType);
        }

        // Edificio che da tot punti per set di tot carte
        i += t.getSetNumOfDifferentCard(setDim) * pointsPerUnit;

        return i;
    }
}
