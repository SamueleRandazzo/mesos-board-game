package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Interfaces.TribeDeck;

public class Binder extends Card implements TribeDeck {
    public Binder(int era, int minPlayer, boolean isObtainable){
        super(era, minPlayer, isObtainable);
    }
}
