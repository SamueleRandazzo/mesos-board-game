package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.CharacterCards.Artist;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CavePaintings event effect.
 * Tests focus on food gain from buildings and Prestige Points logic based on Artists.
 */
class CavePaintingsTest {

    private CavePaintings cavePaintings;
    private Player player;
    private List<Player> players;

    // Test constants
    private final int THRESHOLD = 2;
    private final int BONUS_PER_ARTIST = 2;
    private final int PENALTY = 3;

    @BeforeEach
    void setUp() {
        // Threshold: 2, Bonus: 2 PP per artist, Penalty: 3 PP
        cavePaintings = new CavePaintings(THRESHOLD, BONUS_PER_ARTIST, PENALTY);

        // Initializing player with a color as per Player.java constructor
        player = new Player(Color.RED);
        players = new ArrayList<>();
        players.add(player);
    }

    @Test
    @DisplayName("Should apply penalty when the player has fewer artists than threshold")
    void testPenaltyApplied() {
        // Player starts with 0 artists (below threshold of 2)
        int initialPoints = player.getPrestigePoints();

        cavePaintings.resolve(players);

        // Expected: 0 - 3 = -3
        assertEquals(initialPoints - PENALTY, player.getPrestigePoints(),
                "Player should receive a penalty of 3 points");
    }

    @Test
    @DisplayName("Should apply bonus when the player meets the artist threshold")
    void testBonusAppliedAtThreshold() {
        // Adding 2 Artists to reach the threshold
        player.getTribe().addCard(new Artist(1, 2, true));
        player.getTribe().addCard(new Artist(1, 2, true));

        int initialPoints = player.getPrestigePoints();
        cavePaintings.resolve(players);

        // Expected: 2 artists * 2 bonus = +4 points
        assertEquals(initialPoints + 4, player.getPrestigePoints(),
                "Player should gain 4 points (2 artists * 2 points)");
    }

    @Test
    @DisplayName("Should apply bonus proportional to the number of artists")
    void testBonusProportional() {
        // Adding 3 Artists (above threshold)
        player.getTribe().addCard(new Artist(1, 2, true));
        player.getTribe().addCard(new Artist(1, 2, true));
        player.getTribe().addCard(new Artist(1, 2, true));

        cavePaintings.resolve(players);

        // Expected: 3 artists * 2 bonus = +6 points
        assertEquals(6, player.getPrestigePoints());
    }

    @Test
    @DisplayName("Should verify food gain remains 0 if no buildings are present")
    void testFoodGainNoBuildings() {
        player.setFoodAmount(10);

        cavePaintings.resolve(players);

        // Without CavePaintingBuildings, food should not change
        assertEquals(10, player.getFoodAmount());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if players list is null")
    void testNullPlayersList() {
        assertThrows(IllegalArgumentException.class, () -> cavePaintings.resolve(null));
    }

    @Test
    @DisplayName("Should throw IllegalStateException if players list is empty")
    void testEmptyPlayersList() {
        List<Player> emptyList = new ArrayList<>();
        assertThrows(IllegalStateException.class, () -> cavePaintings.resolve(emptyList));
    }

    @Test
    @DisplayName("Should handle multiple players with different tribe compositions")
    void testMultiplePlayers() {
        Player player2 = new Player(Color.BLUE);
        // Player 2 has 2 artists (Bonus +4), Player 1 has 0 (Penalty -3)
        player2.getTribe().addCard(new Artist(1, 2, true));
        player2.getTribe().addCard(new Artist(1, 2, true));

        players.add(player2);
        cavePaintings.resolve(players);

        assertAll("Verify results for all players",
                () -> assertEquals(-3, player.getPrestigePoints()),
                () -> assertEquals(4, player2.getPrestigePoints())
        );
    }
}