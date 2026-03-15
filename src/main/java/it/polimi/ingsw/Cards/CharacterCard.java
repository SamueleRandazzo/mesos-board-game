package it.polimi.ingsw.Cards;
import it.polimi.ingsw.Enum.CharacterType;

public class CharacterCard {
    private final CharacterType type;
    public CharacterCard(CharacterType type) {
        this.type = type;
    }
    public CharacterType getType() {
        return type;
    }
}
