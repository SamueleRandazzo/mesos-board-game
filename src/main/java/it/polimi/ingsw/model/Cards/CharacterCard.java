package it.polimi.ingsw.model.Cards;
import it.polimi.ingsw.model.Enum.*;

public class CharacterCard extends Card{
    private final CharacterType type;
    private final int foodDiscount;
    private final int prestigePoints;
    private final boolean foodIcon;
    private final InventionIcon inventionIcon;
    private final int shamanStars;

    private CharacterCard(int era, int minPlayer, boolean isObtainable, CharacterType type, int foodDiscount, int prestigePoints, boolean foodIcon, InventionIcon inventionIcon, int shamanStars) {
        super(era, minPlayer, isObtainable);
        this.type = type;
        this.foodDiscount = foodDiscount;
        this.prestigePoints = prestigePoints;
        this.foodIcon = foodIcon;
        this.inventionIcon = inventionIcon;
        this.shamanStars = shamanStars;
    }

    //region Specific
    //only for Artist and Gatherer
    public CharacterCard(int era, int minPlayer, boolean isObtainable, CharacterType type) {
        this(era, minPlayer, isObtainable, type, 0, 0, false, null, 0);
    }

    //only for Builder
    public CharacterCard(int era, int minPlayer, boolean isObtainable, CharacterType type, int foodDiscount, int prestigePoints) {
        this(era, minPlayer, isObtainable, type, foodDiscount, prestigePoints, false, null, 0);
    }

    //only for Hunter
    public CharacterCard(int era, int minPlayer, boolean isObtainable, CharacterType type, boolean foodIcon) {
        this(era, minPlayer, isObtainable, type, 0, 0, false, null, 0);
    }

    //only for Inventor
    public CharacterCard(int era, int minPlayer, boolean isObtainable, CharacterType type, InventionIcon inventionIcon) {
        this(era, minPlayer, isObtainable, type, 0, 0, false, inventionIcon, 0);
    }

    //only for Shaman
    public CharacterCard(int era, int minPlayer, boolean isObtainable, CharacterType type, int shamanStars) {
        this(era, minPlayer, isObtainable, type, 0, 0, false, null, shamanStars);
    }
    //endregion

    public CharacterType getType() {
        return type;
    }

    //Builder
    public int getFoodDiscount(){
        return foodDiscount;
    }

    public int getPrestigePoints(){
        return prestigePoints;
    }

    //Hunter
    public boolean hasFoodIcon(){
        return foodIcon;
    }

    //Inventor
    public InventionIcon getInventionIcon(){
        return inventionIcon;
    }

    //Shaman
    public int getShamanStars(){
        return shamanStars;
    }
}
