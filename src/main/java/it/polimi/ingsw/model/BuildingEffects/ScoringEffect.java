package it.polimi.ingsw.model.BuildingEffects;

import it.polimi.ingsw.model.Enum.BuildingType;
import it.polimi.ingsw.model.Enum.CharacterType;
import it.polimi.ingsw.model.Interfaces.*;
import it.polimi.ingsw.model.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScoringEffect implements BuildingEffect {
    private final int fixedPoints;
    private final int multiplier;
    private final int pointsPerUnit;
    private final int setDim;

    @Nullable
    private final CharacterType characterType;

    public ScoringEffect(int fixedPoints, int multiplier, @Nullable CharacterType characterType, int pointsPerUnit, int setDim) {
        this.fixedPoints = fixedPoints;
        this.multiplier = multiplier;
        this.characterType = characterType;
        this.pointsPerUnit = pointsPerUnit;
        this.setDim = setDim;
    }

    public void applyEffect(Player p, BuildingType l) {
        if (l != BuildingType.SCORING)
            return;

        int total = getTotalPoints(p);
        p.changePrestigePoints(total);
    }

    private int getTotalPoints(Player p) {
        int i = 0;
        // Edificio che da 25 punti: Fixed Points = 25, Multiplier = 1
        i += fixedPoints * multiplier;

        if (characterType != null) {
            // Edificio che da tot punti in base al numero di carte di quel tipo
            i += pointsPerUnit * p.getTribe().numberOf(characterType);

            // Edificio che raddoppia i punti delle carte di un determinato tipo (Multiplier = 2)
            i += multiplier * p.getTribe().getTotalPrestigePointsByType(characterType);
        }

        // Edificio che da tot punti per set di tot carte
        i += p.getTribe().getSetNumOfDifferentCard(setDim) * pointsPerUnit;

        return i;
    }
}
