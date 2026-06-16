package it.polimi.ingsw.model.CharacterTypeCounts;

import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Interfaces.CharacterTypeCount;

/**
 * Implementation of the {@link CharacterTypeCount} interface specialized
 * for counting the number of Gatherer characters within a specific Tribe.
 */
public class GatherersCount implements CharacterTypeCount {

    /**
     * Retrieves the total number of Gatherer cards present in the given Tribe.
     *
     * @param t the {@link Tribe} instance from which to count the Gatherers.
     * @return the number of Gatherer characters in the specified tribe.
     */
    @Override
    public int cardNumber(Tribe t) {
        return t.getGatherersCount();
    }
}