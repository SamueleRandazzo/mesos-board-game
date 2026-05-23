package it.polimi.ingsw.model.factories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.Board.OfferTile;
import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Enum.TileId;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles the loading, parsing, and assembly of game components from JSON resource files.
 * <p>
 * This class serves as the primary data loader for the game engine. It utilizes the Jackson
 * {@link ObjectMapper} to read structured card definition templates and delegates instantiation
 * to a centralized {@link CardFactory}.
 * </p>
 * <p>
 * It enforces structural game rules during setup, such as:
 * </p>
 * <ul>
 *   <li>Filtering out cards that do not match the minimum player count requirements.</li>
 *   <li>Shuffling and grouping character and event decks dynamically by their corresponding Era.</li>
 *   <li>Enforcing building card pool limitations across Eras and player counts via a configuration lookup table.</li>
 *   <li>Injecting game-ending final events at the bottom of the main gameplay stack.</li>
 * </ul>
 *
 * @see CardFactory
 * @see TribeDeck
 * @see BuildingCard
 */
public class GameDataLoader {

    private final ObjectMapper objectMapper;
    private final CardFactory cardFactory;

    /**
     * Configuration table mapping player counts and game eras to specific building distribution counts.
     * <p>
     * Rows represent total players (indexed from 0 to 3, corresponding to 2, 3, 4, or 5 players).
     * Columns represent the game Eras (indexed from 0 to 2, corresponding to Era 1, Era 2, or Era 3).
     * </p>
     * For example, {@code CONFIG_TABLE[0][2]} (2 players, Era 3) yields exactly 3 buildings.
     */
    private static final int[][] CONFIG_TABLE = {
            {1, 2, 3}, // 2 Players
            {2, 2, 4}, // 3 Players
            {2, 3, 4}, // 4 Players
            {2, 3, 5}  // 5 Players
    };

    /**
     * Constructs a new GameDataLoader and initializes its internal Jackson JSON mapper
     * and specialized card factory instance.
     */
    public GameDataLoader() {
        this.objectMapper = new ObjectMapper();
        this.cardFactory = new CardFactory();
    }

    /**
     * Loads, shuffles, and constructs the main game deck consisting of character and non-final event cards,
     * capped off with a randomized selection of endgame final events.
     * <p>
     * The loading workflow strictly adheres to chronological game requirements:
     * </p>
     * <ol>
     *   <li>Filters out raw card definitions failing the {@code minPlayers} restriction.</li>
     *   <li>Separates and shuffles cards internally within Era 1, Era 2, and Era 3 respectively to preserve chronological escalation.</li>
     *   <li>Extracts specialized final event cards assigned to Era 3, shuffles them, and appends a maximum of 2 to the absolute bottom of the composite stack.</li>
     * </ol>
     *
     * @param playersNum the total number of players participating in the current session
     * @return a fully prepared, chronologically stacked {@link List} of {@link TribeDeck} elements ready for gameplay
     */
    public List<TribeDeck> loadDecks(int playersNum) {
        List<RawTribeCardData> rawTribes = readJsonList("/cards/tribe_cards.json", new TypeReference<>() {});
        List<RawEventCardData> rawEvents = readJsonList("/cards/event_cards.json", new TypeReference<>() {});

        List<TribeDeck> allNormalCards = new ArrayList<>();

        rawTribes.stream()
                .filter(d -> d.minPlayers <= playersNum)
                .map(cardFactory::createTribeCard)
                .forEach(allNormalCards::add);

        rawEvents.stream()
                .filter(d -> d.minPlayers <= playersNum && !d.isFinal)
                .map(cardFactory::createEventCard)
                .forEach(allNormalCards::add);

        Map<Integer, List<TribeDeck>> cardsByEra = allNormalCards.stream()
                .collect(Collectors.groupingBy(TribeDeck::getEra));

        List<TribeDeck> finalDeck = new ArrayList<>();

        for (int era = 1; era <= 3; era++) {
            List<TribeDeck> eraCards = cardsByEra.getOrDefault(era, new ArrayList<>());
            Collections.shuffle(eraCards);
            finalDeck.addAll(eraCards);
        }

        List<TribeDeck> finalEvents = rawEvents.stream()
                .filter(d -> d.isFinal && d.era == 3)
                .map(cardFactory::createEventCard)
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(finalEvents);
        finalDeck.addAll(finalEvents.subList(0, Math.min(2, finalEvents.size())));

        return finalDeck;
    }

    /**
     * Generates a structural dictionary mapping every player-accessible main deck card ID
     * to its fully constructed domain model representation.
     * <p>
     * This method bypasses shuffling and deck partitioning logic. It is primarily used during
     * game setup, testing, or state restoration to map raw action logs or network packages back to concrete entities.
     * </p>
     *
     * @param playersNum the total number of players participating in the current session
     * @return a {@link Map} linking unique card ID string values to their corresponding {@link TribeDeck} object instances
     */
    public Map<String, TribeDeck> loadAllTribeCardsById(int playersNum) {
        List<RawTribeCardData> rawTribes = readJsonList("/cards/tribe_cards.json", new TypeReference<>() {});
        List<RawEventCardData> rawEvents = readJsonList("/cards/event_cards.json", new TypeReference<>() {});

        Map<String, TribeDeck> cardsById = new HashMap<>();

        rawTribes.stream()
                .filter(d -> d.minPlayers <= playersNum)
                .map(cardFactory::createTribeCard)
                .forEach(card -> cardsById.put(card.getId(), card));

        rawEvents.stream()
                .filter(d -> d.minPlayers <= playersNum)
                .map(cardFactory::createEventCard)
                .forEach(card -> cardsById.put(card.getId(), card));

        return cardsById;
    }

