package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;

public class Gatherer extends Card implements TribeDeck {
    public Gatherer(int era, int minPlayer, boolean isObtainable){
        super(era, minPlayer, isObtainable);
    }

    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}
