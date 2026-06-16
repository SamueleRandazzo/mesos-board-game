package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Interfaces.EventEffect;
import it.polimi.ingsw.model.Player;

import java.util.List;

/**
 * Represents the Sustenance event effect in the game.
 * Each player must feed their tribe based on the total number of character cards they own,
 * applying discounts from Gatherers and specialized sustenance buildings.
 * If a player lacks the necessary food, they lose Prestige Points.
 */
public class Sustenance implements EventEffect {

    private final int pointsPenalty;

    /**
     * Creates a Sustenance event.
     *
     * @param pointsPenalty the positive amount of Prestige Points a player loses
     *                      for each missing unit of food.
     * @throws IllegalArgumentException if pointsPenalty is negative.
     */
    public Sustenance(int pointsPenalty) {
        if (pointsPenalty < 0) {
            throw new IllegalArgumentException("Points penalty must be non-negative.");
        }
        this.pointsPenalty = pointsPenalty;
    }

    /**
     * Applies the Sustenance event to all players.
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
    public void resolve(List<Player> players, Game game) {
        // 1. Initial Validation
        if (players == null) {
            throw new IllegalArgumentException("Players list cannot be null.");
        }
        if (players.isEmpty()) {
            throw new IllegalStateException("Cannot resolve event with no players.");
        }

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

                if (currentFood >= toFeed) {
                    // The player has enough food, just subtract the required amount
                    p.changeFoodAmount(-toFeed);

                    game.notifyEventMessage(p, String.format("SUSTENANCE EVENT: You lost %d food.", toFeed));
                } else {
                    // The player does not have enough food
                    int missingFood = toFeed - currentFood;

                    // Consume all available food (leaving it at 0)
                    p.setFoodAmount(0);

                    // Apply the prestige penalty for the missing food
                    int totalPenalty = missingFood * pointsPenalty;
                    p.changePrestigePoints(-totalPenalty);

                    game.notifyEventMessage(p, String.format("SUSTENANCE EVENT: You lost %d food and %d prestige points.", currentFood, totalPenalty));
                }
            }
        }
    }
}