    /**
     * Extracts a randomized pool of building cards matching a targeted era and tailored to the
     * specific sizing demands of the current player configuration.
     * <p>
     * The total number of cards returned is strictly looked up from {@link #CONFIG_TABLE}.
     * </p>
     *
     * @param era        the target historical era (must be between 1 and 3 inclusive)
     * @param playersNum the total number of active players (must be between 2 and 5 inclusive)
     * @return a randomly shuffled, bounded {@link List} containing the designated number of {@link BuildingCard}s
     * @throws IllegalArgumentException if the provided era or player arguments violate system boundaries
     */
    public List<BuildingCard> loadBuildings(int era, int playersNum) {
        if (playersNum < 2 || playersNum > 5 || era < 1 || era > 3) {
            throw new IllegalArgumentException("Parameters out of range");
        }

        String path = String.format("/cards/buildings_era%d.json", era);

        List<RawBuildingCardData> rawBuildings = readJsonList(
                path,
                new TypeReference<List<RawBuildingCardData>>() {}
        );

        int countToTake = CONFIG_TABLE[playersNum - 2][era - 1];

        Collections.shuffle(rawBuildings);

        return rawBuildings.stream()
                .limit(countToTake)
                .map(cardFactory::createBuildingCard)
                .collect(Collectors.toList());
    }

    /**
     * Compiles an exhaustive map of all historical structural building cards across all eras
     * that satisfy the current game setup's player boundaries.
     * <p>
     * Iterates through all era templates sequentially, indexing every eligible building entity by its ID string.
     * </p>
     *
     * @param playersNum the total number of players participating in the current session (must be between 2 and 5 inclusive)
     * @return a comprehensive {@link Map} linking building ID strings to their fully populated {@link BuildingCard} instances
     * @throws IllegalArgumentException if the provided player value falls outside allowable bounds
     */
    public Map<String, BuildingCard> loadAllBuildingCardsById(int playersNum) {
        if (playersNum < 2 || playersNum > 5) {
            throw new IllegalArgumentException("Parameters out of range");
        }

        Map<String, BuildingCard> cardsById = new HashMap<>();

        for (int era = 1; era <= 3; era++) {
            String path = String.format("/cards/buildings_era%d.json", era);
            List<RawBuildingCardData> rawBuildings = readJsonList(
                    path,
                    new TypeReference<List<RawBuildingCardData>>() {}
            );

            rawBuildings.stream()
                    .filter(d -> d.minPlayers <= playersNum)
                    .map(cardFactory::createBuildingCard)
                    .forEach(card -> cardsById.put(card.getId(), card));
        }

        return cardsById;
    }

    /**
     * Assembles and wraps an operational shared {@link OfferTrack} layout populated with static,
     * hardcoded trade or resource configurations.
     * <p>
     * Tiles whose structural minimum player limits exceed the actual player roster size are automatically excluded.
     * </p>
     *
     * @param numPlayers the total number of players participating in the current session
     * @return a new, isolated {@link OfferTrack} state instance containing valid tiles for the specified player count
     */
    public OfferTrack loadOfferTrack(int numPlayers) {
        List<OfferTile> allTiles = List.of(
                new OfferTile(3, TileId.A, 0, 0, 5),
                new OfferTile(0, TileId.B, 0, 1, 2),
                new OfferTile(0, TileId.C, 1, 0, 2),
                new OfferTile(0, TileId.D, 0, 2, 3),
                new OfferTile(0, TileId.E, 1, 1, 2),
                new OfferTile(0, TileId.F, 2, 0, 2),
                new OfferTile(0, TileId.G, 2, 1, 4)
        );

        List<OfferTile> filteredTiles = allTiles.stream()
                .filter(tile -> tile.getMinPlayers() <= numPlayers)
                .collect(Collectors.toList());

        return new OfferTrack(filteredTiles);
    }

    /**
     * Performs reflective path resolution and handles raw input stream extraction to process target JSON objects.
     *
     * @param <T>           the structural target parameter type used for mapping collection nodes
     * @param resourcePath  the classpath-absolute location pointing to the desired JSON asset file
     * @param typeReference Jackson type wrapper used to confidently retain deep generic metadata throughout parsing
     * @return a fully populated Java collection or object matching structure specifications
     * @throws IllegalArgumentException if the designated resource path resolves to a null stream
     * @throws RuntimeException         if an accidental file locking issue, underlying stream crash, or malformed JSON payload occurs
     */
    private <T> T readJsonList(String resourcePath, TypeReference<T> typeReference) {
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            return objectMapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON resource: " + resourcePath, e);
        }
    }
}