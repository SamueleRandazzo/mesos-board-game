package it.polimi.ingsw.model.Board;

import it.polimi.ingsw.model.Cards.*;
import it.polimi.ingsw.model.Interfaces.*;
import it.polimi.ingsw.network.DTO.BoardDTO;
import it.polimi.ingsw.network.DTO.CardDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the game board of Mesos.
 * <p>
 * Board is owned by Game (1-to-1 composition): Game instantiates it and
 * exposes it via Game.getBoard(). Board itself is NOT a Singleton.
 * <p>
 * Responsibilities:
 * - Maintaining the four visible card rows (upper/lower tribe, upper/lower building)
 * - Holding the tribe deck and the current-era building deck
 * - Providing card-pick operations for both tribe and building rows
 * - Executing end-of-round cleanup and era transitions
 */
public class Board {

    private static final int UPPER_EXTRA_CARDS = 4;
    private static final int LOWER_EXTRA_CARDS = 1;

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

    /** Draw pile for tribe cards (Era 1 on top, Final Events at the bottom). */
    private List<TribeDeck> tribeDeck;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates the Board.
     * Called once by Game during game setup.
     * <p>
     * After construction the rows are empty; Game must call
     * {@link #fillUpperRow(int)} and set up the initial lower row separately
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
        this.upperBuildingCards = new ArrayList<>(buildingDeck);
        this.lowerBuildingCards = new ArrayList<>();
    }

    /**
     * Recreates the Board from previously saved rows and decks.
     *
     * @param tribeDeck draw pile for tribe cards
     * @param buildingDeck draw pile for building cards of the current era
     * @param upperTribeCards tribe cards currently visible in the upper row
     * @param lowerTribeCards tribe cards currently visible in the lower row
     * @param upperBuildingCards building cards currently visible in the upper row
     * @param lowerBuildingCards building cards currently visible in the lower row
     */
    public Board(List<TribeDeck> tribeDeck,
                 List<BuildingCard> buildingDeck,
                 List<TribeDeck> upperTribeCards,
                 List<TribeDeck> lowerTribeCards,
                 List<BuildingCard> upperBuildingCards,
                 List<BuildingCard> lowerBuildingCards) {
        this.tribeDeck = new ArrayList<>(tribeDeck);
        this.buildingDeck = new ArrayList<>(buildingDeck);
        this.upperTribeCards = new ArrayList<>(upperTribeCards);
        this.lowerTribeCards = new ArrayList<>(lowerTribeCards);
        this.upperBuildingCards = new ArrayList<>(upperBuildingCards);
        this.lowerBuildingCards = new ArrayList<>(lowerBuildingCards);
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
     * Returns a defensive copy of the tribe draw pile.
     *
     * @return list of remaining tribe cards in deck order
     */
    public List<TribeDeck> getTribeDeck() {
        return new ArrayList<>(tribeDeck);
    }

    /**
     * Returns a defensive copy of the building draw pile.
     *
     * @return list of remaining building cards in deck order
     */
    public List<BuildingCard> getBuildingDeck() {
        return new ArrayList<>(buildingDeck);
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
     * - Upper row target size = numPlayers + 4
     * <p>
     * Note: detection of era changes when a next-era card is drawn, and the
     * special handling of Event cards that must go to the upper row during
     * initial lower-row setup, are the responsibility of Game, which calls
     * this method and inspects the result.
     *
     * @param numPlayers number of players, used to compute the upper row target size
     */
    public void fillUpperRow(int numPlayers) {
        int needed = numPlayers + UPPER_EXTRA_CARDS - upperTribeCards.size();

        for (int i = 0; i < needed; i++) {
            if (tribeDeck.isEmpty())
                break;
            upperTribeCards.add(tribeDeck.removeFirst());
        }
    }

    /**
     * Initializes the lower tribe row with character cards, moving event cards to the upper row.
     *
     * @param numPlayers number of players, used to compute the lower row target size
     */
    public void initializeLowerRow(int numPlayers) {
        int needed = numPlayers + LOWER_EXTRA_CARDS;

        int i = 0;
        while (i < needed) {
            if (tribeDeck.isEmpty())
                break;

            TribeDeck card = tribeDeck.removeFirst();

            if (card.isEvent()) {
                upperTribeCards.add(card);
            } else {
                lowerTribeCards.add(card);
                i++;
            }
        }
    }
    // -------------------------------------------------------------------------
    // End-of-round cleanup
    // -------------------------------------------------------------------------

    /**
     * Performs the end-of-round board cleanup (rulebook p. 6):
     * <p>
     * 1. Discard all Character and Event cards from the lower tribe row.
     * Building cards in the lower row are NOT affected (they stay).
     * 2. Move all Character and Event cards from the upper tribe row
     * down to the lower tribe row.
     * Building cards in the upper row are NOT affected (they stay).
     * 3. Refill the upper tribe row via {@link #fillUpperRow(int)}.
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
        fillUpperRow(numPlayers);
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

    // -------------------------------------------------------------------------
    // DTO Generation
    // -------------------------------------------------------------------------

    /**
     * Generates a BoardDTO representing the current state of the board.
     * Uses CardDTOs to send only the unique identifiers of the cards to the network/View.
     *
     * @return A DTO snapshot of the board.
     */
    public BoardDTO toDTO() {
        List<CardDTO> upperTribe = getIdsFromTribeList(this.upperTribeCards);
        List<CardDTO> lowerTribe = getIdsFromTribeList(this.lowerTribeCards);
        List<CardDTO> upperBuildings = getIdsFromBuildingList(this.upperBuildingCards);
        List<CardDTO> lowerBuildings = getIdsFromBuildingList(this.lowerBuildingCards);
        int firstCardEra = tribeDeck.isEmpty() ? 0 : tribeDeck.getFirst().getEra();

        return new BoardDTO(upperTribe, lowerTribe, upperBuildings, lowerBuildings, firstCardEra);
    }

    /**
     * Helper to extract CardDTOs from TribeDeck lists.
     */
    private List<CardDTO> getIdsFromTribeList(List<TribeDeck> cards) {
        return cards.stream()
                .map(card -> new CardDTO(card.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Helper to extract CardDTOs from BuildingCard lists.
     */
    private List<CardDTO> getIdsFromBuildingList(List<BuildingCard> cards) {
        return cards.stream()
                .map(card -> new CardDTO(card.getId()))
                .collect(Collectors.toList());
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
