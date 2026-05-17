package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.BuildingCards.CavePaintingBuilding;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.CharacterCards.Artist;
import it.polimi.ingsw.model.CharacterTypeCounts.ArtistsCount;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CavePaintingsTest {

    private Player player;
    private CavePaintings cavePaintingsEvent;

    @BeforeEach
    void setUp() {
        player = new Player(Color.YELLOW, "TestPlayer");
        // Threshold: 2 Artists. Bonus: 3 PP per Artist. Penalty: 5 PP.
        cavePaintingsEvent = new CavePaintings(2, 3, 5);
    }

    private Game newMinimalGame(List<Player> players) {
        return new Game(
                players,
                new ArrayList<TribeDeck>(),
                new ArrayList<BuildingCard>(),
                new ArrayList<BuildingCard>(),
                new ArrayList<BuildingCard>(),
                new OfferTrack(new ArrayList<>())
        );
    }

    @Test
    void testResolveExceptions() {
        assertThrows(IllegalArgumentException.class, () -> cavePaintingsEvent.resolve(null, null));
        assertThrows(IllegalStateException.class, () -> cavePaintingsEvent.resolve(Collections.emptyList(), null));
        List<Player> listWithNull = new ArrayList<>();
        listWithNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> cavePaintingsEvent.resolve(listWithNull, null));
    }

    @Test
    void testZeroArtistsAppliesPenalty() {
        Player secondPlayer = new Player(Color.BLUE, "SecondPlayer");
        List<Player> players = List.of(player, secondPlayer);
        Game game = newMinimalGame(players);

        cavePaintingsEvent.resolve(players, game);

        assertEquals(-5, player.getPrestigePoints(), "Player should lose 5 PP for having 0 Artists.");
    }

    @Test
    void testPenaltyAppliedWhenBelowThreshold() {
        player.getTribe().addCard(new Artist("artist_1", 1, 2, true));

        Player secondPlayer = new Player(Color.BLUE, "SecondPlayer");
        List<Player> players = List.of(player, secondPlayer);
        Game game = newMinimalGame(players);

        cavePaintingsEvent.resolve(players, game);

        assertEquals(-5, player.getPrestigePoints(), "Player should lose 5 PP for not meeting the Artist threshold.");
        assertEquals(0, player.getFoodAmount(), "Player should not gain food without Cave Painting buildings.");
    }

    @Test
    void testBonusAppliedWhenThresholdMet() {
        player.getTribe().addCard(new Artist("artist_1", 1, 2, true));
        player.getTribe().addCard(new Artist("artist_2", 1, 2, true));

        Player secondPlayer = new Player(Color.BLUE, "SecondPlayer");
        List<Player> players = List.of(player, secondPlayer);
        Game game = newMinimalGame(players);

        cavePaintingsEvent.resolve(players, game);

        assertEquals(6, player.getPrestigePoints(), "Player should gain 6 PP (3 per Artist).");
    }

    @Test
    void testCavePaintingBuildingBonusFood() {
        player.getTribe().addCard(new Artist("artist_1", 1, 2, true));
        player.getTribe().addCard(new Artist("artist_2", 1, 2, true));

        CavePaintingBuilding building = new CavePaintingBuilding("cave_painting_1", 1, 2, true, 0, 0, 2, new ArtistsCount());
        player.getTribe().addCard(building);

        Player secondPlayer = new Player(Color.BLUE, "SecondPlayer");
        List<Player> players = List.of(player, secondPlayer);
        Game game = newMinimalGame(players);

        cavePaintingsEvent.resolve(players, game);

        assertEquals(4, player.getFoodAmount(), "Player should gain 4 food from the Cave Painting building.");
        assertEquals(6, player.getPrestigePoints(), "Player should still gain the 6 PP from the event.");
    }
}
