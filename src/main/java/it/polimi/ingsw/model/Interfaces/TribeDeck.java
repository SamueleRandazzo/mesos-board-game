package it.polimi.ingsw.model.Interfaces;

import it.polimi.ingsw.model.Player;

/**
 * Marker interface for cards that can be placed in the tribe deck
 * and in the board's upper and lower tribe rows.
 *
 * Implemented by:
 *  - CharacterCard
 *  - EventCard
 */
public interface TribeDeck {
    public int getEra();
    public boolean getIsObtainable();
    public void applyTo(Player p);
}