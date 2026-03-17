package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Cards.*;
import it.polimi.ingsw.model.Interfaces.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the game board of Mesos.
 * <p>
 * Board is owned by Game (1-to-1 composition): Game instantiates it and
 * exposes it via Game.getBoard(). Board itself is NOT a Singleton.
 * <p>
 * Responsibilities:
 *  - Maintaining the four visible card rows (upper/lower tribe, upper/lower building)
 *  - Holding the tribe deck and the current-era building deck
 *  - Providing card-pick operations for both tribe and building rows
 *  - Executing end-of-round cleanup and era transitions
 */
public class Board {

    /** Tribe cards visible in the upper row (CharacterCard or EventCard). */
    private List<TribeDeck> upperTribeCards;

    /** Tribe cards visible in the lower row (CharacterCard or EventCard). */
    private List<TribeDeck> lowerTribeCards;

    /** Building cards visible in the upper row. */
    private List<BuildingCard> upperBuildingCards;

    /** Building cards visible in the lower row. */
    private List<BuildingCard> lowerBuildingCards;

    /** Draw pile for building cards of the current era. */
    private List<BuildingCard> buildingDeck;

    /** Draw pile for tribe cards (Era I on top, Final Events at the bottom). */
    private List<TribeDeck> tribeDeck;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates the Board.
     * Called once by Game during game setup (preparazione).
     * <p>
     * After construction the rows are empty; Game must call
     * {@link #fillRows(int)} and set up the initial lower row separately
     * following the setup rules (p. 2-3 of the rulebook).
     *
     * @param tribeDeck    the full, ordered tribe deck (Era I on top, Final Events at bottom)
     * @param buildingDeck the Era-I building cards
     */
    public Board(List<TribeDeck> tribeDeck, List<BuildingCard> buildingDeck) {
        this.tribeDeck          = new ArrayList<>(tribeDeck);
        this.buildingDeck       = new ArrayList<>(buildingDeck);
        this.upperTribeCards    = new ArrayList<>();
        this.lowerTribeCards    = new ArrayList<>();
        this.upperBuildingCards = new ArrayList<>();
        this.lowerBuildingCards = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Tribe row accessors
    // -------------------------------------------------------------------------

    /**
     * Returns a defensive copy of the upper tribe row.
     *
     * @return list of TribeDeck currently in the upper row
     */
    public List<TribeDeck> getTopRow() {
        return new ArrayList<>(upperTribeCards);
    }

    /**
     * Returns a defensive copy of the lower tribe row.
     *
     * @return list of TribeDeck currently in the lower row
     */
    public List<TribeDeck> getBottomRow() {
        return new ArrayList<>(lowerTribeCards);
    }

    /**
     * Removes and returns the tribe card at {@code index} from the upper row.
     * Called when a player picks a card from the upper row during their turn.
     *
     * @param index 0-based position in the upper row
     * @return the TribeDeck card that was at that position
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public TribeDeck takeCardFromTopRow(int index) {
        if (index < 0 || index >= upperTribeCards.size()) {
            throw new IndexOutOfBoundsException(
                    "Invalid index " + index + " for upper tribe row of size " + upperTribeCards.size()
            );
        }
        return upperTribeCards.remove(index);
    }

    /**
     * Removes and returns the tribe card at {@code index} from the lower row.
     * Called when a player picks a card from the lower row during their turn.
     *
     * @param index 0-based position in the lower row
     * @return the TribeDeck card that was at that position
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public TribeDeck takeCardFromBottomRow(int index) {
        if (index < 0 || index >= lowerTribeCards.size()) {
            throw new IndexOutOfBoundsException(
                    "Invalid index " + index + " for lower tribe row of size " + lowerTribeCards.size()
            );
        }
        return lowerTribeCards.remove(index);
    }

    // -------------------------------------------------------------------------
    // Building row accessors
    // -------------------------------------------------------------------------

    /**
     * Returns a defensive copy of the upper building row.
     *
     * @return list of BuildingCard currently in the upper building row
     */
    public List<BuildingCard> getUpperBuildingCards() {
        return new ArrayList<>(upperBuildingCards);
    }

    /**
     * Returns a defensive copy of the lower building row.
     *
     * @return list of BuildingCard currently in the lower building row
     */
    public List<BuildingCard> getLowerBuildingCards() {
        return new ArrayList<>(lowerBuildingCards);
    }

    /**
     * Removes and returns the building card at {@code index} from the upper building row.
     * Called when a player pays for and acquires a building from the upper row.
     *
     * @param index 0-based position in the upper building row
     * @return the BuildingCard that was at that position
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public BuildingCard takeCardFromUpperBuildingRow(int index) {
        if (index < 0 || index >= upperBuildingCards.size()) {
            throw new IndexOutOfBoundsException(
                    "Invalid index " + index + " for upper building row of size " + upperBuildingCards.size()
            );
        }
        return upperBuildingCards.remove(index);
    }

    /**
     * Removes and returns the building card at {@code index} from the lower building row.
     * Called when a player pays for and acquires a building from the lower row.
     *
     * @param index 0-based position in the lower building row
     * @return the BuildingCard that was at that position
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public BuildingCard takeCardFromLowerBuildingRow(int index) {
        if (index < 0 || index >= lowerBuildingCards.size()) {
            throw new IndexOutOfBoundsException(
                    "Invalid index " + index + " for lower building row of size " + lowerBuildingCards.size()
            );
        }
        return lowerBuildingCards.remove(index);
    }

    // -------------------------------------------------------------------------
    // Row fill
    // -------------------------------------------------------------------------

    /**
     * Draws tribe cards from the deck and places them in the upper tribe row
     * until the row reaches (numPlayers + 4) cards, or the deck is exhausted.
     * <p>
     * Cards already present in the upper row count towards the target, so this
     * method is safe to call both for initial setup and for end-of-round refill.
     * <p>
     * Rules reference (rulebook p. 2 step 5, p. 6 step 4):
     *  - Upper row target size = numPlayers + 4
     * <p>
     * Note: detection of era changes when a next-era card is drawn, and the
     * special handling of Event cards that must go to the upper row during
     * initial lower-row setup, are the responsibility of Game, which calls
     * this method and inspects the result.
     *
     * @param numPlayers number of players, used to compute the upper row target size
     */
    public void fillRows(int numPlayers) {
        int target       = numPlayers + 4;
        int alreadyInRow = upperTribeCards.size();
        int needed       = target - alreadyInRow;

        for (int i = 0; i < needed; i++) {
            if (tribeDeck.isEmpty()) break;
            upperTribeCards.add(tribeDeck.remove(0));
        }
    }

    // -------------------------------------------------------------------------
    // End-of-round cleanup
    // -------------------------------------------------------------------------

    /**
     * Performs the end-of-round board cleanup (rulebook p. 6):
     * <p>
     *  1. Discard all Character and Event cards from the lower tribe row.
     *     Building cards in the lower row are NOT affected (they stay).
     *  2. Move all Character and Event cards from the upper tribe row
     *     down to the lower tribe row.
     *     Building cards in the upper row are NOT affected (they stay).
     *  3. Refill the upper tribe row via {@link #fillRows(int)}.
     * <p>
     * Event resolution and era-change detection happen in Game, which calls
     * this method after all players have taken their turns.
     *
     * @param numPlayers number of players, used to compute the upper row target size
     */
    public void cleanUpAtRoundEnd(int numPlayers) {
        lowerTribeCards.clear();
        lowerTribeCards.addAll(upperTribeCards);
        upperTribeCards.clear();
        fillRows(numPlayers);
    }

    // -------------------------------------------------------------------------
    // Era transition
    // -------------------------------------------------------------------------

    /**
     * Handles the start of a new era.
     * <p>
     * Clears any building cards remaining in the lower building row (will be
     * empty on the first era transition, so clear() is always safe to call),
     * moves all building cards from the upper building row down to the lower
     * building row, then places the new era's building cards in the upper row.
     * <p>
     * Game is responsible for calling setBuildingDeck() with the new era's
     * building cards before calling this method.
     */
    public void updateEra() {
        // Discard any building cards left in the lower row
        lowerBuildingCards.clear();

        // Move upper building cards down to the lower building row
        lowerBuildingCards.addAll(upperBuildingCards);
        upperBuildingCards.clear();

        // Place the new era's building cards in the upper row
        upperBuildingCards.addAll(buildingDeck);
        buildingDeck.clear();
    }

    // -------------------------------------------------------------------------
    // Building deck management
    // -------------------------------------------------------------------------

    /**
     * Replaces the current building deck with the cards of the upcoming era.
     * Game must call this before {@link #updateEra()} when transitioning
     * to a new era.
     *
     * @param newEraBuildings building cards for the new era
     * @throws NullPointerException if newEraBuildings is null
     */
    public void setBuildingDeck(List<BuildingCard> newEraBuildings) {
        if (newEraBuildings == null) {
            throw new NullPointerException("newEraBuildings cannot be null.");
        }
        this.buildingDeck = new ArrayList<>(newEraBuildings);
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    /**
     * Returns {@code true} when the tribe deck has been fully drawn.
     * Game uses this to detect that no more refills are possible (end of game).
     *
     * @return true if the tribe deck is empty
     */
    public boolean isTribeDeckEmpty() {
        return tribeDeck.isEmpty();
    }

    @Override
    public String toString() {
        return "Board{"
                + "upperTribe="     + upperTribeCards.size()    + " cards"
                + ", lowerTribe="   + lowerTribeCards.size()    + " cards"
                + ", upperBuilding=" + upperBuildingCards.size() + " cards"
                + ", lowerBuilding=" + lowerBuildingCards.size() + " cards"
                + ", tribeDeck="    + tribeDeck.size()          + " left"
                + ", buildingDeck=" + buildingDeck.size()       + " left"
                + '}';
    }
}