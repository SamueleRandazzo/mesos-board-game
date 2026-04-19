package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.Interfaces.EventEffect;
import it.polimi.ingsw.model.Player;

import java.util.List;

/**
 * Represents the Sustenance event effect in the game.
<<<<<<< Updated upstream
 * Each player must feed their tribe based on the total number of character cards they own,
 * applying discounts from Gatherers and specialized sustenance buildings.
 * If a player lacks the necessary food, they lose Prestige Points.
=======
 *
 * <p>Each player must feed their tribe. The required food is computed as:
 * <pre>
 * numberOfCharacterCards
 *   - (gatherersCount * 3)
 *   - totalSustenanceDiscount
 * </pre>
 *
 * <p>If the player cannot pay the required food, they lose Prestige Points.
 * The penalty value is positive and is subtracted during the event logic.
 *
 * <p>This class guarantees that a Player's food amount is never set to a negative value.
 * All negative calculations are handled internally before updating the Player object.
>>>>>>> Stashed changes
 */
public class Sustenance implements EventEffect {

    private final int pointsPenalty;

    /**
     * Creates a Sustenance event.
<<<<<<< Updated upstream
     * * @param pointsPenalty the positive amount of Prestige Points a player loses
     * for each missing unit of food.
     * @throws IllegalArgumentException if pointsPenalty is negative.
     */
    public Sustenance(int pointsPenalty) {
        if (pointsPenalty < 0) {
            throw new IllegalArgumentException("Points penalty must be non-negative.");
        }
=======
     *
     * @param pointsPenalty the positive amount of Prestige Points to subtract
     *                      when a player cannot feed their tribe
     *
     * @throws IllegalArgumentException if pointsPenalty is negative
     */
    public Sustenance(int pointsPenalty) {
        if (pointsPenalty < 0)
            throw new IllegalArgumentException("pointsPenalty must be non-negative");

>>>>>>> Stashed changes
        this.pointsPenalty = pointsPenalty;
    }

    /**
     * Applies the Sustenance event to all players.
<<<<<<< Updated upstream
     * For each player, the required food is calculated as:
     * (Total Characters) - (Gatherers * 3) - (Building Discounts).
     * If the player's personal supply cannot cover the required food,
     * the missing amount is converted into a Prestige Point penalty.
     *
     * @param players the list of players participating in the event.
     * @throws IllegalArgumentException if the players list is null or contains null elements.
     * @throws IllegalStateException if the players list is empty.
     */
    @Override
    public void resolve(List<Player> players) {
        // 1. Initial Validation
        if (players == null) {
            throw new IllegalArgumentException("Players list cannot be null.");
        }
        if (players.isEmpty()) {
            throw new IllegalStateException("Cannot resolve event with no players.");
        }
=======
     *
     * <p>For each player:
     * <ul>
     *     <li>Compute the required food.</li>
     *     <li>If required food is positive, subtract it from the player's food.</li>
     *     <li>If the player does not have enough food, apply a Prestige penalty
     *         based on the missing food and reset food to zero.</li>
     * </ul>
     *
     * <p>This method ensures that the player's food amount never becomes negative.</p>
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

            // Compute required food
            int toFeed = p.getTribe().numberOfCharacterCards()
                    - p.getTribe().getGatherersCount() * 3
                    - p.getTribe().totalSustenanceDiscount();
>>>>>>> Stashed changes

        // 2. Event Resolution
        for (Player p : players) {
            if (p == null) {
                throw new IllegalArgumentException("Player in the list cannot be null.");
            }

            // Calculate how much food is required to feed the tribe
            int toFeed = p.getTribe().numberOfCharacterCards()
                    - (p.getTribe().getGatherersCount() * 3)
                    - p.getTribe().totalSustenanceDiscount();

            // If the tribe requires food (i.e., discounts didn't cover everything)
            if (toFeed > 0) {
                int currentFood = p.getFoodAmount();

<<<<<<< Updated upstream
                if (currentFood >= toFeed) {
                    // The player has enough food, just subtract the required amount
                    p.changeFoodAmount(-toFeed);
                } else {
                    // The player does not have enough food
                    int missingFood = toFeed - currentFood;

                    // Consume all available food (leaving it at 0)
=======
                // If the player has enough food, simply subtract it
                if (currentFood >= toFeed) {
                    p.changeFoodAmount(-toFeed);
                } else {
                    // Player does not have enough food
                    int missingFood = toFeed - currentFood; // always positive

                    // Apply prestige penalty based on missing food
                    int penalty = missingFood * pointsPenalty;
                    p.changePrestigePoints(-penalty);

                    // Set food to zero (never negative)
>>>>>>> Stashed changes
                    p.setFoodAmount(0);

                    // Apply the prestige penalty for the missing food
                    int totalPenalty = missingFood * pointsPenalty;
                    p.changePrestigePoints(-totalPenalty);
                }
            }
        }
    }
}