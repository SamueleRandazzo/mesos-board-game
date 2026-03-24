package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Hunt event effect.
 * This class verifies the correct distribution of Food and Prestige Points
 * based on the number of Hunters in a player's tribe.
 */
class HuntTest {

    private Hunt huntEvent;

    @BeforeEach
    void setUp() {
        // Initialize the event with a fixed multiplier of 3 points per hunter
        huntEvent = new Hunt(3);
    }

    @Test
    @DisplayName("Should grant 0 rewards if players have no hunters or hunt buildings")
    void testResolveNoHunters() {
        // Create player locally to ensure no leftover cards from other tests
        Player player = new Player(Color.YELLOW);
        List<Player> players = List.of(player);

        huntEvent.resolve(players);

        assertEquals(0, player.getFoodAmount(), "Food should remain 0");
        assertEquals(0, player.getPrestigePoints(), "Points should remain 0");
    }

    /*@Test
    @DisplayName("Should correctly calculate rewards for a single player with multiple hunters")
    void testResolveWithHunters() {
        Player player = new Player(Color.RED);

        // Adding 2 Hunters to the tribe.
        // Constructor: Hunter(era, minPlayer, isObtainable, foodIcon)
        player.getTribe().addCard(new Hunter(1, 2, true, true));
        player.getTribe().addCard(new Hunter(1, 2, true, false));

        huntEvent.resolve(List.of(player));

        // Calculation: 2 hunters = 2 food
        assertEquals(2, player.getFoodAmount(), "Player should receive exactly 1 food per hunter");

        // Calculation: 2 hunters * 3 points = 6 points
        assertEquals(6, player.getPrestigePoints(), "Player should receive hunters * pointsPerCard");
    }

    @Test
    @DisplayName("Should handle multiple players independently (no state leakage)")
    void testMultiplePlayers() {
        // Creating fresh players inside the method is the only way to avoid 'Expected 1, Actual 2' errors
        Player p1 = new Player(Color.BLUE);
        Player p2 = new Player(Color.WHITE);

        // Player 1: Has 1 Hunter -> Expected: +1 Food, +3 Points
        p1.getTribe().addCard(new Hunter(1, 2, true, true));

        // Player 2: Has 2 Hunters -> Expected: +2 Food, +6 Points
        p2.getTribe().addCard(new Hunter(1, 2, true, true));
        p2.getTribe().addCard(new Hunter(1, 2, true, true));

        List<Player> players = List.of(p1, p2);
        huntEvent.resolve(players);

        // assertAll ensures all checks are run even if one fails
        assertAll("Verify rewards for all players",
                () -> assertEquals(1, p1.getFoodAmount(), "P1 food count is incorrect"),
                () -> assertEquals(3, p1.getPrestigePoints(), "P1 points count is incorrect"),
                () -> assertEquals(2, p2.getFoodAmount(), "P2 food count is incorrect"),
                () -> assertEquals(6, p2.getPrestigePoints(), "P2 points count is incorrect")
        );
    }*/

    @Test
    @DisplayName("Should throw IllegalArgumentException for negative points in constructor")
    void testConstructorNegativePoints() {
        // Note: This assumes your Hunt class has a check in the constructor
        assertThrows(IllegalArgumentException.class, () -> new Hunt(-5));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if the list of players is null")
    void testResolveNullList() {
        assertThrows(IllegalArgumentException.class, () -> huntEvent.resolve(null));
    }

    @Test
    @DisplayName("Should throw IllegalStateException if the list of players is empty")
    void testResolveEmptyList() {
        List<Player> emptyList = new ArrayList<>();
        assertThrows(IllegalStateException.class, () -> huntEvent.resolve(emptyList));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if a player in the list is null")
    void testResolveListWithNullElement() {
        List<Player> listWithNull = new ArrayList<>();
        listWithNull.add(new Player(Color.BLACK));
        listWithNull.add(null);

        assertThrows(IllegalArgumentException.class, () -> huntEvent.resolve(listWithNull));
    }
}