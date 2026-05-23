package it.polimi.ingsw.model.factories;

/**
 * A lightweight Data Transfer Object (DTO) used to map flat JSON records of tribe character cards.
 * <p>
 * This class serves as an intermediate data structure during the game setup phase.
 * Since a single JSON definition file aggregates multiple distinct character professions
 * (such as Hunters, Gatherers, Shamans, or Inventors), fields representing specialized
 * attributes use wrapper classes ({@link Integer}) and will be {@code null} if they do not
 * apply to the card's specific subtype.
 * </p>
 * <p>
 * The {@link CardFactory} parses instances of this class to instantiate the concrete
 * character domain models with their respective gameplay bonuses or icons.
 * </p>
 *
 * @see CardFactory
 * @see GameDataLoader
 */
public class RawTribeCardData {

    /** The unique string identifier of the tribe card. */
    public String id;

    /** The specific profession or role of the character (e.g., "HUNTER", "GATHERER", "SHAMAN", "INVENTOR"). */
    public String subtype;

    /** The historical game era (1, 2, or 3) during which this character becomes available. */
    public int era;

    /** The minimum number of players required in the session for this card to be included in the deck. */
    public int minPlayers;

    // --- Optional fields depending on the character subtype ---

    /**
     * The specific technological icon printed on the card.
     * Primarily populated for {@code Inventor} cards. Can be null.
     */
    public String inventionIcon;

    /**
     * The number of spiritual star points awarded to the player's track upon recruitment.
     * Primarily populated for {@code Shaman} cards. Can be null.
     */
    public Integer shamanStars;

    /**
     * The amount of immediate food resources granted when this character enters the tribe.
     * Primarily populated for certain {@code Hunter} cards. Can be null.
     */
    public Integer immediateFood;

    /**
     * A resource discount applied when constructing buildings.
     * Populated if the character grants a structural discount perk. Can be null.
     */
    public Integer buildingDiscount;

    /**
     * Any immediate or baseline victory/prestige points granted by the card.
     * Can be null.
     */
    public Integer prestigePoints;
}