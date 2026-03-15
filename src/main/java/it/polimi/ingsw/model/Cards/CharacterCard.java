package it.polimi.ingsw.model.Cards;
import it.polimi.ingsw.model.Enum.CharacterType;

abstract public class CharacterCard extends Card{
    private final CharacterType type;
    public CharacterCard(int era, int minPlayer, boolean isObtainable, CharacterType type) {
        super(era, minPlayer, isObtainable);
        this.type = type;
    }
    public CharacterType getType() {
        return type;
    }

    public int getShamanStars(){
        return 0;
    }
}
