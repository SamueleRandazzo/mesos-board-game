package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Interfaces.TribeDeck;

public class Artist extends Card implements TribeDeck {
    public Artist(int era, int minPlayer, boolean isObtainable) {
        super(era, minPlayer, isObtainable);
    }
}
