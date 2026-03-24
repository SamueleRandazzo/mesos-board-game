package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.Interfaces.EventEffect;
import it.polimi.ingsw.model.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Shamanic Ritual event effect in the game.
 *
 * <p>This event compares the Shamanic Stars of all players:
 * <ul>
 *     <li>Players with the highest number of stars receive the victory reward.</li>
 *     <li>Players with the lowest number of stars receive the defeat penalty.</li>
 * </ul>
 *
 * <p>Some tribes may have special attributes:
 * <ul>
 *     <li><b>Double on Winning</b>: doubles the victory reward.</li>
 *     <li><b>Prevent Loss</b>: prevents the defeat penalty.</li>
 * </ul>
 */
public class ShamanicRitual implements EventEffect {

    private final int victoryPrestigePoints;
    private final int defeatPrestigePoints;

    /**
     * Creates a ShamanicRitual event effect.
     *
     * @param victoryPrestigePoints the Prestige Points awarded to winners (must be >= 0)
     * @param defeatPrestigePoints the Prestige Points removed from losers (must be >= 0)
     *
     * @throws IllegalArgumentException if any parameter is negative
     */
    public ShamanicRitual(int victoryPrestigePoints, int defeatPrestigePoints) {
        if (victoryPrestigePoints < 0)
            throw new IllegalArgumentException("Victory points cannot be negative");

        if (defeatPrestigePoints < 0)
            throw new IllegalArgumentException("Defeat points cannot be negative");

        this.victoryPrestigePoints = victoryPrestigePoints;
        this.defeatPrestigePoints = defeatPrestigePoints;
    }

    /**
     * Applies the Shamanic Ritual event to all players.
     *
     * <p>The method:
     * <ol>
     *     <li>Computes each player's Shamanic Stars.</li>
     *     <li>Finds the maximum and minimum star values.</li>
     *     <li>Awards victory points to all players with the maximum stars.</li>
     *     <li>Applies defeat penalties to all players with the minimum stars.</li>
     * </ol>
     *
     * @param players the list of players participating in the event
     *
     * @throws IllegalArgumentException if players is null or contains null elements
     * @throws IllegalStateException if players is empty
     */
    @Override
    public void resolve(List<Player> players) {
        if (players == null)
            throw new IllegalArgumentException("Players list cannot be null");

        if (players.isEmpty())
            throw new IllegalStateException("Cannot resolve event with no players");

        for (Player p : players) {
            if (p == null)
                throw new IllegalArgumentException("Player cannot be null");
        }

        int maxStars = Integer.MIN_VALUE;
        int minStars = Integer.MAX_VALUE;

        List<Player> maxPlayers = new ArrayList<>();
        List<Player> minPlayers = new ArrayList<>();

        // Map each player to their Shamanic Stars
        Map<Player, Integer> shamanicStarsForPlayer = new HashMap<>();
        for (Player p : players) {
            int stars = p.getTribe().getShamanicAttr().getStars();
            shamanicStarsForPlayer.put(p, stars);
        }

        // Determine maximum and minimum star values
        for (int stars : shamanicStarsForPlayer.values()) {
            if (stars > maxStars) maxStars = stars;
            if (stars < minStars) minStars = stars;
        }

        // Identify players with max and min stars
        for (Map.Entry<Player, Integer> entry : shamanicStarsForPlayer.entrySet()) {
            if (entry.getValue() == maxStars)
                maxPlayers.add(entry.getKey());

            if (entry.getValue() == minStars)
                minPlayers.add(entry.getKey());
        }

        // Apply victory rewards
        for (Player pl : maxPlayers) {
            boolean doubleReward = pl.getTribe().getShamanicAttr().isDoubleOnWinning();
            int reward = doubleReward ? victoryPrestigePoints * 2 : victoryPrestigePoints;
            pl.changePrestigePoints(reward);
        }

        // Apply defeat penalties (subtracting positive points)
        for (Player pl : minPlayers) {
            boolean preventLoss = pl.getTribe().getShamanicAttr().isPreventLoss();
            int penalty = preventLoss ? 0 : -defeatPrestigePoints; // subtract positive value
            pl.changePrestigePoints(penalty);
        }
    }
}