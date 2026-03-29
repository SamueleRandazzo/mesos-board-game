package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Cards.*;
import it.polimi.ingsw.model.Interfaces.*;
import it.polimi.ingsw.model.Board.*;
import it.polimi.ingsw.model.factories.TurnOrderFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Central orchestrator of a Mesos game session.
 * <p>
 * Each instance represents one independent game, supporting multiple
 * concurrent games (e.g. on a server) without Singleton constraints.
 * <p>
 * Responsibilities:
 *  - Initializing and holding all model objects (Board, Players, OfferTrack)
 *  - Managing the current era and detecting era transitions
 *  - Driving the 10-round game loop (place totems → resolve actions → end-of-round)
 *  - Resolving events found in the lower row at end of round
 *  - Computing and declaring the winner at game end
 * <p>
 * What Game does NOT do (delegated to other classes):
 *  - Filling / rotating board rows mechanically  → Board
 *  - Applying event effects to players           → EventCard.raiseEvent()
 *  - Tracking tribe composition                  → Tribe
 *  - Tracking player resources                   → Player
 */
public class Game {

    //region Constants
    /** Total number of rounds in a game of Mesos. */
    private static final int TOTAL_ROUNDS = 10;
    /** First era number. */
    private static final int FIRST_ERA = 1;
    //endregion

    //region Attributes
    /** All players in this game, in initial turn order. */
    private final List<Player> players;
    /** Number of players in this game. Drives board row sizes and turn order. */
    private final int numPlayers;
    /** The game board (rows, decks). Owned exclusively by this Game instance. */
    private final Board board;
    /** The offer track (tiles where players place their totems). */
    private final OfferTrack offerTrack;
    /** The TurnOrderTrack*/
    private TurnOrderTile TurnOrderTile;
    /**
     * Current era (1, 2 or 3).
     * Starts at 1; advances when a card of the next era is revealed during fillRows().
     */
    private int currentEra;
    /** Round counter. Starts at 1, ends at TOTAL_ROUNDS. */
    private int currentRound;
    /**
     * Building decks for eras 2 and 3, indexed as:
     *  eraBuildingDecks.get(0) = Era 2 buildings
     *  eraBuildingDecks.get(1) = Era 3 buildings
     * Kept here so Game can hand them to Board when a new era begins.
     */
    private final List<List<BuildingCard>> eraBuildingDecks;
    private int currentPlayerIndex = 0;
    //endregion

    /**
     * Creates and fully initialises a new Game instance.
     * <p>
     * The caller (e.g. a controller or factory) is responsible for building
     * the decks according to the setup rules (rulebook p. 2-3) before passing
     * them in.
     *
     * @param players         players in initial (randomised) turn order; size must be 2-5
     * @param tribeDeck       full ordered tribe deck (Era I on top, Final Events at bottom)
     * @param era1Buildings   building cards selected for Era I
     * @param era2Buildings   building cards selected for Era II
     * @param era3Buildings   building cards selected for Era III
     * @param offerTrack      the offer track initialised for the correct player count
     * @throws IllegalArgumentException if player count is outside 2-5
     * @throws NullPointerException     if any argument is null
     */
    public Game(List<Player> players,
                List<TribeDeck> tribeDeck,
                List<BuildingCard> era1Buildings,
                List<BuildingCard> era2Buildings,
                List<BuildingCard> era3Buildings,
                OfferTrack offerTrack)
    {

        if (players == null)       throw new NullPointerException("players cannot be null.");
        if (tribeDeck == null)     throw new NullPointerException("tribeDeck cannot be null.");
        if (era1Buildings == null) throw new NullPointerException("era1Buildings cannot be null.");
        if (era2Buildings == null) throw new NullPointerException("era2Buildings cannot be null.");
        if (era3Buildings == null) throw new NullPointerException("era3Buildings cannot be null.");
        if (offerTrack == null)    throw new NullPointerException("offerTrack cannot be null.");

        this.numPlayers = players.size();
        if (this.numPlayers < 2 || this.numPlayers > 5) {
            throw new IllegalArgumentException(
                    "Mesos supports 2 to 5 players. Got: " + this.numPlayers
            );
        }

        this.players = new ArrayList<>(players);
        this.offerTrack = offerTrack;
        this.currentEra = FIRST_ERA;
        this.currentRound = 1;
        this.currentPlayerIndex = 0;

        // Store Era II and III building decks for later use
        this.eraBuildingDecks = new ArrayList<>();
        this.eraBuildingDecks.add(new ArrayList<>(era2Buildings));
        this.eraBuildingDecks.add(new ArrayList<>(era3Buildings));

        // Board is created with Era I buildings
        this.board = new Board(tribeDeck, era1Buildings);
    }

