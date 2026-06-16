package it.polimi.ingsw.model.Interfaces;

import it.polimi.ingsw.model.Player;

/**
 * Represents a unified contract for any game card template capable of residing within
 * the main tribe deck or appearing in the market rows (both upper and lower) on the central board.
 * <p>
 * This interface provides a polymorphic abstraction over structurally distinct card categories,
 * allowing the game setup systems, deck managers, and market layout handlers to manipulate them
 * interchangeably during card drawing, shifting, and purchasing routines.
 * </p>
 *
 * @see it.polimi.ingsw.model.Cards.EventCard
 */
public interface TribeDeck {

    /**
     * Retrieves the unique alphanumeric identifier assigned to this card template.
     *
     * @return a {@link String} representing the card's unique ID
     */
    String getId();

    /**
     * Retrieves the specific historical game era (1, 2, or 3) this card belongs to.
     *
     * @return the era index of the card
     */
    int getEra();

    /**
     * Checks whether this card is currently available to be bought, drafted, or acquired
     * by a player based on its internal structural state.
     *
     * @return {@code true} if a player can legitimately obtain this card; {@code false} otherwise
     */
    boolean getIsObtainable();

    /**
     * Executes the internal gameplay effects, resource distribution, or immediate bonuses
     * defined by this card directly onto the target player's state.
     *
     * @param p the {@link Player} instance who is acquiring or triggering this card
     */
    void applyTo(Player p);

    /**
     * Helper discriminator method used to distinguish background events from interactable
     * character assets without relying on unsafe reflection or {@code instanceof} checks.
     * <p>
     * The default implementation returns {@code false}. Concrete event components must
     * override this method to return {@code true}.
     * </p>
     *
     * @return {@code true} if the implementing card is an event scenario; {@code false}
     *         if it represents a recruitable character card
     */
    default boolean isEvent() {
        return false;
    }
}