package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.CharacterCards.Artist;
import it.polimi.ingsw.model.BuildingCards.CavePaintingBuilding;
import it.polimi.ingsw.model.CharacterTypeCounts.ArtistsCount;
import it.polimi.ingsw.model.Enum.Color;
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

    @Test
    void testResolveExceptions() {
        assertThrows(IllegalArgumentException.class, () -> cavePaintingsEvent.resolve(null));
        assertThrows(IllegalStateException.class, () -> cavePaintingsEvent.resolve(Collections.emptyList()));
        List<Player> listWithNull = new ArrayList<>();
        listWithNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> cavePaintingsEvent.resolve(listWithNull));
    }

    @Test
    void testZeroArtistsAppliesPenalty() {
        // SETUP: Player has 0 Artists (threshold is 2).

        // EXECUTE
        cavePaintingsEvent.resolve(List.of(player));

        // ASSERT: Player is below threshold, loses 5 PP.
        assertEquals(-5, player.getPrestigePoints(), "Player should lose 5 PP for having 0 Artists.");
    }

    @Test
    void testPenaltyAppliedWhenBelowThreshold() {
        // SETUP: Player has only 1 Artist (threshold is 2).
        player.getTribe().addCard(new Artist(1, 2, true));

        // EXECUTE
        cavePaintingsEvent.resolve(List.of(player));

        // ASSERT: Player is below threshold, should lose 5 PP.
        assertEquals(-5, player.getPrestigePoints(), "Player should lose 5 PP for not meeting the Artist threshold.");
        assertEquals(0, player.getFoodAmount(), "Player should not gain any food without buildings.");
    }

    @Test
    void testBonusAppliedWhenThresholdMet() {
        // SETUP: Player has 2 Artists (meets the threshold of 2).
        player.getTribe().addCard(new Artist(1, 2, true));
        player.getTribe().addCard(new Artist(1, 2, true));

        // EXECUTE
        cavePaintingsEvent.resolve(List.of(player));

        // ASSERT: Player met threshold. Bonus is 3 PP * 2 Artists = 6 PP.
        assertEquals(6, player.getPrestigePoints(), "Player should gain 6 PP (3 per Artist).");
    }

    @Test
    void testCavePaintingBuildingBonusFood() {
        // SETUP: Player has 2 Artists.
        player.getTribe().addCard(new Artist(1, 2, true));
        player.getTribe().addCard(new Artist(1, 2, true));

        // Add a CavePaintingBuilding: gives 2 extra food per Artist
        CavePaintingBuilding building = new CavePaintingBuilding(1, 2, true, 0, 0, 2, new ArtistsCount());
        player.getTribe().addCard(building);

        // EXPECTATIONS: 2 Artists * 2 extra food = 4 food. Met threshold -> 2 Artists * 3 PP = 6 PP.

        // EXECUTE
        cavePaintingsEvent.resolve(List.of(player));

        // ASSERT
        assertEquals(4, player.getFoodAmount(), "Player should gain 4 food from the building.");
        assertEquals(6, player.getPrestigePoints(), "Player should still gain the 6 PP from the event.");
    }
}