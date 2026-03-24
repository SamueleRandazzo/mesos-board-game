package it.polimi.ingsw.model.CharacterCards;

import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Interfaces.PlayerTribe;
import it.polimi.ingsw.model.Interfaces.TribeDeck;

public class Shaman extends Card implements TribeDeck, PlayerTribe {
    private final int shamanStars;

    public Shaman(int era, int minPlayer, boolean isObtainable, int shamanStars){
        super(era, minPlayer, isObtainable);
        this.shamanStars = shamanStars;
    }

    public int getShamanStars(){
        return this.shamanStars;
    }

    @Override
    public void addToTribe(Tribe tribe) {
        tribe.addCard(this);
    }
}
