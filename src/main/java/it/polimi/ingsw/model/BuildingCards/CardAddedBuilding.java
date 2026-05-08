package it.polimi.ingsw.model.BuildingCards;

import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Player;

public class CardAddedBuilding extends BuildingCard {
    private final int foodBonus;
    private final boolean bonusOnDuplicateInventor;
    private final boolean bonusOnSetCharacters;
    private int initialSetCount;
    private int rewardedSetCount;
    private int setDim;

    public CardAddedBuilding(String id, int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints,
                             boolean bonusOnDuplicateInventor, boolean bonusOnSetCharacters, int foodBonus, int setDim) {
        super(id, era, minPlayer, isObtainable, foodCost, prestigePoints);
        this.bonusOnDuplicateInventor = bonusOnDuplicateInventor;
        this.bonusOnSetCharacters = bonusOnSetCharacters;
        this.foodBonus = foodBonus;
        this.setDim = setDim;
        initialSetCount = 0;
        rewardedSetCount = 0;
    }

    public boolean isBonusOnDuplicateInventor() {
        return bonusOnDuplicateInventor;
    }

    public boolean isBonusOnSetCharacters() {
        return bonusOnSetCharacters;
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

    public int getRewardedSetCount() {
        return rewardedSetCount;
    }

    public void setInitialSetCount(int n) {
        this.initialSetCount = n;
    }

    public void incrementRewardedSetCount() {
        rewardedSetCount++;
    }

    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}
