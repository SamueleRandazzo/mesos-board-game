package it.polimi.ingsw.model.Cards;

import it.polimi.ingsw.model.Enum.CharacterType;
import it.polimi.ingsw.model.Enum.InventionIcon;

/**
 * Abstract class representing a character card in the game.
 * Each character card belongs to a specific {@link CharacterType} and may
 * provide different bonuses depending on its subtype.
 *
 * Each public constructor is restricted to a specific character subtype.
 * If the provided {@link CharacterType} does not match the expected one,
 * an {@link IllegalArgumentException} is thrown.
 */
public abstract class CharacterCard extends Card {

    private final CharacterType type;
    private final int foodDiscount;
    private final int prestigePoints;
    private final boolean foodIcon;
    private final InventionIcon inventionIcon;
    private final int shamanStars;

    /**
     * Internal full constructor used by all other constructors.
     */
    private CharacterCard(int era, int minPlayer, boolean isObtainable,
                          CharacterType type, int foodDiscount, int prestigePoints,
                          boolean foodIcon, InventionIcon inventionIcon, int shamanStars) {

        super(era, minPlayer, isObtainable);

        if (type == null)
            throw new IllegalArgumentException("CharacterType cannot be null");
        if (foodDiscount < 0)
            throw new IllegalArgumentException("Food discount cannot be negative");
        if (prestigePoints < 0)
            throw new IllegalArgumentException("Prestige points cannot be negative");
        if (shamanStars < 0)
            throw new IllegalArgumentException("Shaman stars cannot be negative");

        this.type = type;
        this.foodDiscount = foodDiscount;
        this.prestigePoints = prestigePoints;
        this.foodIcon = foodIcon;
        this.inventionIcon = inventionIcon;
        this.shamanStars = shamanStars;
    }


    /**
     * Constructor for Artist and Gatherer cards.
     *
     * @throws IllegalArgumentException if type is not ARTIST or GATHERER
     */
    public CharacterCard(int era, int minPlayer, boolean isObtainable, CharacterType type) {
        super(era, minPlayer, isObtainable);

        if (type != CharacterType.ARTIST && type != CharacterType.GATHERER)
            throw new IllegalArgumentException("This constructor is only for ARTIST or GATHERER");

        this.type = type;
        this.foodDiscount = 0;
        this.prestigePoints = 0;
        this.foodIcon = false;
        this.inventionIcon = null;
        this.shamanStars = 0;
    }


    /**
     * Constructor for Builder cards.
     *
     * @throws IllegalArgumentException if type is not BUILDER
     */
    public CharacterCard(int era, int minPlayer, boolean isObtainable,
                         CharacterType type, int foodDiscount, int prestigePoints) {

        this(era, minPlayer, isObtainable, type, foodDiscount, prestigePoints, false, null, 0);

        if (type != CharacterType.BUILDER)
            throw new IllegalArgumentException("This constructor is only for BUILDER");
    }


    /**
     * Constructor for Hunter cards.
     *
     * @throws IllegalArgumentException if type is not HUNTER
     */
    public CharacterCard(int era, int minPlayer, boolean isObtainable,
                         CharacterType type, boolean foodIcon) {

        this(era, minPlayer, isObtainable, type, 0, 0, foodIcon, null, 0);

        if (type != CharacterType.HUNTER)
            throw new IllegalArgumentException("This constructor is only for HUNTER");
    }


    /**
     * Constructor for Inventor cards.
     *
     * @throws IllegalArgumentException if type is not INVENTOR or inventionIcon is null
     */
    public CharacterCard(int era, int minPlayer, boolean isObtainable,
                         CharacterType type, InventionIcon inventionIcon) {

        this(era, minPlayer, isObtainable, type, 0, 0, false, inventionIcon, 0);

        if (type != CharacterType.INVENTOR)
            throw new IllegalArgumentException("This constructor is only for INVENTOR");

        if (inventionIcon == null)
            throw new IllegalArgumentException("InventionIcon cannot be null for INVENTOR");
    }


    /**
     * Constructor for Shaman cards.
     *
     * @throws IllegalArgumentException if type is not SHAMAN
     */
    public CharacterCard(int era, int minPlayer, boolean isObtainable,
                         CharacterType type, int shamanStars) {

        this(era, minPlayer, isObtainable, type, 0, 0, false, null, shamanStars);

        if (type != CharacterType.SHAMAN)
            throw new IllegalArgumentException("This constructor is only for SHAMAN");
    }


    // ------------------------------------------------------------
    // GETTERS
    // ------------------------------------------------------------

    public CharacterType getType() {
        return type;
    }

    public int getFoodDiscount() {
        return foodDiscount;
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }

    public boolean hasFoodIcon() {
        return this.foodIcon;
    }

    public InventionIcon getInventionIcon() {
        return inventionIcon;
    }

    public int getShamanStars() {
        return shamanStars;
    }
}
