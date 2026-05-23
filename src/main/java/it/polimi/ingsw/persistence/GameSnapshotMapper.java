package it.polimi.ingsw.persistence;

import it.polimi.ingsw.model.Board.Board;
import it.polimi.ingsw.model.Board.OfferTile;
import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.Board.TurnOrderSlot;
import it.polimi.ingsw.model.Board.TurnOrderTile;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Enum.TileId;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.factories.GameDataLoader;
import it.polimi.ingsw.model.states.ActionResolutionState;
import it.polimi.ingsw.model.states.EndGameState;
import it.polimi.ingsw.model.states.GameState;
import it.polimi.ingsw.model.states.SetupGameState;
import it.polimi.ingsw.model.states.TotemPlacementState;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Data Mapper responsible for bidirectional conversions between the live runtime {@link Game}
 * domain model and its serializable, detached {@link GameSnapshot} representation.
 * <p>
 * This coordinator isolates the core game state machine from data persistence schemas,
 * implementing full flattening routines during serialization and deterministic pointer
 * re-linking (re-hydrating player mappings and factory lookups) during deserialization.
 * </p>
 *
 * @see Game
 * @see GameSnapshot
 * @see GameDataLoader
 */
public class GameSnapshotMapper {

    /**
     * Extracts and flattens the structural runtime state of a live {@link Game} engine into a
     * clean, deep-copied, serializable {@link GameSnapshot} architecture.
     * <p>
     * This mapping captures structural variables, operational track layers, dynamic state records,
     * and maps complex cross-references into flat alphanumeric ID tokens.
     * </p>
     *
     * @param game the active {@link Game} engine instance acting as the mapping source
     * @return a fully populated, serializable {@link GameSnapshot} data container
     */
    public GameSnapshot toSnapshot(Game game) {
        GameSnapshot snapshot = new GameSnapshot();
        snapshot.numPlayers = game.getNumPlayers();
        snapshot.currentEra = game.getCurrentEra();
        snapshot.currentRound = game.getCurrentRound();
        snapshot.currentPlayerIndex = game.getCurrentPlayerIndex();
        snapshot.currentStateName = game.getCurrentStateName();

        for (Player player : game.getPlayers()) {
            snapshot.players.add(toPlayerSnapshot(player));
        }

        snapshot.board = toBoardSnapshot(game.getBoard());
        snapshot.offerTrack = toOfferTrackSnapshot(game.getOfferTrack());
        snapshot.turnOrderTile = toTurnOrderSnapshot(game.getTurnOrderTile());
        snapshot.roundTurnOrder = game.getRoundTurnOrder().stream()
                .map(Player::getNickname)
                .toList();
        snapshot.eraBuildingDecks = game.getEraBuildingDecks().stream()
                .map(this::buildingIds)
                .toList();
        snapshot.actionResolutionState = game.getActionResolutionState()
                .map(this::toActionResolutionSnapshot)
                .orElse(null);

        return snapshot;
    }

