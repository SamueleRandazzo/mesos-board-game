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

    @Nullable
    private CharacterType characterType;

    public ScoringEffect(int fixedPoints, int multiplier, @Nullable CharacterType characterType) {
        this.fixedPoints = fixedPoints;
        this.multiplier = multiplier;
        this.characterType = characterType;
    }

    public void applyEffect(Player p, List<BuildingType> l) {
        if (!l.contains(BuildingType.SCORING))
            return;

        p.changePrestigePoints(fixedPoints);
    }
}
