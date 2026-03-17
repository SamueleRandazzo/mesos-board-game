package it.polimi.ingsw.model.BuildingEffects;

import it.polimi.ingsw.model.Enum.BuildingType;
import it.polimi.ingsw.model.Enum.CharacterType;
import it.polimi.ingsw.model.Interfaces.*;
import it.polimi.ingsw.model.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScoringEffect implements BuildingEffect {
    private int fixedPoints;
    private int multiplier;
    private int pointsPerUnit;

    @Nullable
    private CharacterType characterType;

    public ScoringEffect(int fixedPoints, int multiplier, @Nullable CharacterType characterType, int pointsPerUnit) {
        this.fixedPoints = fixedPoints;
        this.multiplier = multiplier;
        this.characterType = characterType;
        this.pointsPerUnit = pointsPerUnit;
    }

    public void applyEffect(Player p, List<BuildingType> l) {
        if (!l.contains(BuildingType.SCORING))
            return;

        int total = getTotalPoints(p);
        p.changePrestigePoints(total);
    }

    // nota: fare test potente
    private int getTotalPoints(Player p) {
        int i = 0;
        i += fixedPoints * multiplier;

        if (characterType != null) {
            i += pointsPerUnit * p.getTribe().numberOf(characterType);
            i += multiplier * p.getTribe().getTotalPrestigePointsByType(characterType);
        }

        return i;
    }
}
