package it.polimi.ingsw.model.EventEffects;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SustenanceTest {

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
    }
}