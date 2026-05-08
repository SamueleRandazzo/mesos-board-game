package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;

public class Hunter extends Card implements TribeDeck {
    private final boolean foodIcon;

    public Hunter(String id, int era, int minPlayer, boolean isObtainable, boolean foodIcon) {
        super(id, era, minPlayer, isObtainable);
        this.foodIcon = foodIcon;
    }

    public boolean hasFoodIcon(){
        return this.foodIcon;
    }

    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}
