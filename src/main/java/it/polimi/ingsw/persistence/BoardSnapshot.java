package it.polimi.ingsw.persistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Serializable snapshot of board rows and draw piles.
 */
public class BoardSnapshot {
    /** Card ids currently visible in the upper tribe row. */
    public List<String> upperTribeCards = new ArrayList<>();
    /** Card ids currently visible in the lower tribe row. */
    public List<String> lowerTribeCards = new ArrayList<>();
    /** Building card ids currently visible in the upper building row. */
    public List<String> upperBuildingCards = new ArrayList<>();
    /** Building card ids currently visible in the lower building row. */
    public List<String> lowerBuildingCards = new ArrayList<>();
    /** Remaining tribe deck card ids in draw order. */
    public List<String> tribeDeck = new ArrayList<>();
    /** Remaining current-era building deck card ids in draw order. */
    public List<String> buildingDeck = new ArrayList<>();
}
