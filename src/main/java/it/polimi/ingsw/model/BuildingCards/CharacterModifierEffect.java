package it.polimi.ingsw.model.BuildingCards;

import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Enum.*;
import it.polimi.ingsw.model.*;

public class CharacterModifierEffect extends BuildingCard {
    private final int extraFood;
    private final int extraPoints;
    private final CharacterType characterType;
    private final EventType eventType;

    public CharacterModifierEffect(int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints, boolean isFinalBuilding,
                                   int extraFood, int extraPoints, CharacterType ct, EventType et) {
        super(era, minPlayer, isObtainable, foodCost, prestigePoints, isFinalBuilding);
        this.extraFood = extraFood;
        this.extraPoints = extraPoints;
        this.characterType = ct;
        this.eventType = et;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void applyEffect(Player p) {
        p.changeFoodAmount(extraFood * p.getTribe().numberOf(characterType));
        p.changePrestigePoints(extraPoints * p.getTribe().numberOf(characterType));
    }
}
