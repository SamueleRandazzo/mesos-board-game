package it.polimi.ingsw.model.factories;

/**
 * A lightweight Data Transfer Object (DTO) used to map flat JSON records of event cards.
 * <p>
 * This class serves as an intermediate data structure during the game initialization phase.
 * It holds template values for both mid-game events and game-ending final events.
 * Depending on the {@link #effectType}, specific subsets of fields will be populated,
 * leaving the rest as {@code null}.
 * </p>
 * <p>
 * The {@link CardFactory} consumes instances of this class to instantiate the corresponding
 * concrete domain-model event entities with their respective scoring or penalty parameters.
 * </p>
 *
 * @see CardFactory
 * @see GameDataLoader
 */
public class RawEventCardData {

    /** The unique string identifier of the event card. */
    public String id;

    /** The historical game era (1, 2, or 3) during which this event can occur. */
    public int era;

    /** The minimum number of players required in the session for this card to be included in the deck. */
    public int minPlayers;

    /** Flags whether this is a standard mid-game event or an endgame-triggering final event. */
    public boolean isFinal;

    /** The core strategy category of the event effect (e.g., "HUNT", "SUSTENANCE", "SHAMANIC_RITUAL", "CAVE_PAINTINGS"). */
    public String effectType;

    // --- Hunt Event Fields ---

    /** The amount of victory/prestige points awarded for each Hunter card owned by the player. Can be null. */
    public Integer prestigePerHunter;

    // --- Sustenance Event Fields ---

    /** The prestige point penalty deducted for each tribe member left unfed during the sustenance phase. Can be null. */
    public Integer prestigeLossPerUnfed;

    // --- Shamanic Ritual Event Fields ---

    /** The prestige points granted to the player(s) holding the majority status on the shamanic track. Can be null. */
    public Integer majorityPrestigeGain;

    /** The prestige points deducted from the player(s) falling into the minority status on the shamanic track. Can be null. */
    public Integer minorityPrestigeLoss;

    // --- Cave Paintings Event Fields ---

    /** The minimum threshold of Artist cards a player must own to avoid penalization. Can be null. */
    public Integer minArtists;

    /** The prestige point penalty inflicted if the player's Artist count falls below {@link #minArtists}. Can be null. */
    public Integer prestigeLossIfBelow;

    /** The prestige points awarded per Artist card if the player's count is equal to or greater than {@link #minArtists}. Can be null. */
    public Integer prestigePerArtistIfAbove;
}