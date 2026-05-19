package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Cards.*;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.EventEffects.Sustenance;
import it.polimi.ingsw.model.Interfaces.*;
import it.polimi.ingsw.model.Board.*;
import it.polimi.ingsw.model.factories.TurnOrderFactory;
import it.polimi.ingsw.model.states.EndGameState;
import it.polimi.ingsw.model.states.GameState;
import it.polimi.ingsw.model.states.SetupGameState;
import it.polimi.ingsw.model.states.TotemPlacementState;
import it.polimi.ingsw.model.states.ActionResolutionState;
import it.polimi.ingsw.network.DTO.LeaderboardDTO;
import it.polimi.ingsw.network.DTO.OfferTileDTO;
import it.polimi.ingsw.network.DTO.PlayerRankDTO;
import it.polimi.ingsw.network.DTO.TribeStatusDTO;
import it.polimi.ingsw.network.DTO.TurnOrderTileDTO;
import java.util.*;
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
    private TurnOrderTile turnOrderTile;
    /**
     * Current era (1, 2 or 3).
     * Starts at 1; advances when a card of the next era is revealed during fillUpperRow().
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
    private int currentPlayerIndex;

    private GameState currentState;

    /**the sequence of players for the current phase.*/
    private List<Player> roundTurnOrder;

    private final List<GameEventListener> listeners = new ArrayList<>();
    //endregion

    /**
     * Creates and fully initializes a new Game instance.
     * <p>
     * The caller (e.g. a controller or factory) is responsible for building
     * the decks according to the setup rules (rulebook p. 2-3) before passing
     * them in.
     *
     * @param players         players in initial (randomized) turn order; size must be 2-5
     * @param tribeDeck       full ordered tribe deck (Era I on top, Final Events at bottom)
     * @param era1Buildings   building cards selected for Era I
     * @param era2Buildings   building cards selected for Era II
     * @param era3Buildings   building cards selected for Era III
     * @param offerTrack      the offer track initialized for the correct player count
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
        this.roundTurnOrder = new ArrayList<>(players);

        // Store Era II and III building decks for later use
        this.eraBuildingDecks = new ArrayList<>();
        this.eraBuildingDecks.add(new ArrayList<>(era2Buildings));
        this.eraBuildingDecks.add(new ArrayList<>(era3Buildings));

        // Board is created with Era I buildings
        this.board = new Board(tribeDeck, era1Buildings);

        this.currentState = new SetupGameState();
    }

    public Game(List<Player> players,
                Board board,
                OfferTrack offerTrack,
                TurnOrderTile turnOrderTile,
                List<List<BuildingCard>> eraBuildingDecks,
                int currentEra,
                int currentRound,
                int currentPlayerIndex,
                List<Player> roundTurnOrder,
                GameState currentState) {
        if (players == null) throw new NullPointerException("players cannot be null.");
        if (board == null) throw new NullPointerException("board cannot be null.");
        if (offerTrack == null) throw new NullPointerException("offerTrack cannot be null.");
        if (turnOrderTile == null) throw new NullPointerException("turnOrderTile cannot be null.");
        if (eraBuildingDecks == null) throw new NullPointerException("eraBuildingDecks cannot be null.");
        if (roundTurnOrder == null) throw new NullPointerException("roundTurnOrder cannot be null.");
        if (currentState == null) throw new NullPointerException("currentState cannot be null.");

        this.numPlayers = players.size();
        if (this.numPlayers < 2 || this.numPlayers > 5) {
            throw new IllegalArgumentException(
                    "Mesos supports 2 to 5 players. Got: " + this.numPlayers
            );
        }

        this.players = new ArrayList<>(players);
        this.board = board;
        this.offerTrack = offerTrack;
        this.turnOrderTile = turnOrderTile;
        this.eraBuildingDecks = new ArrayList<>();
        for (List<BuildingCard> deck : eraBuildingDecks) {
            this.eraBuildingDecks.add(new ArrayList<>(deck));
        }
        this.currentEra = currentEra;
        this.currentRound = currentRound;
        this.currentPlayerIndex = currentPlayerIndex;
        this.roundTurnOrder = new ArrayList<>(roundTurnOrder);
        this.currentState = currentState;
    }

    /**
     * Triggers the start of the game execution.
     * <p>
     * This method delegates the specific game initialization logic, validation,
     * and initial state transitions to the active {@link GameState}.
     * </p>
     */
    public void startGame() {
        currentState.startGame(this);
    }

    /**
     * Initializes the fundamental components and resources required at the beginning of the game.
     * <p>
     * This method distributes starting food resources to players based on their turn order positioning
     * using a specific modifier formula, populates the initial board rows, and generates the turn order track.
     * Finally, it sets the current round counter to one and resets the active player index to zero.
     * </p>
     */
    public void initializeGame() {
        for (int i = 1; i <= players.size(); i++) {
            int initFoodModifier = i / 2 + 1;
            players.get(i - 1).changeFoodAmount(initFoodModifier);
        }

        setupInitialRows();
        createTurnOrderTile(numPlayers);

        this.currentRound = 1;
        this.currentPlayerIndex = 0;
    }

    //region Listener
    /**
     * Registers a new game event listener to the list of subscribers.
     * Registered listeners will receive notifications regarding state changes
     * and turn transitions throughout the different phases of the match.
     *
     * @param listener the {@link GameEventListener} instance to be added.
     */
    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }

    /**
     * Notifies all registered listeners that the turn has changed during the Totem Placement phase.
     * <p>
     * This method performs a mapping of the internal {@link OfferTile} model objects into
     * a list of {@link OfferTileDTO} (Data Transfer Objects). This ensures that the View
     * receives a decoupled and serializable representation of the offer track,
     * containing only the necessary information for display.
     * </p>
     */
    public void notifyTotemPlacementTurnChanged() {
        for (GameEventListener l : listeners) {
            l.onTotemPlacementTurnChanged(this.getCurrentActivePlayer().getNickname());
        }
    }

    /**
     * Notifies all registered listeners that the turn has changed during the Action Resolution phase.
     * <p>
     * It sends the nickname of the currently active player to the listeners,
     * allowing the View to update its state and prompt the correct user for input.
     * </p>
     */
    public void notifyActionResultTurnChanged () {
        if (this.getCurrentActivePlayer().getUpperPick() == 0 && this.getCurrentActivePlayer().getLowerPick() == 0) {
            resolveEndTurn();
        } else {
            boolean canChoose = checkIfCurrentPlayerCanChoose();

            if (canChoose) {
                for (GameEventListener l : listeners) {
                    l.onActionResultTurnChanged(this.getCurrentActivePlayer().getNickname());
                }
            } else {
                boolean canBuyBuilding = checkIfCurrentPlayerCanBuyBuilding();

                if (canBuyBuilding) {
                    for (GameEventListener l : listeners) {
                        l.onManualEndTurnRequest(this.getCurrentActivePlayer().getNickname());
                    }
                } else {
                    resolveEndTurn();
                }
            }
        }
    }

    /**
     * Notifies all registered listeners that a totem has been placed by the current player.
     *
     * @param tileIndex the index of the tile where the totem was positioned.
     */
    public void notifyOnTotemPlaced(int tileIndex) {
        for (GameEventListener l : listeners) {
            l.onTotemPlaced(this.getCurrentActivePlayer().getNickname(), tileIndex);
        }
    }

    /**
     * Synchronizes the state of the Offer Track by notifying all listeners.
     * It maps the domain model tiles to Data Transfer Objects (DTOs), including
     * the nickname of the player who occupied the tile, if applicable.
     */
    public void notifyOnShowOfferTrack() {
        List<OfferTile> modelTiles = offerTrack.getTiles();
        List<OfferTileDTO> tiles = new ArrayList<>();

        for (int i = 0; i < modelTiles.size(); i++) {
            OfferTile t = modelTiles.get(i);
            String nick = t.isAvailable() ? null : t.getPlacedPlayer().getNickname();

            tiles.add(new OfferTileDTO(
                    i,
                    t.getFoodBonus(),
                    t.getTopRowDraws(),
                    t.getBottomRowDraws(),
                    nick
            ));
        }

        for (GameEventListener l : listeners) {
            l.onShowOfferTrack(tiles);
        }
    }

    /**
     * Notifies listeners to update the visual representation of a specific player's tribe.
     * Converts the current player's tribe status into a DTO for remote transmission.
     *
     * @param playerNickname the nickname of the player whose tribe status is being broadcast.
     */
    public void notifyShowTribe(String playerNickname) {
        Player player = players.stream()
                               .filter(p -> p.getNickname().equals(playerNickname))
                               .findFirst()
                               .orElse(null);

        if (player != null) {
            TribeStatusDTO tribeDTO = player.getTribe().toDTO();

            for (GameEventListener l : listeners) {
                l.onShowTribe(playerNickname, tribeDTO);
            }
        }
    }

    /**
     * Broadcasts a generic game event message associated with a specific player to all listeners.
     *
     * @param player       the player instance related to the event.
     * @param eventMessage the description or content of the event to be displayed.
     */
    public void notifyEventMessage(Player player, String eventMessage) {
        for (GameEventListener l : listeners) {
            l.onEventMessage(player.getNickname(), eventMessage);
        }
    }

    /**
     * This method sends the updated board to all connected clients
     */
    public void notifyOnShowBoard() {
        for (GameEventListener l : listeners) {
            l.onShowBoard(this.board.toDTO());
        }
    }

    /**
     * Notifies listeners to build the static turn-order tile structure.
     * The DTO contains all the information about the turnOrderTile of the game
     */
    public void notifyDisplayTurnOrderTile() {

        List<TurnOrderTileDTO> turnOrderTile = new ArrayList<>();

        for (TurnOrderSlot s : this.turnOrderTile.getSlots()) {
            String nick = s.getOccupyingPlayer().isPresent() ? s.getOccupyingPlayer().get().getNickname() : "Empty";
            Color color = s.getOccupyingPlayer().isPresent() ? s.getOccupyingPlayer().get().getColor() : null;
            turnOrderTile.add(new TurnOrderTileDTO(nick, color, s.getFoodModifier()));
        }

        for(GameEventListener l : listeners) {
            l.onDisplayTurnOrderTile(turnOrderTile);
        }

    }

    /**
     * Notifies all registered game event listeners of the current turn order for the round.
     * <p>
     * This method extracts the nicknames of the players from the {@code roundTurnOrder} list,
     * maps them into a list of strings, and then broadcasts this ordered list to each
     * {@link GameEventListener} via the {@code onShowPlayersOrder} callback.
     * </p>
     */
    public void notifyShowPlayerOrder() {
        List<String> playersOrder = roundTurnOrder.stream()
                .map(Player::getNickname)
                .toList();

        for (GameEventListener l : listeners) {
            l.onShowPlayersOrder(playersOrder);
        }
    }

    /**
     * Triggers the end-game sequence by notifying all listeners that the match has concluded.
     * It generates a comprehensive leaderboard DTO containing the final scores and rankings
     * to be displayed to all participants.
     */
    public void notifyEndGame() {
        LeaderboardDTO leaderboardDTO = createLeaderboardDTO();

        for (GameEventListener l : listeners) {
            l.onEndGame(leaderboardDTO);
        }
    }
    //endregion

    /**
     * Fills the lower row (numPlayers + 1 cards) and then the upper row
     * (numPlayers + 4 cards) during initial setup (rulebook p. 2, steps 4-5).
     * <p>
     * Event cards drawn for the lower row are redirected to the upper row
     * as required by the rules.
     */
    private void setupInitialRows() {
        board.initializeLowerRow(numPlayers);
        board.fillUpperRow(numPlayers);
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

        resolveSortedEvents(events);
    }

    /**
     * Processes a list of event cards by sorting them according to priority and executing their effects.
     * <p>
     * The sorting logic prioritizes event effects that are not instances of {@code Sustenance}
     * (moving them to the front of the execution queue), and then breaks ties by sorting chronologically
     * based on the card's era. After sorting, each event is triggered sequentially for all players,
     * and an update notification is sent out for each player's tribe.
     * </p>
     *
     * @param events the list of {@link EventCard} instances to be sorted and resolved
     */
    private void resolveSortedEvents(List<EventCard> events) {
        List<EventCard> sortedEvents = events.stream().sorted(Comparator.comparingInt((EventCard card) ->
                                                                card.getEventEffect() instanceof Sustenance ? 1 : 0)
                                                                .thenComparingInt(EventCard::getEra)).toList();

        sortedEvents.forEach(card -> card.raiseEvent(players, this));

        players.forEach(p -> notifyShowTribe(p.getNickname()));
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

        notifyOnShowBoard();
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

        resolveSortedEvents(events);
    }

    /**
     * Adds end-of-game prestige points for each player (rulebook p. 7):
     *  - Builder cards PP
     *  - Inventor PP (numInventors x numDistinctInventionIcons)
     *  - 10 PP per every 2 Artist cards
     *  - Building card PP (printed + end-game effects)
     * <p>
     */
    private void computeFinalScores() {
        for (Player p : players) {
            p.changePrestigePoints(p.getTribe().getTotalScoringBuildingsPoints());
            p.changePrestigePoints((p.getTribe().getArtistsCount() / 2) * 10);
            p.changePrestigePoints(p.getTribe().getInventorsCount() * p.getTribe().totalDifferentInventorIcon());
            p.changePrestigePoints(p.getTribe().totalBuildersPoints());
        }
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
     * Returns the current player index
     *
     * @return current player index
     */
    public int getCurrentPlayerIndex() {
        return this.currentPlayerIndex;
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

    /**
     * Returns the player whose turn it is currently.
     */
    public Player getCurrentActivePlayer() {

        if(roundTurnOrder == null || currentPlayerIndex >= roundTurnOrder.size()) {
            return null;
        }
        return roundTurnOrder.get(currentPlayerIndex);
    }

    public List<Player> getRoundTurnOrder() {
        return Collections.unmodifiableList(roundTurnOrder);
    }

    public List<List<BuildingCard>> getEraBuildingDecks() {
        List<List<BuildingCard>> copy = new ArrayList<>();
        for (List<BuildingCard> deck : eraBuildingDecks) {
            copy.add(new ArrayList<>(deck));
        }
        return copy;
    }

    public String getCurrentStateName() {
        return currentState.getClass().getSimpleName();
    }

    public Optional<ActionResolutionState> getActionResolutionState() {
        if (currentState instanceof ActionResolutionState actionResolutionState) {
            return Optional.of(actionResolutionState);
        }
        return Optional.empty();
    }
    //endregion

    /**
     * Initializes and populates the turn order tile structure based on the number of players.
     * <p>
     * This method uses a factory to create the appropriate turn order track for the given
     * player count, and then registers each player in the game by placing their corresponding
     * totem onto the newly created track.
     * </p>
     *
     * @param numPlayers the total number of players participating in the game
     */
    private void createTurnOrderTile(int numPlayers){

        this.turnOrderTile = TurnOrderFactory.createTrack(numPlayers);
        for (Player p: players)
            this.turnOrderTile.placeTotem(p);
    }

    /**
     * Advances the active turn to the next player in alphabetical or turn sequence order.
     * <p>
     * This method increments the active player index, using a modulo operation based on the
     * total player count to safely loop back to the first player (index 0) once a full cycle
     * of turns is completed.
     * </p>
     */
    public void advanceTurn() {
        this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.players.size();
    }

    /**
     * Entry point for totem placement. Delegates logic to the current state.
     */
    public void placePlayerTotem(int tileIndex) {
        currentState.placeTotem(this, getCurrentActivePlayer(), tileIndex);
    }

    /**
     * Advances the game to the next round, handling both mid-game transitions and end-game triggers.
     * <p>
     * This method starts by checking for any extra card choices and incrementing the round counter.
     * It resets the active player index to zero and triggers the end-of-round cleanup.
     * </p>
     * <p>
     * If the maximum number of rounds ({@code TOTAL_ROUNDS}) has been exceeded, it processes any
     * remaining events, calculates final scores, switches the game to {@link EndGameState}, and
     * triggers the end-game notifications. Otherwise, it transitions the game to the
     * {@link TotemPlacementState} and notifies listeners about the updated board track and placement turn.
     * </p>
     */
    public void nextRound() {
        checkExtraCardChoose();

        this.currentRound++;
        this.currentPlayerIndex = 0;

        endOfRound(); // clean boards and events

        if (this.currentRound > TOTAL_ROUNDS) {
            resolveRemainingEvents();
            computeFinalScores();

            this.setState(new EndGameState());
            notifyEndGame();
        } else {
            this.setState(new TotemPlacementState());
            notifyOnShowOfferTrack();
            notifyTotemPlacementTurnChanged();
        }
    }

    /**
     * Scans the turn order of the current round to identify players entitled to an extra card
     * choice (e.g., due to a specific tribe ability or power).
     * <p>
     * If a player with the 'extraCardFromUpper' flag is found, the current player index
     * is updated to point to them, and a notification is sent to the observers to
     * signal the turn change and update the UI/Client state.
     * </p>
     */
    private void checkExtraCardChoose() {
        for (int i = 0; i < roundTurnOrder.size(); i++) {
            if (roundTurnOrder.get(i).getTribe().getExtraCardFromUpper()) {
                this.currentPlayerIndex = i;
                this.getCurrentActivePlayer().setUpperPick(1);
                notifyActionResultTurnChanged();
            }
        }
    }

    /**
     * Resolves the action of the current player picking a card from the upper row.
     * <p>
     * This method delegates the specific pick logic and subsequent state transitions
     * to the active {@link GameState}.
     * </p>
     *
     * @param pos the zero-based index or position of the selected card on the upper board row
     */
    public void resolveUpperCardPlayerPick(int pos) {
        currentState.resolveUpperCardPick(this, getCurrentActivePlayer(), pos);
    }

    /**
     * Resolves the action of the current player picking a card from the lower row.
     * <p>
     * This method delegates the specific pick logic and subsequent state transitions
     * to the active {@link GameState}.
     * </p>
     *
     * @param pos the zero-based index or position of the selected card on the lower board row
     */
    public void resolveLowerCardPlayerPick(int pos) {
        currentState.resolveLowerCardPick(this, getCurrentActivePlayer(), pos);
    }

    /**
     * Resolves the action of the current player purchasing or picking a building card from the upper row.
     * <p>
     * This method delegates the resource deduction, card acquisition, and state transitions
     * to the active {@link GameState}.
     * </p>
     *
     * @param pos the zero-based index or position of the selected building card on the upper board row
     */
    public void resolveUpperBuildingPlayerPick(int pos) {
        currentState.resolveUpperBuildingPick(this, getCurrentActivePlayer(), pos);
    }

    /**
     * Resolves the action of the current player purchasing or picking a building card from the lower row.
     * <p>
     * This method delegates the resource deduction, card acquisition, and state transitions
     * to the active {@link GameState}.
     * </p>
     *
     * @param pos the zero-based index or position of the selected building card on the lower board row
     */
    public void resolveLowerBuildingPlayerPick(int pos) {
        currentState.resolveLowerBuildingPick(this, getCurrentActivePlayer(), pos);
    }

    /**
     * Updates the current state of the game engine.
     * <p>
     * This method is used to dynamically transition the game between different phases
     * or turns by replacing the active {@link GameState} instance.
     * </p>
     *
     * @param newState the new {@link GameState} instance to be applied
     */
    public void setState(GameState newState){
        this.currentState = newState;
    }

    /**
     * Actually performs the totem placement on the board data.
     * Called by TotemPlacementState after validation.
     */
    public void executeTotemPlacement(Player player, int tileIndex) {
        OfferTile chosen = offerTrack.getTiles().get(tileIndex);
        chosen.placeTotem(player);
        turnOrderTile.cleanTurnOrderSlot(player);
    }

    /**
     * Actually performs the upper card acquisition.
     * Called by ActionResolutionState after validation.
     */
    public void executeUpperCardPick(Player player, int pos) {
        TribeDeck c = this.board.takeCardFromTopRow(pos);
        c.applyTo(player);
    }

    /**
     * Actually performs the lower card acquisition.
     */
    public void executeLowerCardPick(Player player, int pos) {
        TribeDeck c = this.board.takeCardFromBottomRow(pos);
        c.applyTo(player);
    }

    /**
     * Actually performs the upper building acquisition.
     */
    public void executeUpperBuildingPick(Player player, int pos) {
        BuildingCard c = this.board.takeCardFromUpperBuildingRow(pos);
        c.applyTo(player);
    }

    /**
     * Actually performs the lower building acquisition.
     */
    public void executeLowerBuildingPick(Player player, int pos) {
        BuildingCard c = this.board.takeCardFromLowerBuildingRow(pos);
        c.applyTo(player);
    }

    /**
     * Updates the turn order for the current phase.
     * @param newOrder the list of players in the order they must act.
     */
    public void setTurnOrder(List<Player> newOrder) {
        this.roundTurnOrder = new ArrayList<>(newOrder);
        this.currentPlayerIndex = 0;
    }

    /**
     * Retrieves the turn order tile currently assigned to this entity.
     *
     * @return the {@link TurnOrderTile} representing the player's position or advantages
     *         in the turn sequence
     */
    public TurnOrderTile getTurnOrderTile() {
        return this.turnOrderTile;
    }

    /**
     * Resolves the end-of-turn logic for the current active player based on the current game state.
     * <p>
     * This method delegates the turn-ending logic to the active {@link GameState} implementation,
     * passing the current game instance and the active player to process state transitions,
     * resource cleanups, or next-player determinations.
     * </p>
     */
    public void resolveEndTurn() {
        this.currentState.endTurn(this, this.getCurrentActivePlayer());
    }

    /**
     * Returns the players ranked from first to last place.
     * Ranking criteria:
     * 1. Highest Prestige Points
     * 2. Tiebreaker: Most food
     *
     * @return list of players sorted by rank (descending)
     */
    public List<Player> getLeaderboard() {
        return players.stream()
                .sorted(Comparator
                        .comparingInt(Player::getPrestigePoints).reversed()
                        .thenComparing(Comparator.comparingInt(Player::getFoodAmount).reversed())
                )
                .collect(Collectors.toList());
    }

    /**
     * Creates and populates a {@link LeaderboardDTO} based on the current player rankings.
     * <p>
     * This method retrieves the sorted list of players and assigns their positions.
     * If multiple players share the exact same amount of prestige points and food,
     * they are assigned the same rank (tie-handling). It also determines if there is
     * a tie for the first place (multiple winners).
     * </p>
     *
     * @return a {@code LeaderboardDTO} containing the list of ranked players and a flag
     *         indicating if there are multiple winners
     */
    public LeaderboardDTO createLeaderboardDTO() {
        List<Player> sortedPlayers = getLeaderboard();
        List<PlayerRankDTO> elements = new ArrayList<>();

        for (int i = 0; i < sortedPlayers.size(); i++) {
            Player p = sortedPlayers.get(i);

            int actualPosition = i + 1;
            if (i > 0) {
                Player previous = sortedPlayers.get(i - 1);
                if (p.getPrestigePoints() == previous.getPrestigePoints() &&
                        p.getFoodAmount() == previous.getFoodAmount()) {
                    actualPosition = elements.get(i - 1).getPosition();
                }
            }

            elements.add(new PlayerRankDTO(
                    p.getNickname(),
                    p.getPrestigePoints(),
                    p.getFoodAmount(),
                    actualPosition,
                    actualPosition == 1 // Winner
            ));
        }

        long winnerCount = elements.stream().filter(PlayerRankDTO::isWinner).count();
        return new LeaderboardDTO(elements, winnerCount > 1);
    }

    /**
     * Updates the game status by transitioning the current state to the end-game state.
     * <p>
     * This method instantiates and assigns a new {@link EndGameState} to the game's state machine.
     * </p>
     */
    public void setEndGameStatus() {
        this.currentState = new EndGameState();
    }

    /**
     * Retrieves the total number of upper deck cards that are currently available for purchase or selection.
     *
     * @return the count of obtainable cards in the board's top row as an {@code int}
     */
    private int getCurrenObtainableUpperCard() {
        return Math.toIntExact(this.board.getTopRow().stream().filter(TribeDeck::getIsObtainable).count());
    }

    /**
     * Retrieves the total number of lower deck cards that are currently available for purchase or selection.
     *
     * @return the count of obtainable cards in the board's bottom row as an {@code int}
     */
    private int getCurrenObtainableLowerCard() {
        return Math.toIntExact(this.board.getBottomRow().stream().filter(TribeDeck::getIsObtainable).count());
    }

    /**
     * Checks whether the current active player has valid remaining choices to make on the board.
     * <p>
     * The player can make a choice if they have at least one lower pick available while lower cards
     * are obtainable, or if they have at least one upper pick available while upper cards are obtainable.
     * </p>
     *
     * @return {@code true} if the current player has available picks and there are corresponding
     *         obtainable cards on the board; {@code false} otherwise
     */
    private boolean checkIfCurrentPlayerCanChoose() {
        return (this.getCurrentActivePlayer().getLowerPick() > 0 && getCurrenObtainableLowerCard() != 0)
                || (this.getCurrentActivePlayer().getUpperPick() > 0 && getCurrenObtainableUpperCard() != 0);
    }

    /**
     * Checks whether the current active player has enough resources to purchase at least one
     * available building card from the board.
     * <p>
     * The method factors in the player's available picks (upper or lower), their current food stock,
     * and any food discounts provided by their tribe's builders.
     * </p>
     *
     * @return {@code true} if the player has an upper or lower pick available and can afford at
     *         least one building card in that row; {@code false} otherwise
     */
    private boolean checkIfCurrentPlayerCanBuyBuilding() {
        Player player = this.getCurrentActivePlayer();
        int foodDiscount = player.getTribe().totalBuildersFoodDiscount();

        if (player.getUpperPick() > 0) {
            if (this.getBoard().getUpperBuildingCards().stream().anyMatch(b -> b.getFoodCost() <= player.getFoodAmount() + foodDiscount)) {
                return true;
            }
        }

        if (player.getLowerPick() > 0) {
            return this.getBoard().getLowerBuildingCards().stream().anyMatch(b -> b.getFoodCost() <= player.getFoodAmount() + foodDiscount);
        }

        return false;
    }
}
