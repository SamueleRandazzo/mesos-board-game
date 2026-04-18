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
import java.util.ArrayList;
import java.util.List;

public class GameDataLoader {

    private final ObjectMapper objectMapper;
    private final CardFactory cardFactory;

    public GameDataLoader() {
        this.objectMapper = new ObjectMapper();
        this.cardFactory = new CardFactory();
    }

    public List<TribeDeck> loadDecks() {
        List<RawTribeCardData> tribeCards = readJsonList(
                "/cards/tribe_cards.json",
                new TypeReference<List<RawTribeCardData>>() {}
        );

        List<RawEventCardData> eventCards = readJsonList(
                "/cards/event_cards.json",
                new TypeReference<List<RawEventCardData>>() {}
        );

        List<TribeDeck> deck = new ArrayList<>();

        for (RawTribeCardData data : tribeCards) {
            deck.add(cardFactory.createTribeCard(data));
        }

        for (RawEventCardData data : eventCards) {
            deck.add(cardFactory.createEventCard(data));
        }

        return deck;
    }

    public List<BuildingCard> loadBuildings(int era) {
        String path;
        switch (era) {
            case 1:
                path = "/cards/buildings_era1.json";
                break;
            case 2:
                path = "/cards/buildings_era2.json";
                break;
            case 3:
                path = "/cards/buildings_era3.json";
                break;
            default:
                throw new IllegalArgumentException("Invalid era: " + era);
        }

        List<RawBuildingCardData> rawBuildings = readJsonList(
                path,
                new TypeReference<List<RawBuildingCardData>>() {}
        );

        List<BuildingCard> buildings = new ArrayList<>();
        for (RawBuildingCardData data : rawBuildings) {
            buildings.add(cardFactory.createBuildingCard(data));
        }

        return buildings;
    }

    public OfferTrack loadOfferTrack(int numPlayers) {

        List<OfferTile> tiles = new ArrayList<>();

        // CONFIG BASE (puoi cambiarla dopo)
        tiles.add(new OfferTile(0, TileId.A, 1, 0));
        tiles.add(new OfferTile(1, TileId.B, 0, 1));
        tiles.add(new OfferTile(2, TileId.C, 1, 1));
        tiles.add(new OfferTile(3, TileId.D, 2, 0));
        tiles.add(new OfferTile(4, TileId.E, 0, 2));

        // se vuoi puoi adattare in base ai player
        if (numPlayers <= 2) {
            return new OfferTrack(tiles.subList(0, 3));
        }

        return new OfferTrack(tiles);
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