package it.polimi.ingsw.model.Interfaces;

import it.polimi.ingsw.model.Cards.Tribe;

/**
 * Strategy interface used to compute the total number of character cards matching
 * a specific criteria within a player's tribe tableau.
 * <p>
 * This functional interface enables decoupled, dynamic evaluation of tribe configurations.
 * It is primarily utilized by scoring systems, building perks, or event triggers that need
 * to count specific profession types (e.g., counting only Hunters, Shamans, or unique sets
 * of characters) without exposing the underlying data structures of the {@link Tribe}.
 * </p>
 * <p>
 * Since this interface contains a single abstract method, it can be cleanly implemented
 * using Lambda expressions or method references within the game evaluation loops.
 * </p>
 *
 * @see Tribe
 */
@FunctionalInterface
public interface CharacterTypeCount {

    /**
     * Evaluates the given tribe state and returns the count of cards that satisfy
     * this specific counter's matching rules.
     *
     * @param t the target {@link Tribe} instance whose character cards are to be analyzed
     * @return the total number of matching character cards found in the specified tribe
     */
    int cardNumber(Tribe t);
}