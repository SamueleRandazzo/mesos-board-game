package it.polimi.ingsw.model.Cards;

import it.polimi.ingsw.model.Player;

/**
 * Abstract base class for all cards in the game.
 * It defines the common attributes and behaviors shared by every card (Characters, Buildings, Events).
 */
public abstract class Card {
    private final int era;
    private final int minPlayer;
    private final boolean isObtainable;

    /**
     * Constructs a new Card.
     *
     * @param era          the era of the card (must be strictly 1, 2, or 3)
     * @param minPlayer    the minimum number of players required for this card (must be between 2 and 5)
     * @param isObtainable true if the card can be obtained by a player (e.g., Characters, Buildings),
     * false otherwise (e.g., Events)
     * @throws IllegalArgumentException if the era is not 1, 2, or 3, or if minPlayer is not between 2 and 5
     */
    protected Card(int era, int minPlayer, boolean isObtainable) {
        // MESOS RULE: Eras are strictly 1, 2, or 3
        if (era < 1 || era > 3) {
            throw new IllegalArgumentException("Era must be 1, 2, or 3.");
        }

        // MESOS RULE: The game is for 2 to 5 players
        if (minPlayer < 2 || minPlayer > 5) {
            throw new IllegalArgumentException("Minimum number of players must be between 2 and 5.");
        }

        this.era = era;
        this.minPlayer = minPlayer;
        this.isObtainable = isObtainable;
    }

    /**
     * Returns the era of the card.
     * * @return the era (1, 2, or 3)
     */
    public int getEra() {
        return this.era;
    }

    /**
     * Returns the minimum number of players required to include this card in the game.
     * * @return the minimum player required (2 to 5)
     */
    public int getMinPlayer() {
        return this.minPlayer;
    }

    /**
     * Returns whether this card can be physically taken and kept by a player.
     * * @return true if obtainable, false otherwise
     */
    public boolean getIsObtainable() {
        return this.isObtainable;
    }

    /**
     * Applies the card's effect or adds it to the specified player's tribe.
     * This method must be implemented by concrete card classes.
     *
     * @param player the player to whom the card will be applied
     */
    public abstract void applyTo(Player player);
}