package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Interfaces.TribeDeck;

public class Hunter extends Card implements TribeDeck {
    private final boolean foodIcon;

    public Hunter(int era, int minPlayer, boolean isObtainable, boolean foodIcon) {
        super(era, minPlayer, isObtainable);
        this.foodIcon = foodIcon;
    }

    public boolean hasFoodIcon(){
        return this.foodIcon;
    }
}
