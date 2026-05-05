package it.polimi.ingsw.model.factories;

import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Cards.EventCard;
import it.polimi.ingsw.model.CharacterCards.Builder;
import it.polimi.ingsw.model.CharacterCards.Gatherer;
import it.polimi.ingsw.model.CharacterCards.Hunter;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameDataLoaderTest {

    @Test
    void loadDecks_shouldLoadTribeCardsFromJson() {
        GameDataLoader loader = new GameDataLoader();

        List<TribeDeck> deck = loader.loadDecks(2);

        assertFalse(deck.isEmpty());

        long hunterCount = deck.stream().filter(card -> card instanceof Hunter).count();
        long builderCount = deck.stream().filter(card -> card instanceof Builder).count();
        long gathererCount = deck.stream().filter(card -> card instanceof Gatherer).count();

        assertTrue(hunterCount >= 5);
        assertTrue(builderCount >= 4);
        assertTrue(gathererCount >= 1);
    }

    @Test
    void loadDecks_shouldIncludeEventCards() {
        GameDataLoader loader = new GameDataLoader();

        List<TribeDeck> deck = loader.loadDecks(2);

        boolean hasEvent = deck.stream()
                .anyMatch(card -> card instanceof EventCard);

        assertTrue(hasEvent);
    }

    @Test
    void loadDecks_shouldLoadCorrectNumberOfEventCards() {
        GameDataLoader loader = new GameDataLoader();

        List<TribeDeck> deck = loader.loadDecks(2);

        long eventCount = deck.stream()
                .filter(card -> card instanceof EventCard)
                .count();

        assertEquals(12, eventCount);
    }

    @Test
    void loadBuildings_shouldLoadEra1() {
        GameDataLoader loader = new GameDataLoader();

        List<BuildingCard> buildings = loader.loadBuildings(1, 2);

        assertFalse(buildings.isEmpty());
        assertEquals(1, buildings.size());
    }

    @Test
    void loadBuildings_shouldLoadEra2() {
        GameDataLoader loader = new GameDataLoader();

        List<BuildingCard> buildings = loader.loadBuildings(2, 2);

        assertFalse(buildings.isEmpty());
        assertEquals(2, buildings.size());
    }

    @Test
    void loadBuildings_shouldLoadEra3() {
        GameDataLoader loader = new GameDataLoader();

        List<BuildingCard> buildings = loader.loadBuildings(3, 2);

        assertFalse(buildings.isEmpty());
        assertEquals(3, buildings.size());
    }

    @Test
    void loadOfferTrack_shouldLoadReducedTrackForTwoPlayers() {
        GameDataLoader loader = new GameDataLoader();

        OfferTrack track = loader.loadOfferTrack(2);

        assertNotNull(track);
        assertEquals(4, track.getTiles().size());
    }

    @Test
    void loadOfferTrack_shouldLoadFullTrackForMoreThanTwoPlayers() {
        GameDataLoader loader = new GameDataLoader();

        OfferTrack track = loader.loadOfferTrack(4);

        assertNotNull(track);
        assertEquals(6, track.getTiles().size());
    }
}