package it.polimi.ingsw.model.CharacterTypeCounts;

import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Interfaces.CharacterTypeCount;

public class ShamansCount implements CharacterTypeCount {
    @Override
    public int cardNumber(Tribe t) {
        return t.getShamansCount();
    }
}
