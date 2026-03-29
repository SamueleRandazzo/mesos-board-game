package it.polimi.ingsw.model.Cards;

import it.polimi.ingsw.model.Player;

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

    public abstract void applyTo(Player player);
}
