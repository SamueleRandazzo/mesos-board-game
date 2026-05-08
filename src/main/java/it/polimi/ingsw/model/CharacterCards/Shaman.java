package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;

public class Shaman extends Card implements TribeDeck {
    private final int shamanStars;

    public Shaman(String id, int era, int minPlayer, boolean isObtainable, int shamanStars){
        super(id, era, minPlayer, isObtainable);
        this.shamanStars = shamanStars;
    }

    public int getShamanStars(){
        return this.shamanStars;
    }

    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}
