package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.BuildingCards.SustenanceBuilding;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.CharacterCards.Artist;
import it.polimi.ingsw.model.CharacterCards.Gatherer;
import it.polimi.ingsw.model.CharacterTypeCounts.GatherersCount;
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

class SustenanceTest {

    private Sustenance sustenanceEvent;
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
        assertThrows(IllegalArgumentException.class, () -> sustenanceEvent.resolve(null, null),
                "Should throw IllegalArgumentException if players list is null.");

        assertThrows(IllegalStateException.class, () -> sustenanceEvent.resolve(Collections.emptyList(), null),
                "Should throw IllegalStateException if players list is empty.");

        List<Player> listWithNull = new ArrayList<>();
        listWithNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> sustenanceEvent.resolve(listWithNull, null),
                "Should throw IllegalArgumentException if a player in the list is null.");
    }

    @Test
    void testResolve_SufficientFoodNoPenalty() {
        player.getTribe().addCard(new Artist("artist_1", 1, 2, true));
        player.getTribe().addCard(new Artist("artist_2", 1, 2, true));
        player.setFoodAmount(5);

        Player secondPlayer = new Player(Color.BLUE, "SecondPlayer");
        List<Player> players = List.of(player, secondPlayer);
        Game game = newMinimalGame(players);

        sustenanceEvent.resolve(players, game);

        assertEquals(3, player.getFoodAmount(), "Player should pay 2 food for 2 character cards.");
        assertEquals(0, player.getPrestigePoints(), "Player should not lose any prestige points.");
    }

    @Test
    void testResolve_InsufficientFoodAppliesPenalty() {
        player.getTribe().addCard(new Artist("artist_1", 1, 2, true));
        player.getTribe().addCard(new Artist("artist_2", 1, 2, true));
        player.getTribe().addCard(new Artist("artist_3", 1, 2, true));
        player.setFoodAmount(1);

        Player secondPlayer = new Player(Color.BLUE, "SecondPlayer");
        List<Player> players = List.of(player, secondPlayer);
        Game game = newMinimalGame(players);

        sustenanceEvent.resolve(players, game);

        assertEquals(0, player.getFoodAmount(), "Player's food should be reduced to 0.");
        assertEquals(-4, player.getPrestigePoints(), "Player should lose 4 prestige points for 2 missing food.");
    }

    @Test
    void testResolve_GathererDiscountIsApplied() {
        player.getTribe().addCard(new Artist("artist_1", 1, 2, true));
        player.getTribe().addCard(new Artist("artist_2", 1, 2, true));
        player.getTribe().addCard(new Artist("artist_3", 1, 2, true));
        player.getTribe().addCard(new Gatherer("gatherer_1", 1, 2, true));
        player.setFoodAmount(0);

        Player secondPlayer = new Player(Color.BLUE, "SecondPlayer");
        List<Player> players = List.of(player, secondPlayer);
        Game game = newMinimalGame(players);

        sustenanceEvent.resolve(players, game);

        assertEquals(0, player.getFoodAmount(), "Food should remain at 0.");
        assertEquals(-2, player.getPrestigePoints(), "Player should lose 2 prestige points for the remaining required food.");
    }

    @Test
    void testResolve_SustenanceBuildingDiscountHandlesNegativeRequiredFood() {
        player.getTribe().addCard(new Gatherer("gatherer_1", 1, 2, true));
        player.getTribe().addCard(new Gatherer("gatherer_2", 1, 2, true));
        player.setFoodAmount(0);

        SustenanceBuilding building = new SustenanceBuilding("sustenance_1", 1, 2, true, 0, 0, 1, new GatherersCount());
        player.getTribe().addCard(building);

        Player secondPlayer = new Player(Color.BLUE, "SecondPlayer");
        List<Player> players = List.of(player, secondPlayer);
        Game game = newMinimalGame(players);

        sustenanceEvent.resolve(players, game);

        assertEquals(0, player.getFoodAmount(), "Player should keep 0 food when discounts fully cover sustenance.");
        assertEquals(0, player.getPrestigePoints(), "No prestige points should be lost.");
    }
}
