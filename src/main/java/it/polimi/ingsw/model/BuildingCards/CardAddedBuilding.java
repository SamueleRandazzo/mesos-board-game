package it.polimi.ingsw.model.BuildingCards;

import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Cards.Tribe;
import java.util.*;

public class CardAddedBuilding extends BuildingCard {
    private final int foodBonus;
    private final boolean bonusOnDuplicateInventor;
    private final boolean bonusOnSet6Characters;
    private int initialSetCount;
    private int setDim;

    public CardAddedBuilding(int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints,
                             boolean bonusOnDuplicateInventor, boolean bonusOnSet6Characters, int foodBonus, int setDim) {
        super(era, minPlayer, isObtainable, foodCost, prestigePoints);
        this.bonusOnDuplicateInventor = bonusOnDuplicateInventor;
        this.bonusOnSet6Characters = bonusOnSet6Characters;
        this.foodBonus = foodBonus;
        this.setDim = setDim;
        initialSetCount = 0;
    }

    @Override
    public void addToTribe(Tribe tribe) {
        tribe.addCard(this);
    }

    public boolean isBonusOnDuplicateInventor() {
        return bonusOnDuplicateInventor;
    }

    public boolean isBonusOnSet6Characters() {
        return bonusOnSet6Characters;
    }

    public int getFoodBonus() {
        return foodBonus;
    }

    public int getInitialSetCount() {
        return initialSetCount;
    }

    public int getSetDim() {
        return setDim;
    }

    public void setInitialSetCount(int n) {
        this.initialSetCount = n;
    }
}
