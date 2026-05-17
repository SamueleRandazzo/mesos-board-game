package it.polimi.ingsw.persistence;

import java.util.ArrayList;
import java.util.List;

public class BoardSnapshot {
    public List<String> upperTribeCards = new ArrayList<>();
    public List<String> lowerTribeCards = new ArrayList<>();
    public List<String> upperBuildingCards = new ArrayList<>();
    public List<String> lowerBuildingCards = new ArrayList<>();
    public List<String> tribeDeck = new ArrayList<>();
    public List<String> buildingDeck = new ArrayList<>();
}