    public void initializeGame() {
        setupInitialRows();
        createTurnOrderTile(numPlayers);
        this.currentRound = 1;
        this.currentPlayerIndex = 0;
    }

    /**
     * Fills the lower row (numPlayers + 1 cards) and then the upper row
     * (numPlayers + 4 cards) during initial setup (rulebook p. 2, steps 4-5).
     * <p>
     * Event cards drawn for the lower row are redirected to the upper row
     * as required by the rules.
     */
    public void setupInitialRows() {
        // TODO: implement initial lower-row setup with event-card redirection.
        // For now, delegate standard fill to Board.
        board.fillRows(numPlayers);
    }

    /**
     * End-of-round sequence (rulebook p. 6):
     *  1. Resolve Event cards in the lower row (Sustenance last).
     *  2. Discard lower tribe row, move upper tribe row down, refill upper row.
     *  3. Check whether a new era has started and handle the transition.
     */
    public void endOfRound() {
        // Step 1 — resolve events in the lower row
        resolveEventsInLowerRow();

        // Step 2 — board cleanup and refill
        board.cleanUpAtRoundEnd(numPlayers);

        // Step 3 — check for era transition triggered by newly revealed cards
        checkEraTransition();
    }

    /**
     * Resolves all Event cards currently in the lower tribe row (rulebook p. 6).
     * Sustenance is always resolved last.
     * If two events of the same type appear, they are resolved in era order.
     * <p>
     * Non-event cards (Character, Building) are ignored here.
     */
    private void resolveEventsInLowerRow() {
        List<TribeDeck> bottomRow = board.getBottomRow();

        List<EventCard> events = new ArrayList<>();
        for (TribeDeck card : bottomRow) {
            if (card instanceof EventCard) {
                events.add((EventCard) card);
            }
        }

        if (events.isEmpty()) return;

        // Sort: non-sustenance events first (by era), sustenance last.
        // TODO: identify Sustenance by subclass once EventCard subclasses are available.
        events.sort(Comparator.comparingInt(EventCard::getEra));

        for (EventCard event : events) {
            event.raiseEvent(getPlayers()); // NOTE: raiseEvent() still uses Game.game() singleton — to fix later.
        }
    }

    /**
     * Checks whether the cards just revealed in the upper row belong to a new era.
     * If so, triggers the era transition on Board (rulebook p. 7).
     */

    private void checkEraTransition() {
        List<TribeDeck> topRow = board.getTopRow();

        for (TribeDeck card : topRow) {
            int cardEra = card.getEra();
            if (cardEra > currentEra) {
                advanceEra(cardEra);
                break; // One transition per round is enough
            }
        }
    }

    /**
     * Advances the game to {@code newEra} and updates the board accordingly.
     *
     * @param newEra the era number to transition to (must be > currentEra)
     */
    private void advanceEra(int newEra) {
        currentEra = newEra;

        // Load the building deck for the new era into Board before calling updateEra().
        // eraBuildingDecks index: era 2 → index 0, era 3 → index 1
        int deckIndex = newEra - 2;
        if (deckIndex >= 0 && deckIndex < eraBuildingDecks.size()) {
            board.setBuildingDeck(eraBuildingDecks.get(deckIndex));
        }

        board.updateEra();
    }

    /**
     * At the end of round 10, resolves all Event cards still visible
     * on the board — both upper and lower rows (rulebook p. 7).
     * Sustenance is resolved last as usual.
     */
    private void resolveRemainingEvents() {
        List<EventCard> events = new ArrayList<>();

        for (TribeDeck card : board.getBottomRow()) {
            if (card instanceof EventCard) events.add((EventCard) card);
        }
        for (TribeDeck card : board.getTopRow()) {
            if (card instanceof EventCard) events.add((EventCard) card);
        }

        events.sort(Comparator.comparingInt(EventCard::getEra));

        for (EventCard event : events) {
            event.raiseEvent(getPlayers()); // NOTE: to fix after EventCard Singleton dependency is removed.
        }
    }

