package it.polimi.ingsw.model.factories;

public class RawBuildingCardData {
    public String id;
    public String subtype;
    public int era;
    public int minPlayers;

    public Integer foodCost;
    public Integer prestigePoints;

    // HuntBuilding / CavePaintingBuilding
    public Integer extraFood;
    public Integer extraPoints;

    // ScoringBuilding / CardAddedBuilding
    public Integer fixedPoints;
    public Integer multiplier;
    public Integer pointsPerUnit;
    public Integer setDim;

    // tipo conteggio
    public String countType;
    public String inventionIcon;

    // InstantEffectBuilding
    public Integer extraStars;
    public Boolean preventLoss;
    public Boolean doubleOnWinning;
    public Boolean extraCardFromUpper;
    public Boolean extraFoodFromBonus;

    // CardAddedBuilding
    public Boolean bonusOnDuplicateInventor;
    public Boolean bonusOnSetCharacters;
    public Integer foodBonus;
}