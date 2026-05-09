package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;

public class Artist extends Card implements TribeDeck {
    public Artist(String id, int era, int minPlayer, boolean isObtainable) {
        super(id, era, minPlayer, isObtainable);
    }

    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}
