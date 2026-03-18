package it.polimi.ingsw.model.BuildingEffects;

import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Enum.*;
import it.polimi.ingsw.model.Interfaces.BuildingEffect;
import it.polimi.ingsw.model.*;

import java.util.List;

public class CharacterModifierEffect implements BuildingEffect {
    private final int extraFood;
    private final int extraPoints;
    private final CharacterType characterType;
    private final EventType eventType;

    public CharacterModifierEffect(int extraFood, int extraPoints, CharacterType ct, EventType et) {
        this.extraFood = extraFood;
        this.extraPoints = extraPoints;
        this.characterType = ct;
        this.eventType = et;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void applyEffect(Player p, BuildingType l) {
        if (l != BuildingType.CHARACTER_MOD)
            return;

        p.changeFoodAmount(extraFood * p.getTribe().numberOf(characterType));
        p.changePrestigePoints(extraPoints * p.getTribe().numberOf(characterType));
    }
}