    /**
     * Reconstructs and fully re-hydrates a live, interactive {@link Game} engine session
     * from a detached, serialized {@link GameSnapshot} schema template.
     * <p>
     * This method orchestrates resource boots using the {@link GameDataLoader} to bind
     * static card definitions by ID, enforces transactional sanity checks on user structures,
     * and reconstructs precise reference points across player arrays, tracks, and finite state handlers.
     * </p>
     *
     * @param snapshot the flat {@link GameSnapshot} file template acting as the data source
     * @return a live, executable {@link Game} engine session matching the saved footprint
     * @throws IllegalArgumentException if an asset ID is unknown, an unowned card structure is corrupted,
     *                                  or if an unmapped game state label is intercepted
     */
    public Game toGame(GameSnapshot snapshot) {
        GameDataLoader loader = new GameDataLoader();
        Map<String, TribeDeck> tribeCards = loader.loadAllTribeCardsById(snapshot.numPlayers);
        Map<String, BuildingCard> buildingCards = loader.loadAllBuildingCardsById(snapshot.numPlayers);

        List<Player> players = new ArrayList<>();
        Map<String, Player> playersByNickname = new LinkedHashMap<>();

        for (PlayerSnapshot playerSnapshot : snapshot.players) {
            Player player = new Player(Color.valueOf(playerSnapshot.color), playerSnapshot.nickname);
            restorePlayerCards(player, playerSnapshot.ownedCardIds, tribeCards, buildingCards);
            player.setFoodAmount(playerSnapshot.foodAmount);
            player.setPrestigePoints(playerSnapshot.prestigePoints);
            players.add(player);
            playersByNickname.put(player.getNickname(), player);
            player.setUpperPick(playerSnapshot.upperPick);
            player.setLowerPick(playerSnapshot.lowerPick);
        }

        Board board = restoreBoard(snapshot.board, tribeCards, buildingCards);
        OfferTrack offerTrack = restoreOfferTrack(snapshot.offerTrack, playersByNickname);
        TurnOrderTile turnOrderTile = restoreTurnOrderTile(snapshot.turnOrderTile, playersByNickname);
        List<List<BuildingCard>> eraBuildingDecks = snapshot.eraBuildingDecks.stream()
                .map(ids -> resolveIds(ids, buildingCards, "building"))
                .toList();
        List<Player> roundTurnOrder = resolvePlayers(snapshot.roundTurnOrder, playersByNickname);
        GameState state = restoreState(snapshot, playersByNickname);

        return new Game(
                players,
                board,
                offerTrack,
                turnOrderTile,
                eraBuildingDecks,
                snapshot.currentEra,
                snapshot.currentRound,
                snapshot.currentPlayerIndex,
                roundTurnOrder,
                state
        );
    }

    /**
     * Converts a single live player object into a flat, detached data record block.
     */
    private PlayerSnapshot toPlayerSnapshot(Player player) {
        PlayerSnapshot snapshot = new PlayerSnapshot();
        snapshot.nickname = player.getNickname();
        snapshot.color = player.getColor().name();
        snapshot.foodAmount = player.getFoodAmount();
        snapshot.prestigePoints = player.getPrestigePoints();
        snapshot.ownedCardIds = player.getTribe().getOwnedCards().stream()
                .map(Card::getId)
                .toList();
        snapshot.upperPick = player.getUpperPick();
        snapshot.lowerPick = player.getLowerPick();
        return snapshot;
    }

    /**
     * Extracts card blueprints from the central game board slots and reduces them to text coordinate lines.
     */
    private BoardSnapshot toBoardSnapshot(Board board) {
        BoardSnapshot snapshot = new BoardSnapshot();
        snapshot.upperTribeCards = tribeIds(board.getTopRow());
        snapshot.lowerTribeCards = tribeIds(board.getBottomRow());
        snapshot.upperBuildingCards = buildingIds(board.getUpperBuildingCards());
        snapshot.lowerBuildingCards = buildingIds(board.getLowerBuildingCards());
        snapshot.tribeDeck = tribeIds(board.getTribeDeck());
        snapshot.buildingDeck = buildingIds(board.getBuildingDeck());
        return snapshot;
    }

    /**
     * Records the grid properties and player allocation hooks of the current offer/totem selection track.
     */
    private OfferTrackSnapshot toOfferTrackSnapshot(OfferTrack offerTrack) {
        OfferTrackSnapshot snapshot = new OfferTrackSnapshot();
        for (OfferTile tile : offerTrack.getTiles()) {
            OfferTileSnapshot tileSnapshot = new OfferTileSnapshot();
            tileSnapshot.foodBonus = tile.getFoodBonus();
            tileSnapshot.tileId = tile.getTileId().name();
            tileSnapshot.topRowDraws = tile.getTopRowDraws();
            tileSnapshot.bottomRowDraws = tile.getBottomRowDraws();
            tileSnapshot.minPlayers = tile.getMinPlayers();
            tileSnapshot.placedPlayerNickname = tile.getPlacedPlayer() == null
                    ? null
                    : tile.getPlacedPlayer().getNickname();
            snapshot.tiles.add(tileSnapshot);
        }
        return snapshot;
    }

