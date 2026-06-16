package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Interfaces.EventEffect;
import it.polimi.ingsw.model.Player;

import java.util.List;
/**
 * Represents the Hunt event effect in the game.
 *
 * <p>For each player:
 * <ul>
 *     <li>They gain Food equal to the total produced by their Hunt buildings.</li>
 *     <li>They gain Prestige Points equal to the total provided by their Hunt buildings.</li>
 *     <li>They gain additional Food equal to the number of Hunters in their tribe.</li>
 *     <li>They gain additional Prestige Points equal to
 *         {@code numberOfHunters * pointsPerCard}.</li>
 * </ul>
 *
 * <p>This event therefore rewards both Hunt buildings and the number of Hunters.</p>
 */
public class Hunt implements EventEffect {

    private final int pointsPerCard;

    /**
     * Creates a Hunt event effect.
     *
     * @param pointsPerCard the number of Prestige Points awarded per Hunter
     *
     * @throws IllegalArgumentException if {@code pointsPerCard < 0}
     */
    public Hunt(int pointsPerCard) {
        if (pointsPerCard < 0)
            throw new IllegalArgumentException("pointsPerCard cannot be negative");

        this.pointsPerCard = pointsPerCard;
    }

    /**
     * Applies the Hunt event effect to all players in the list.
     *
     * <p>Each player receives Food and Prestige Points from their Hunt buildings,
     * plus additional rewards based on the number of Hunters they have.</p>
     *
     * @param players the list of players participating in the event
     *
     * @throws IllegalArgumentException if {@code players} is {@code null}
     *                                  or contains {@code null} elements
     * @throws IllegalStateException if {@code players} is empty
     */
    @Override
    public void resolve(List<Player> players, Game game) {
        if (players == null)
            throw new IllegalArgumentException("Players list cannot be null.");

        if (players.isEmpty())
            throw new IllegalStateException("Cannot resolve event with no players.");

        for (Player p : players) {
            if (p == null)
                throw new IllegalArgumentException("Player cannot be null.");

            int numberOfHunters = p.getTribe().getHuntersCount();
            int totalFoodReward = p.getTribe().totalFoodByHuntBuildings() + numberOfHunters;
            int totalPointsReward = p.getTribe().totalPointsByHuntBuildings() + numberOfHunters * pointsPerCard;

            p.changeFoodAmount(totalFoodReward);
            p.changePrestigePoints(totalPointsReward);

            game.notifyEventMessage(p, String.format("HUNT EVENT: You earned %d foods and %d prestige points.", totalFoodReward, totalPointsReward));
        }
    }
}