    /**
     * Adds end-of-game prestige points for each player (rulebook p. 7):
     *  - Builder cards PP
     *  - Inventor PP (numInventors x numDistinctInventionIcons)
     *  - 10 PP per every 2 Artist cards
     *  - Building card PP (printed + end-game effects)
     * <p>
     * TODO: implement once Tribe exposes the required query methods.
     */
    private void computeFinalScores() {
        for (Player p : players) {
            p.changePrestigePoints(p.getTribe().getTotalScoringBuildingsPoints());
            p.changePrestigePoints((p.getTribe().getArtistsCount() / 2) * 10);
            p.changePrestigePoints(p.getTribe().getInventorsCount() * p.getTribe().totalDifferentInventorIcon());
            p.changePrestigePoints(p.getTribe().totalBuildersPoints());
        }
    }

    /**
     * Determines and returns the winner(s) (rulebook p. 7).
     * Tiebreaker: most food. If still tied, victory is shared.
     *
     * @return list of winning players (size > 1 means shared victory)
     */
    public List<Player> getWinner() {
        int maxPP = players.stream()
                .mapToInt(Player::getPrestigePoints)
                .max()
                .orElse(0);

        List<Player> winners = players.stream()
                .filter(p -> p.getPrestigePoints() == maxPP)
                .collect(Collectors.toList());

        if (winners.size() == 1) return winners;

        // Tiebreaker: most food
        int maxFood = winners.stream()
                .mapToInt(Player::getFoodAmount)
                .max()
                .orElse(0);

        return winners.stream()
                .filter(p -> p.getFoodAmount() == maxFood)
                .collect(Collectors.toList());
    }

    //region Getters
    /**
     * Returns the list of players in this game.
     *
     * @return unmodifiable view of the player list
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Returns the Board owned by this game instance.
     *
     * @return the Board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Returns the current era number (1, 2 or 3).
     *
     * @return current era
     */
    public int getCurrentEra() {
        return currentEra;
    }

    /**
     * Returns the current round number (1-10).
     *
     * @return current round
     */
    public int getCurrentRound() {
        return currentRound;
    }

    /**
     * Returns the number of players in this game.
     *
     * @return number of players
     */
    public int getNumPlayers() {
        return numPlayers;
    }

    /**
     * Returns the offer track for this game.
     *
     * @return the OfferTrack
     */
    public OfferTrack getOfferTrack() {
        return offerTrack;
    }

    public Player getCurrentActivePlayer() {
        return players.get(currentPlayerIndex);
    }
    //endregion

    //create the TurnOrderTile using the TurnOrderFactory
    public boolean createTurnOrderTile(int numPlayers){

        this.TurnOrderTile = TurnOrderFactory.createTrack(numPlayers);

        return true;
        //l' eccezione sul numero di players è già gestita
    }

    public void advanceTurn() {
        this.currentPlayerIndex = (currentPlayerIndex + 1) % numPlayers;
    }

    public void placePlayerTotem(int tileIndex) {
        OfferTile chosen = offerTrack.getTiles().get(tileIndex);

        if (!chosen.isAvailable()) {
            throw new IllegalStateException("Tile già occupata!");
        }

        chosen.placeTotem(getCurrentActivePlayer());

        // Se il regolamento dice che dopo il piazzamento il turno passa al prossimo:
        advanceTurn();
    }

    public List<OfferTile> getTilesToResolve() {
        return offerTrack.getTiles();
    }

    public void nextRound() {
        endOfRound(); // Esegue pulizia board ed eventi
        this.currentRound++;
        this.currentPlayerIndex = 0; // Reset per il nuovo round

        if (this.currentRound > TOTAL_ROUNDS) {
            resolveRemainingEvents();
            computeFinalScores();
        }
    }

    public void resolveUpperCardPlayerPick(int pos) {
        TribeDeck c = this.board.takeCardFromTopRow(pos);
        c.applyTo(this.getCurrentActivePlayer());
    }

    public void resolveLowerCardPlayerPick(int pos) {
        TribeDeck c = this.board.takeCardFromBottomRow(pos);
        c.applyTo(this.getCurrentActivePlayer());
    }

    public void resolveUpperBuildingPlayerPick(int pos) {
        BuildingCard c = this.board.takeCardFromUpperBuildingRow(pos);
        c.applyTo(this.getCurrentActivePlayer());
    }

    public void resolveLowerBuildingPlayerPick(int pos) {
        BuildingCard c = this.board.takeCardFromLowerBuildingRow(pos);
        c.applyTo(this.getCurrentActivePlayer());
    }
}