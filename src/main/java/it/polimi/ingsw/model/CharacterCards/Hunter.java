package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Interfaces.PlayerTribe;
import it.polimi.ingsw.model.Interfaces.TribeDeck;

public class Hunter extends Card implements TribeDeck, PlayerTribe {
    private final boolean foodIcon;

    public Hunter(int era, int minPlayer, boolean isObtainable, boolean foodIcon) {
        super(era, minPlayer, isObtainable);
        this.foodIcon = foodIcon;
    }

    public boolean hasFoodIcon(){
        return this.foodIcon;
    }

    @Override
    public void addToTribe(Tribe tribe) {
        tribe.addCard(this);
    }
}
