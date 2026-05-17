package it.polimi.ingsw.model.Cards;

import it.polimi.ingsw.model.EventEffects.Hunt;
import it.polimi.ingsw.model.Interfaces.EventEffect;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Enum.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Board.OfferTrack;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventCardTest {

    private EventCard eventCard;
    private EventEffect actualGameEvent;
    private Player player;

    @BeforeEach
    void setUp() {
        // Use a REAL game event (Hunt grants prestige based on Hunters)
        actualGameEvent = new Hunt(2);

        player = new Player(Color.RED, "TestPlayer");

        // Create an EventCard for Era 1, min 2 players, not a final event
        eventCard = new EventCard("event_1", 1, 2, false, actualGameEvent);
    }

    @Test
    void testConstructor_ValidParameters() {
        // ASSERT: Getters should return the exact values passed in the constructor
        assertEquals(1, eventCard.getEra(), "Era should be 1.");
        assertEquals(2, eventCard.getMinPlayer(), "Minimum players should be 2.");
        assertFalse(eventCard.isFinal(), "Card should not be a final event.");
        assertEquals(actualGameEvent, eventCard.getEventEffect(), "Should return the assigned Hunt event.");

        // MESOS RULE: Event cards are never added to a player's tribe
        assertFalse(eventCard.getIsObtainable(), "Event cards should not be obtainable.");
    }

    @Test
    void testConstructor_NullEventEffectThrowsException() {
        // EXECUTE & ASSERT: An EventCard must hold a valid EventEffect
        assertThrows(IllegalArgumentException.class, () -> new EventCard("event_null", 1, 2, false, null),
                "Constructor must throw an exception if the EventEffect is null.");
    }

    @Test
    void testConstructor_InvalidCardParametersThrowException() {
        // MESOS RULE: Eras are strictly 1, 2, or 3
        assertThrows(IllegalArgumentException.class, () -> new EventCard("event_invalid_era_0", 0, 2, false, actualGameEvent),
                "Should throw an exception for Era < 1.");

        assertThrows(IllegalArgumentException.class, () -> new EventCard("event_invalid_era_4", 4, 2, false, actualGameEvent),
                "Should throw an exception for Era > 3.");

        // MESOS RULE: Player count must be strictly between 2 and 5
        assertThrows(IllegalArgumentException.class, () -> new EventCard("event_invalid_min_1", 1, 1, false, actualGameEvent),
                "Should throw an exception if minimum players is less than 2.");

        assertThrows(IllegalArgumentException.class, () -> new EventCard("event_invalid_min_6", 1, 6, false, actualGameEvent),
                "Should throw an exception if minimum players is greater than 5.");
    }

    @Test
    void testRaiseEvent_Success() {
        // SETUP: Give the player 5 starting food to verify behavior
        player.setFoodAmount(5);
        Player secondPlayer = new Player(Color.BLUE, "SecondPlayer");
        List<Player> players = List.of(player, secondPlayer);

        // EXECUTE: Resolve the event through the card.
        // A base Hunt event (with 0 Hunters) shouldn't add food or crash.
        Game game = new Game(
                players,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new OfferTrack(new ArrayList<>())
        );

        eventCard.raiseEvent(players, game);

        // ASSERT: Verify the player hasn't erroneously lost or gained resources
        assertEquals(5, player.getFoodAmount(), "The Hunt event should not add food without Hunters.");
    }

    @Test
    void testRaiseEvent_ExceptionsAreThrownCorrectly() {
        // EXECUTE & ASSERT: The system must not allow resolving an event into the void

        assertThrows(IllegalArgumentException.class, () -> eventCard.raiseEvent(null, null),
                "Should throw IllegalArgumentException if the player list is null.");

        assertThrows(IllegalStateException.class, () -> eventCard.raiseEvent(Collections.emptyList(), null),
                "Should throw IllegalStateException if the player list is empty.");
    }

    @Test
    void testApplyTo_ThrowsException() {
        // MESOS RULE: Event cards trigger globally, players CANNOT take them into their tribe
        assertThrows(IllegalArgumentException.class, () -> eventCard.applyTo(player),
                "The applyTo method must throw an exception because event cards cannot be obtained.");
    }

    @Test
    void testIsEvent_ReturnsTrue() {
        // EXECUTE & ASSERT: The TribeDeck interface marker should return true
        assertTrue(eventCard.isEvent(), "isEvent() must return true for an EventCard.");
    }
}
