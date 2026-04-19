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

public class GameDataLoader {

    private final ObjectMapper objectMapper;
    private final CardFactory cardFactory;

    private static final int[][] CONFIG_TABLE = {
            {1, 2, 3}, // 2 Players
            {2, 2, 4}, // 3 Players
            {2, 3, 4}, // 4 Players
            {2, 3, 5}  // 5 Players
    };

    public GameDataLoader() {
        this.objectMapper = new ObjectMapper();
        this.cardFactory = new CardFactory();
    }

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