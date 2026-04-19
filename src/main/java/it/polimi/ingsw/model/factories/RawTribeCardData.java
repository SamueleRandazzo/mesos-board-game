package it.polimi.ingsw.model.factories;

public class RawTribeCardData {
    public String id;
    public String subtype;
    public int era;
    public int minPlayers;

    // these are optional depending on the card type
    public String inventionIcon;
    public Integer shamanStars;
    public Integer immediateFood;
    public Integer buildingDiscount;
    public Integer sustenanceDiscount;
    public Integer prestigePoints;

}