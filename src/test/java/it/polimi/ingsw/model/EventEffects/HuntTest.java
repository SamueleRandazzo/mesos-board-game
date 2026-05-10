package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.BuildingCards.HuntBuilding;
import it.polimi.ingsw.model.CharacterCards.Hunter;
import it.polimi.ingsw.model.CharacterTypeCounts.HuntersCount;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Interfaces.TribeDeck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HuntTest {

    private Hunt huntEvent;
    private Player player;
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

    @BeforeEach
    void setUp() {
        huntEvent = new Hunt(2);
        player = new Player(Color.RED, "TestPlayer");
    }

    @Test
    void testConstructor_NegativePointsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Hunt(-1),
                "Constructor should reject negative hunt points.");
    }

    @Test
    void testResolve_ExceptionsAreThrownCorrectly() {
        assertThrows(IllegalArgumentException.class, () -> huntEvent.resolve(null, null),
                "Should throw IllegalArgumentException if players list is null.");

        assertThrows(IllegalStateException.class, () -> huntEvent.resolve(Collections.emptyList(), null),
                "Should throw IllegalStateException if players list is empty.");

        List<Player> listWithNull = new ArrayList<>();
        listWithNull.add(null);

        assertThrows(IllegalArgumentException.class, () -> huntEvent.resolve(listWithNull, null),
                "Should throw IllegalArgumentException if a player in the list is null.");
    }

    @Test
    void testResolve_NoHuntersNoPoints() {
        Player secondPlayer = new Player(Color.BLUE, "SecondPlayer");
        List<Player> players = List.of(player, secondPlayer);

        Game game = newMinimalGame(players);

        huntEvent.resolve(players, game);

        assertEquals(0, player.getPrestigePoints(),
                "Player without Hunters should gain no prestige points.");
    }

    @Test
    void testResolve_HuntersGrantPrestigePoints() {
        // Add 2 Hunters
        player.getTribe().addCard(new Hunter("hunter_1", 1, 2, true, false));
        player.getTribe().addCard(new Hunter("hunter_2", 1, 2, true, false));

        Player secondPlayer = new Player(Color.BLUE, "SecondPlayer");
        List<Player> players = List.of(player, secondPlayer);

        Game game = newMinimalGame(players);

        huntEvent.resolve(players, game);

        // 2 Hunters * 2 PP each = 4 PP
        assertEquals(4, player.getPrestigePoints(),
                "Player should gain 4 prestige points.");
    }

    @Test
    void testResolve_HuntBuildingBonusApplies() {
        // Add 2 Hunters
        player.getTribe().addCard(new Hunter("hunter_1", 1, 2, true, false));
        player.getTribe().addCard(new Hunter("hunter_2", 1, 2, true, false));

        // Add Hunt Building: +1 food and +3 points per Hunter
        HuntBuilding building = new HuntBuilding(
                "hunt_1",
                1,
                2,
                true,
                0,
                0,
                1,
                3,
                new HuntersCount()
        );

        player.getTribe().addCard(building);

        Player secondPlayer = new Player(Color.BLUE, "SecondPlayer");
        List<Player> players = List.of(player, secondPlayer);

        Game game = newMinimalGame(players);

        huntEvent.resolve(players, game);

        // Base Hunt event: 2 Hunters * 2 PP = 4 PP
        // Building bonus: 2 Hunters * 3 PP = 6 PP
        // Total = 10 PP

        assertEquals(10, player.getPrestigePoints(),
                "Player should gain total prestige points from Hunt + Building.");

        // Food bonus from building: 2 Hunters * 1 Food = 2 Food
        assertEquals(5, player.getFoodAmount(), "Player should gain food from Hunt event and Hunt Building.");
    }
}