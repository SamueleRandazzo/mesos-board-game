package it.polimi.ingsw.model.Interfaces;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import java.util.List;

/**
 * Strategy interface representing the execution logic of an event card effect.
 * <p>
 * This functional interface encapsulates the core gameplay resolution rules triggered by
 * mid-game events or final end-game scenarios. Implementations of this interface define how
 * game metrics are evaluated across multiple entities simultaneously, handling global calculations
 * such as feeding penalties (sustenance phase), track majorities (shamanic rituals), or
 * set-collection point scoring (cave paintings & hunt).
 * </p>
 *
 * @see it.polimi.ingsw.model.Cards.EventCard
 */
@FunctionalInterface
public interface EventEffect {

     /**
      * Executes and resolves the specific rules, payouts, or penalties of this event
      * across the active participants within the current game context.
      * <p>
      * This method provides the event logic with direct access to the entire roster of
      * players and the global engine state, enabling cross-player comparisons (e.g., determining
      * who holds the maximum or minimum score/star attributes) and broad state mutations.
      * </p>
      *
      * @param p    the {@link List} of all {@link Player} instances participating in the match
      *             who are subject to the event's evaluation rules
      * @param game the global {@link Game} engine instance managing the shared board, tracks,
      *             and overall phase state
      */
     void resolve(List<Player> p, Game game);
}