    /**
     * Captures player positioning maps and bonus matrices from the core turn sequence tracks.
     */
    private TurnOrderSnapshot toTurnOrderSnapshot(TurnOrderTile turnOrderTile) {
        TurnOrderSnapshot snapshot = new TurnOrderSnapshot();
        for (TurnOrderSlot slot : turnOrderTile.getSlots()) {
            TurnOrderSlotSnapshot slotSnapshot = new TurnOrderSlotSnapshot();
            slotSnapshot.foodModifier = slot.getFoodModifier();
            slotSnapshot.occupyingPlayerNickname = slot.getOccupyingPlayer()
                    .map(Player::getNickname)
                    .orElse(null);
            snapshot.slots.add(slotSnapshot);
        }
        return snapshot;
    }

    /**
     * Isolates and saves operational context tracking values from an active action resolution sub-phase loop.
     */
    private ActionResolutionSnapshot toActionResolutionSnapshot(ActionResolutionState state) {
        ActionResolutionSnapshot snapshot = new ActionResolutionSnapshot();
        snapshot.currentActivePlayerNickname = state.getCurrentActivePlayerForState() == null
                ? null
                : state.getCurrentActivePlayerForState().getNickname();
        snapshot.extraCardChoose = state.isExtraCardChoose();
        return snapshot;
    }

    /**
     * Re-applies and registers card instances back into a player's hand or tribe tableau records,
     * validating structural card type criteria.
     */
    private void restorePlayerCards(Player player,
                                    List<String> ownedCardIds,
                                    Map<String, TribeDeck> tribeCards,
                                    Map<String, BuildingCard> buildingCards) {
        for (String cardId : ownedCardIds) {
            if (tribeCards.containsKey(cardId)) {
                TribeDeck card = tribeCards.get(cardId);
                if (!card.getIsObtainable()) {
                    throw new IllegalArgumentException("Event card cannot be restored as owned card: " + cardId);
                }
                card.applyTo(player);
            } else if (buildingCards.containsKey(cardId)) {
                buildingCards.get(cardId).applyTo(player);
            } else {
                throw new IllegalArgumentException("Unknown owned card id: " + cardId);
            }
        }
    }

    /**
     * Rebuilds the central common Board engine container by resolving saved textual identifiers back into structural card lists.
     */
    private Board restoreBoard(BoardSnapshot snapshot,
                               Map<String, TribeDeck> tribeCards,
                               Map<String, BuildingCard> buildingCards) {
        return new Board(
                resolveIds(snapshot.tribeDeck, tribeCards, "tribe"),
                resolveIds(snapshot.buildingDeck, buildingCards, "building"),
                resolveIds(snapshot.upperTribeCards, tribeCards, "tribe"),
                resolveIds(snapshot.lowerTribeCards, tribeCards, "tribe"),
                resolveIds(snapshot.upperBuildingCards, buildingCards, "building"),
                resolveIds(snapshot.lowerBuildingCards, buildingCards, "building")
        );
    }

    /**
     * Restores totem grid placement properties onto the newly mapped player reference parameters.
     */
    private OfferTrack restoreOfferTrack(OfferTrackSnapshot snapshot, Map<String, Player> playersByNickname) {
        List<OfferTile> tiles = new ArrayList<>();
        for (OfferTileSnapshot tileSnapshot : snapshot.tiles) {
            OfferTile tile = new OfferTile(
                    tileSnapshot.foodBonus,
                    TileId.valueOf(tileSnapshot.tileId),
                    tileSnapshot.topRowDraws,
                    tileSnapshot.bottomRowDraws,
                    tileSnapshot.minPlayers
            );
            if (tileSnapshot.placedPlayerNickname != null) {
                tile.restorePlacedPlayer(resolvePlayer(tileSnapshot.placedPlayerNickname, playersByNickname));
            }
            tiles.add(tile);
        }
        return new OfferTrack(tiles);
    }

