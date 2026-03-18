package it.polimi.ingsw.model.Cards;

public abstract class Card {
    private final int era;
    private final int minPlayer;
    private final boolean isObtainable;

    protected Card(int era, int minPlayer, boolean isObtainable) {
        this.era = era;
        this.minPlayer = minPlayer;
        this.isObtainable = isObtainable;
    }

    // Return era
    public int getEra() {
        return this.era;
    }

    // Return min player required
    public int getMinPlayer() {
        return this.minPlayer;
    }

    // Return obtainable
    public boolean getIsObtainable() {
        return this.isObtainable;
    }
}
