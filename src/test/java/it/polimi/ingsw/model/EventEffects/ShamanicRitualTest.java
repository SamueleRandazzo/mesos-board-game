package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShamanicRitualTest {

    private ShamanicRitual ritual;
    private List<Player> players;
    private final int VICTORY_POINTS = 10;
    private final int DEFEAT_POINTS = 5;

    @BeforeEach
    void setUp() {
        // Initialize the ritual with 10 victory points and 5 defeat points
        ritual = new ShamanicRitual(VICTORY_POINTS, DEFEAT_POINTS);
        players = new ArrayList<>();
    }

    @Test
    @DisplayName("Test basic resolution with one winner, one loser and one neutral player")
    void testResolve_StandardScenario() {
        Player winner = new Player(Color.RED, "winner");
        Player neutral = new Player(Color.BLUE, "neutral");
        Player loser = new Player(Color.YELLOW, "loser");

        // Setting stars: Winner(10), Neutral(5), Loser(0)
        winner.getTribe().getShamanicAttr().addStars(10);
        neutral.getTribe().getShamanicAttr().addStars(5);
        loser.getTribe().getShamanicAttr().addStars(0);

        players.addAll(List.of(winner, neutral, loser));

        ritual.resolve(players);

        // Assertions
        assertEquals(VICTORY_POINTS, winner.getPrestigePoints(), "Winner should gain standard victory points");
        assertEquals(0, neutral.getPrestigePoints(), "Neutral player should not change prestige points");
        assertEquals(-DEFEAT_POINTS, loser.getPrestigePoints(), "Loser should lose standard defeat points");
    }

    @Test
    @DisplayName("Test multiple winners and losers (Ties)")
    void testResolve_MultipleWinnersAndLosers() {
        Player win1 = new Player(Color.RED, "win1");
        Player win2 = new Player(Color.BLUE, "win2");
        Player loss1 = new Player(Color.WHITE, "loss1");
        Player loss2 = new Player(Color.BLACK, "loss2");

        win1.getTribe().getShamanicAttr().addStars(8);
        win2.getTribe().getShamanicAttr().addStars(8);
        loss1.getTribe().getShamanicAttr().addStars(2);
        loss2.getTribe().getShamanicAttr().addStars(2);

        players.addAll(List.of(win1, win2, loss1, loss2));

        ritual.resolve(players);

        assertEquals(VICTORY_POINTS, win1.getPrestigePoints());
        assertEquals(VICTORY_POINTS, win2.getPrestigePoints());
        assertEquals(-DEFEAT_POINTS, loss1.getPrestigePoints());
        assertEquals(-DEFEAT_POINTS, loss2.getPrestigePoints());
    }

    @Test
    @DisplayName("Test Double On Winning attribute")
    void testResolve_DoubleOnWinning() {
        Player luckyWinner = new Player(Color.RED, "luckyWinner");
        Player standardLoser = new Player(Color.BLUE, "standardLoser");

        luckyWinner.getTribe().getShamanicAttr().addStars(10);
        luckyWinner.getTribe().getShamanicAttr().setDoubleOnWinning(true);

        standardLoser.getTribe().getShamanicAttr().addStars(5);

        players.addAll(List.of(luckyWinner, standardLoser));

        ritual.resolve(players);

        // 10 * 2 = 20
        assertEquals(VICTORY_POINTS * 2, luckyWinner.getPrestigePoints(), "Double reward should be applied");
    }

    @Test
    @DisplayName("Test Prevent Loss attribute")
    void testResolve_PreventLoss() {
        Player standardWinner = new Player(Color.RED, "standardWinner");
        Player protectedLoser = new Player(Color.BLUE, "protectedLoser");

        standardWinner.getTribe().getShamanicAttr().addStars(10);

        protectedLoser.getTribe().getShamanicAttr().addStars(2);
        protectedLoser.getTribe().getShamanicAttr().setPreventLoss(true);

        players.addAll(List.of(standardWinner, protectedLoser));

        ritual.resolve(players);

        assertEquals(0, protectedLoser.getPrestigePoints(), "Protected loser should not lose points");
    }

    @Test
    @DisplayName("Test edge case: All players have the same number of stars")
    void testResolve_AllTied() {
        Player p1 = new Player(Color.RED, "p1");
        Player p2 = new Player(Color.BLUE, "p2");

        // Both have 5 stars
        p1.getTribe().getShamanicAttr().addStars(5);
        p2.getTribe().getShamanicAttr().addStars(5);

        players.addAll(List.of(p1, p2));

        ritual.resolve(players);

        // According to current logic: both are in maxPlayers AND minPlayers
        // Result: 0 + 10 (win) - 5 (loss) = 5
        int expected = VICTORY_POINTS - DEFEAT_POINTS;
        assertEquals(expected, p1.getPrestigePoints(), "Player should receive both reward and penalty if they are both max and min");
        assertEquals(expected, p2.getPrestigePoints());
    }

    @Test
    @DisplayName("Test edge case: Single player in the game")
    void testResolve_SinglePlayer() {
        Player p1 = new Player(Color.RED, "singlePlayer");
        p1.getTribe().getShamanicAttr().addStars(5);
        players.add(p1);

        ritual.resolve(players);

        // Single player is both max and min
        assertEquals(VICTORY_POINTS - DEFEAT_POINTS, p1.getPrestigePoints());
    }
}