    /**
     * Resolves and re-establishes structural occupant pointer parameters inside turn priority tracking blocks.
     */
    private TurnOrderTile restoreTurnOrderTile(TurnOrderSnapshot snapshot, Map<String, Player> playersByNickname) {
        List<TurnOrderSlot> slots = new ArrayList<>();
        for (TurnOrderSlotSnapshot slotSnapshot : snapshot.slots) {
            TurnOrderSlot slot = new TurnOrderSlot(slotSnapshot.foodModifier);
            if (slotSnapshot.occupyingPlayerNickname != null) {
                slot.occupy(resolvePlayer(slotSnapshot.occupyingPlayerNickname, playersByNickname));
            }
            slots.add(slot);
        }
        return new TurnOrderTile(slots);
    }

    /**
     * Instantiates and configures the exact behavioral State machine object context active when the snapshot was saved.
     */
    private GameState restoreState(GameSnapshot snapshot, Map<String, Player> playersByNickname) {
        return switch (snapshot.currentStateName) {
            case "SetupGameState" -> new SetupGameState();
            case "TotemPlacementState" -> new TotemPlacementState();
            case "ActionResolutionState" -> {
                ActionResolutionSnapshot state = snapshot.actionResolutionState;
                Player currentActivePlayer = state == null || state.currentActivePlayerNickname == null
                        ? null
                        : resolvePlayer(state.currentActivePlayerNickname, playersByNickname);
                yield new ActionResolutionState(
                        null,
                        currentActivePlayer,
                        state != null && state.extraCardChoose
                );
            }
            case "EndGameState" -> new EndGameState();
            default -> throw new IllegalArgumentException("Unknown game state: " + snapshot.currentStateName);
        };
    }

    /** Extracts standard text string labels from a generic collection of tribe-related card elements. */
    private List<String> tribeIds(List<TribeDeck> cards) {
        return cards.stream().map(TribeDeck::getId).toList();
    }

    /** Extracts standard text string labels from a generic collection of structural building cards. */
    private List<String> buildingIds(List<BuildingCard> cards) {
        return cards.stream().map(BuildingCard::getId).toList();
    }

    /** Resolves an ordered list of player profile names back into active player entity references. */
    private List<Player> resolvePlayers(List<String> nicknames, Map<String, Player> playersByNickname) {
        return nicknames.stream()
                .map(nickname -> resolvePlayer(nickname, playersByNickname))
                .toList();
    }

    /** Lookups and maps a player registration reference node using their unique nickname key. */
    private Player resolvePlayer(String nickname, Map<String, Player> playersByNickname) {
        Player player = playersByNickname.get(nickname);
        if (player == null) {
            throw new IllegalArgumentException("Unknown player nickname in save file: " + nickname);
        }
        return player;
    }

    /** Convenience overload targeting map-backed resource registries for resolving tokenized collections. */
    private <T> List<T> resolveIds(List<String> ids, Map<String, T> cardsById, String type) {
        return resolveIds(ids, cardsById::get, type);
    }

    /**
     * Evaluates flat text structural array collections, looking up and returning concrete
     * instance blocks using functional extractor tools.
     */
    private <T> List<T> resolveIds(List<String> ids, Function<String, T> resolver, String type) {
        List<T> cards = new ArrayList<>();
        for (String id : ids) {
            T card = resolver.apply(id);
            if (card == null) {
                throw new IllegalArgumentException("Unknown " + type + " card id in save file: " + id);
            }
            cards.add(card);
        }
        return cards;
    }
}