package it.polimi.ingsw.model.Cards;
import it.polimi.ingsw.model.Enum.CharacterType;

abstract public class CharacterCard extends Card{
    private final CharacterType type;
    private final int prestigePoints;
    private final int shamanStars;

    public CharacterCard(int era, int minPlayer, boolean isObtainable, CharacterType type, int prestigePoints, int shamanStars) {
        super(era, minPlayer, isObtainable);
        this.type = type;
        this.prestigePoints = prestigePoints;
        this.shamanStars = shamanStars;
    }

    public CharacterType getType() {
        return type;
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }

    public int getShamanStars() {
        return shamanStars;
    }
}
