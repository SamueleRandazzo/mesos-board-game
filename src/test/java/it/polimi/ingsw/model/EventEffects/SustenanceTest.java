package it.polimi.ingsw.model.EventEffects;

<<<<<<< Updated upstream
import it.polimi.ingsw.model.CharacterCards.Artist;
import it.polimi.ingsw.model.CharacterCards.Gatherer;
import it.polimi.ingsw.model.BuildingCards.SustenanceBuilding;
import it.polimi.ingsw.model.CharacterTypeCounts.GatherersCount;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
=======
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.CharacterCards.Artist;
import it.polimi.ingsw.model.CharacterCards.Gatherer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
>>>>>>> Stashed changes
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SustenanceTest {

<<<<<<< Updated upstream
    private Sustenance sustenanceEvent;
    private Player player;

    @BeforeEach
    void setUp() {
        // Initialize an event that penalizes 2 PP per missing food
        sustenanceEvent = new Sustenance(2);
        player = new Player(Color.RED, "TestPlayer");
    }

    @Test
    void testConstructor_NegativePenaltyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Sustenance(-1),
                "Constructor should reject negative penalty values.");
    }

    @Test
    void testResolve_ExceptionsAreThrownCorrectly() {
        // Test null list
        assertThrows(IllegalArgumentException.class, () -> sustenanceEvent.resolve(null),
                "Should throw IllegalArgumentException if players list is null.");

        // Test empty list
        assertThrows(IllegalStateException.class, () -> sustenanceEvent.resolve(Collections.emptyList()),
                "Should throw IllegalStateException if players list is empty.");

        // Test list containing a null element
        List<Player> listWithNull = new ArrayList<>();
        listWithNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> sustenanceEvent.resolve(listWithNull),
                "Should throw IllegalArgumentException if a player in the list is null.");
    }

    @Test
    void testResolve_SufficientFoodNoPenalty() {
        // SETUP: 2 cards (require 2 food). Player has 5 food.
        player.getTribe().addCard(new Artist(1, 2, true));
        player.getTribe().addCard(new Artist(1, 2, true));
        player.setFoodAmount(5);

        // EXECUTE
        sustenanceEvent.resolve(List.of(player));

        // ASSERT: 5 - 2 = 3 food left. No prestige loss.
        assertEquals(3, player.getFoodAmount(), "Player should pay exactly 2 food.");
        assertEquals(0, player.getPrestigePoints(), "Player should not lose any prestige points.");
    }

    @Test
    void testResolve_InsufficientFoodAppliesPenalty() {
        // SETUP: 3 cards (require 3 food). Player has 1 food.
        player.getTribe().addCard(new Artist(1, 2, true));
        player.getTribe().addCard(new Artist(1, 2, true));
        player.getTribe().addCard(new Artist(1, 2, true));
        player.setFoodAmount(1);

        // EXECUTE
        sustenanceEvent.resolve(List.of(player));

        // ASSERT: Missing food is 2. Penalty is 2 missing * 2 PP = -4 PP. Food drops to 0.
        assertEquals(0, player.getFoodAmount(), "Player's food should be reduced to 0.");
        assertEquals(-4, player.getPrestigePoints(), "Player should lose 4 prestige points.");
    }

    @Test
    void testResolve_GathererDiscountIsApplied() {
        // SETUP: 4 cards (3 Artists + 1 Gatherer). Base food required = 4.
        // Gatherer discount = 3. Final required food = 1.
        // Player has 0 food.
        player.getTribe().addCard(new Artist(1, 2, true));
        player.getTribe().addCard(new Artist(1, 2, true));
        player.getTribe().addCard(new Artist(1, 2, true));
        player.getTribe().addCard(new Gatherer(1, 2, true));
        player.setFoodAmount(0);

        // EXECUTE
        sustenanceEvent.resolve(List.of(player));

        // ASSERT: Missing food is 1. Penalty is 1 missing * 2 PP = -2 PP.
        assertEquals(0, player.getFoodAmount(), "Food should remain at 0.");
        assertEquals(-2, player.getPrestigePoints(), "Player should lose 2 PP due to 1 missing food.");
    }

    @Test
    void testResolve_SustenanceBuildingDiscountHandlesNegativeRequiredFood() {
        // SETUP: 2 Gatherers. Base food = 2.
        // Gatherers discount = 2 * 3 = 6.
        // SustenanceBuilding provides an extra discount of 1 per Gatherer = 2.
        // Total discount = 8. Required food becomes negative, meaning 0 to feed.
        player.getTribe().addCard(new Gatherer(1, 2, true));
        player.getTribe().addCard(new Gatherer(1, 2, true));
        player.setFoodAmount(0);

        SustenanceBuilding building = new SustenanceBuilding(1, 2, true, 0, 0, 1, new GatherersCount());
        player.getTribe().addCard(building);

        // EXECUTE
        sustenanceEvent.resolve(List.of(player));

        // ASSERT: The event should gracefully ignore negative 'toFeed' values.
        assertEquals(0, player.getFoodAmount(), "Food should remain unaffected.");
        assertEquals(0, player.getPrestigePoints(), "No prestige points should be lost.");
=======
    private Sustenance sustenance;
    private List<Player> players;
    private final int PENALTY_PER_FOOD = 3;

    @BeforeEach
    void setUp() {
        // Penalty of 3 prestige points for each missing food
        sustenance = new Sustenance(PENALTY_PER_FOOD);
        players = new ArrayList<>();
    }

    @Test
    @DisplayName("Test sufficient food: food is consumed, prestige remains unchanged")
    void testResolve_SufficientFood() {
        Player p = new Player(Color.RED);
        // Tribe of 2 Artists (requires 2 food)
        p.getTribe().addCard(new Artist(1, 2, true));
        p.getTribe().addCard(new Artist(1, 2, true));
        p.setFoodAmount(10);

        players.add(p);
        sustenance.resolve(players);

        assertEquals(8, p.getFoodAmount(), "10 - 2 = 8 food remaining");
        assertEquals(0, p.getPrestigePoints(), "Prestige should not change");
    }

    @Test
    @DisplayName("Test insufficient food: food goes to 0 and prestige penalty is applied")
    void testResolve_InsufficientFood() {
        Player p = new Player(Color.BLUE);
        // 4 Artists = 4 food needed
        for(int i=0; i<4; i++) p.getTribe().addCard(new Artist(1, 2, true));

        p.setFoodAmount(1); // 3 food missing
        players.add(p);

        sustenance.resolve(players);

        // Missing 3 * Penalty 3 = -9 points
        assertEquals(0, p.getFoodAmount(), "Food should be clamped to 0");
        assertEquals(-9, p.getPrestigePoints(), "Penalty should be missingFood * pointsPenalty");
    }

    @Test
    @DisplayName("Test Gatherers reduction: 1 Gatherer feeds 3 people")
    void testResolve_GathererEffect() {
        Player p = new Player(Color.YELLOW);
        // 4 Characters: 1 Gatherer + 3 Artists
        p.getTribe().addCard(new Gatherer(1, 2, true));
        p.getTribe().addCard(new Artist(1, 2, true));
        p.getTribe().addCard(new Artist(1, 2, true));
        p.getTribe().addCard(new Artist(1, 2, true));

        // Requirement: 4 - (1 * 3) = 1 food needed
        p.setFoodAmount(1);
        players.add(p);

        sustenance.resolve(players);

        assertEquals(0, p.getFoodAmount(), "The 1 required food was consumed");
        assertEquals(0, p.getPrestigePoints(), "No penalty should be applied");
    }

    @Test
    @DisplayName("Test negative requirement: food should not increase")
    void testResolve_ExcessGatherers() {
        Player p = new Player(Color.WHITE);
        // 1 Character, but 1 Gatherer (feeds 3). Requirement: 1 - 3 = -2.
        p.getTribe().addCard(new Gatherer(1, 2, true));
        p.setFoodAmount(5);
        players.add(p);

        sustenance.resolve(players);

        // if toFeed <= 0, the event does nothing
        assertEquals(5, p.getFoodAmount(), "Food should not change if requirement is zero or negative");
    }

    @Test
    @DisplayName("Test validation: null or empty player list")
    void testResolve_Validation() {
        // Test null list
        assertThrows(IllegalArgumentException.class, () -> sustenance.resolve(null));

        // Test empty list
        List<Player> emptyList = new ArrayList<>();
        assertThrows(IllegalStateException.class, () -> sustenance.resolve(emptyList));

        // Test list containing null player
        players.add(null);
        assertThrows(IllegalArgumentException.class, () -> sustenance.resolve(players));
    }

    @Test
    @DisplayName("Test constructor: pointsPenalty must be non-negative")
    void testConstructor_Validation() {
        assertThrows(IllegalArgumentException.class, () -> new Sustenance(-1));
>>>>>>> Stashed changes
    }
}