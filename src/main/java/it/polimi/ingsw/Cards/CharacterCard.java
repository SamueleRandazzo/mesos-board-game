package it.polimi.ingsw.Cards;
import it.polimi.ingsw.Enum.CharacterType;

abstract public class CharacterCard extends Card{
    private final CharacterType type;
    public CharacterCard(int era, int minPlayer, boolean isObtainable, CharacterType type) {
        super(era, minPlayer, isObtainable);
        this.type = type;
    }
    public CharacterType getType() {
        return type;
    }
}
