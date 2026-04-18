package it.polimi.ingsw.model.factories;

public class RawEventCardData {
    public String id;
    public int era;
    public int minPlayers;
    public boolean isFinal;
    public String effectType;

    // Hunt
    public Integer prestigePerHunter;

    // Sustenance
    public Integer prestigeLossPerUnfed;

    // Shamanic Ritual
    public Integer majorityPrestigeGain;
    public Integer minorityPrestigeLoss;

    // Cave Paintings
    public Integer minArtists;
    public Integer prestigeLossIfBelow;
    public Integer prestigePerArtistIfAbove;
}