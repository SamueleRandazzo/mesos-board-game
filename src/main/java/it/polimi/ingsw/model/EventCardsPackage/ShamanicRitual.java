package it.polimi.ingsw.model.EventCardsPackage;

import it.polimi.ingsw.model.Cards.EventCard;
import it.polimi.ingsw.model.Player;

public class ShamanicRitual extends EventCard {
    public ShamanicRitual(int era, int minPlayer, boolean isObtainable, boolean isFinal) {
        super(era, minPlayer, isObtainable, isFinal);
    }

    @Override
    public void eventHandler(Player player){

    }
}
