package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.CharacterCards.Shaman;
import it.polimi.ingsw.model.BuildingCards.InstantEffectBuilding;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShamanicRitualTest {

    private Player p1, p2, p3;
    private ShamanicRitual ritualEvent;

    @BeforeEach
    void setUp() {
        p1 = new Player(Color.RED, "Alice");
        p2 = new Player(Color.BLUE, "Bob");
        p3 = new Player(Color.YELLOW, "Charlie");
        // Win reward: 10 PP. Defeat penalty: 5 PP.
        ritualEvent = new ShamanicRitual(10, 5);
    }

    @Test
    void testResolveExceptions() {
        assertThrows(IllegalArgumentException.class, () -> ritualEvent.resolve(null));
        assertThrows(IllegalStateException.class, () -> ritualEvent.resolve(Collections.emptyList()));
        List<Player> listWithNull = new ArrayList<>();
        listWithNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> ritualEvent.resolve(listWithNull));
    }

    @Test
    void testClearWinnerAndLoser() {
        // SETUP: p1 has 3 stars, p2 has 1 star, p3 has 2 stars
        p1.getTribe().addCard(new Shaman(1, 2, true, 3));
        p2.getTribe().addCard(new Shaman(1, 2, true, 1));
        p3.getTribe().addCard(new Shaman(1, 2, true, 2));

        // EXECUTE
        ritualEvent.resolve(List.of(p1, p2, p3));

        // ASSERT
        assertEquals(10, p1.getPrestigePoints(), "P1 (Max) should gain 10 PP.");
        assertEquals(-5, p2.getPrestigePoints(), "P2 (Min) should lose 5 PP.");
        assertEquals(0, p3.getPrestigePoints(), "P3 (Middle) should not gain or lose PP.");
    }

    @Test
    void testAbsoluteTieEveryoneAppliesBothBonusAndMalus() {
        // SETUP: No players have any Shaman cards (Everyone has 0 stars).
        // Since max = 0 and min = 0, every player is in BOTH maxPlayers and minPlayers.
        // Therefore, every player gets +10 (win) and -5 (loss) = +5 PP net total.

        // EXECUTE
        ritualEvent.resolve(List.of(p1, p2, p3));

        // ASSERT: Everyone ends up with +5 PP
        assertEquals(5, p1.getPrestigePoints(), "P1 should get +10 and -5 due to absolute tie.");
        assertEquals(5, p2.getPrestigePoints(), "P2 should get +10 and -5 due to absolute tie.");
        assertEquals(5, p3.getPrestigePoints(), "P3 should get +10 and -5 due to absolute tie.");
    }

    @Test
    void testTiedWinnersAndLosers() {
        // SETUP: p1 and p2 tie for Max (4 stars). p3 and an added p4 tie for Min (1 star).
        Player p4 = new Player(Color.WHITE, "Dave");

        p1.getTribe().addCard(new Shaman(1, 2, true, 4));
        p2.getTribe().addCard(new Shaman(1, 2, true, 4));
        p3.getTribe().addCard(new Shaman(1, 2, true, 1));
        p4.getTribe().addCard(new Shaman(1, 2, true, 1));

        // EXECUTE
        ritualEvent.resolve(List.of(p1, p2, p3, p4));

        // ASSERT: Both winners get +10, both losers get -5.
        assertEquals(10, p1.getPrestigePoints());
        assertEquals(10, p2.getPrestigePoints());
        assertEquals(-5, p3.getPrestigePoints());
        assertEquals(-5, p4.getPrestigePoints());
    }

    @Test
    void testDoubleOnWinningAndPreventLoss() {
        // SETUP:
        // p1 (Winner) has 3 stars AND a DoubleOnWinning building.
        // p2 (Loser) has 1 star AND a PreventLoss building.

        p1.getTribe().addCard(new Shaman(1, 2, true, 3));
        // InstantEffectBuilding(era, minPlayer, isObtainable, foodCost, prestigePoints, extraStars, preventLoss, doubleOnWinning, extraCardFromUpper, extraFoodFromBonus)
        p1.getTribe().addCard(new InstantEffectBuilding(1, 2, true, 0, 0, 0, false, true, false, false));

        p2.getTribe().addCard(new Shaman(1, 2, true, 1));
        p2.getTribe().addCard(new InstantEffectBuilding(1, 2, true, 0, 0, 0, true, false, false, false));

        // EXECUTE
        ritualEvent.resolve(List.of(p1, p2));

        // ASSERT
        assertEquals(20, p1.getPrestigePoints(), "P1 should get Double Points (10 * 2).");
        assertEquals(0, p2.getPrestigePoints(), "P2 should prevent the 5 PP loss.");
    }
}