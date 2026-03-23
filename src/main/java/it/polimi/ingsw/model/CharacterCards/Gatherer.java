package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Interfaces.PlayerTribe;
import it.polimi.ingsw.model.Interfaces.TribeDeck;

public class Gatherer extends Card implements TribeDeck, PlayerTribe {
    public Gatherer(int era, int minPlayer, boolean isObtainable){
        super(era, minPlayer, isObtainable);
    }

    @Override
    public void addToTribe(Tribe tribe) {
        tribe.addCard(this);
    }
}
