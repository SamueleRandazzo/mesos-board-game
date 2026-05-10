package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Interfaces.EventEffect;
import it.polimi.ingsw.model.Player;

import java.util.List;

/**
 * Represents the Cave Paintings event effect in the game.
 *
 * <p>For each player:
 * <ul>
 *     <li>They gain Food equal to the total provided by their Cave Painting buildings.</li>
 *     <li>If the number of Artists in their tribe is lower than the penalty threshold,
 *         they lose a fixed amount of Prestige Points.</li>
 *     <li>Otherwise, they gain Prestige Points equal to
 *         {@code bonusPointsPerArtist * numberOfArtists}.</li>
 * </ul>
 *
 * <p><strong>Note:</strong> {@code penaltyPoints} must always be a positive value.
 * The actual loss is applied as {@code -penaltyPoints} inside the method.</p>
 */
public class CavePaintings implements EventEffect {

    private final int penaltyThreshold;
    private final int bonusPointsPerArtist;
    private final int penaltyPoints;

    /**
     * Creates a Cave Paintings event effect.
     *
     * @param penaltyThreshold the minimum number of Artists required to avoid the penalty
     * @param bonusPointsPerArtist the number of Prestige Points gained per Artist
     *                             when the threshold is met
     * @param penaltyPoints the number of Prestige Points lost when the threshold is not met;
     *                      must be strictly positive
     *
     * @throws IllegalArgumentException if {@code penaltyThreshold < 0},
     *                                  {@code bonusPointsPerArtist < 0},
     *                                  or {@code penaltyPoints <= 0}
     */
    public CavePaintings(int penaltyThreshold, int bonusPointsPerArtist, int penaltyPoints) {
        if (penaltyThreshold < 0)
            throw new IllegalArgumentException("Penalty threshold cannot be negative");

        if (bonusPointsPerArtist < 0)
            throw new IllegalArgumentException("Bonus points per artist cannot be negative");

        if (penaltyPoints <= 0)
            throw new IllegalArgumentException("Penalty points must be strictly positive");

        this.penaltyThreshold = penaltyThreshold;
        this.bonusPointsPerArtist = bonusPointsPerArtist;
        this.penaltyPoints = penaltyPoints;
    }

    /**
     * Applies the Cave Paintings event effect to all players in the list.
     *
     * <p>Each player gains Food from their Cave Painting buildings, then either
     * loses or gains Prestige Points depending on their number of Artists.</p>
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
            throw new IllegalArgumentException("Players list cannot be null");

        if (players.isEmpty())
            throw new IllegalStateException("Cannot resolve event with no players");

        for (Player p : players) {
            if (p == null)
                throw new IllegalArgumentException("Player cannot be null");

            int totalFoodAmount = p.getTribe().totalFoodByCavePaintingBuildings();
            p.changeFoodAmount(totalFoodAmount);

            int numberOfArtists = p.getTribe().getArtistsCount();

            if (numberOfArtists < penaltyThreshold) {
                p.changePrestigePoints(-penaltyPoints);
                game.notifyEventMessage(p, String.format("CAVE PAINTINGS EVENT: You earn %d foods and lost %d prestige points.", totalFoodAmount, penaltyPoints));
            }
            else {
                int totalPrestigePoints = bonusPointsPerArtist * numberOfArtists;
                p.changePrestigePoints(totalPrestigePoints);
                game.notifyEventMessage(p, String.format("CAVE PAINTINGS EVENT: You earn %d foods and %d prestige points.", totalFoodAmount, totalPrestigePoints));
            }
        }
    }
}
