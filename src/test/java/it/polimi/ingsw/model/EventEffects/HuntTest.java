package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.CharacterCards.Hunter;
import it.polimi.ingsw.model.BuildingCards.HuntBuilding;
import it.polimi.ingsw.model.CharacterTypeCounts.HuntersCount;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HuntTest {

    private Player player;
    private Hunt huntEvent;

    @BeforeEach
    void setUp() {
        player = new Player(Color.BLUE, "TestPlayer");
        // Create a Hunt event that gives 2 PP per Hunter
        huntEvent = new Hunt(2);
    }

    @Test
    void testResolveExceptions() {
        assertThrows(IllegalArgumentException.class, () -> huntEvent.resolve(null),
                "Should throw exception if player list is null.");
        assertThrows(IllegalStateException.class, () -> huntEvent.resolve(Collections.emptyList()),
                "Should throw exception if player list is empty.");

        List<Player> listWithNull = new ArrayList<>();
        listWithNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> huntEvent.resolve(listWithNull),
                "Should throw exception if player list contains null.");
    }

    @Test
    void testHuntWithNoHunters() {
        // SETUP: Player has no cards.

        // EXECUTE
        huntEvent.resolve(List.of(player));

        // ASSERT: No food or PP should be gained.
        assertEquals(0, player.getFoodAmount(), "Player should not gain any food.");
        assertEquals(0, player.getPrestigePoints(), "Player should not gain any prestige points.");
    }

    @Test
    void testHuntWithOnlyHunters() {
        // SETUP: Player gets 2 Hunters (foodIcon is false to not trigger the immediate bonus in Tribe.java).
        player.getTribe().addCard(new Hunter(1, 2, true, false));
        player.getTribe().addCard(new Hunter(1, 2, true, false));

        // EXECUTE
        huntEvent.resolve(List.of(player));

        // ASSERT: 2 Hunters -> +2 food. 2 Hunters * 2 PP = +4 PP.
        assertEquals(2, player.getFoodAmount(), "Player should gain 1 food per Hunter.");
        assertEquals(4, player.getPrestigePoints(), "Player should gain 2 PP per Hunter.");
    }

    @Test
    void testHuntWithHuntersAndBuilding() {
        // SETUP: Add 2 Hunters.
        player.getTribe().addCard(new Hunter(1, 2, true, false));
        player.getTribe().addCard(new Hunter(1, 2, true, false));

        // Add a HuntBuilding: gives 1 extra food and 1 extra PP per Hunter
        HuntBuilding building = new HuntBuilding(1, 2, true, 0, 0, 1, 1, new HuntersCount());
        player.getTribe().addCard(building);

        // EXPECTATIONS:
        // Building Food: 2 Hunters * 1 = 2
        // Building PP: 2 Hunters * 1 = 2
        // Base Event Food: 2 Hunters = 2
        // Base Event PP: 2 Hunters * 2 = 4
        // Total Food: 4, Total PP: 6

        // EXECUTE
        huntEvent.resolve(List.of(player));

        // ASSERT
        assertEquals(4, player.getFoodAmount(), "Player should gain combined food from building and event.");
        assertEquals(6, player.getPrestigePoints(), "Player should gain combined prestige points from building and event.");
    }
}