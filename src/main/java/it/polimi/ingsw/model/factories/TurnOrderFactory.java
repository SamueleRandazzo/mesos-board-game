package it.polimi.ingsw.model.factories;

import it.polimi.ingsw.model.Board.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A factory class responsible for generating the turn order tracking structures based on the total player count.
 * <p>
 * This factory isolates the configuration of turn order positions and maps how turn priority
 * impacts a player's resource management via food modifiers (e.g., earlier turn slots might yield
 * extra food, while later slots might inflict a penalty).
 * </p>
 *
 * @see TurnOrderTile
 * @see TurnOrderSlot
 */
public class TurnOrderFactory {

    /**
     * Immutable mapping containing the static food modifier configurations for each turn order layout.
     * <p>
     * <b>Key:</b> The total number of players in the match (supported values: 2, 3, 4, or 5).<br>
     * <b>Value:</b> An ordered {@link List} of integers representing the specific food modifier applied
     * to each progressive slot position.
     * </p>
     * For instance, a 3-player match grants {@code +2} food to the 1st slot, {@code 0} to the 2nd, and {@code -1} to the 3rd.
     */
    private static final Map<Integer, List<Integer>> config = Map.of(
            2, List.of(1, -1),
            3, List.of(2, 0, -1),
            4, List.of(2, 1, 0, -1),
            5, List.of(3, 1, 0, 0, -1)
    );

    /**
     * Generates a fully populated {@link TurnOrderTile} calibrated for the specified number of players.
     * <p>
     * This method retrieves the corresponding modifier list from the internal static configuration,
     * builds a sequential collection of individual {@link TurnOrderSlot} instances, and bundles them
     * into a fresh track layout.
     * </p>
     *
     * @param numOfPlayers the total number of players participating in the match
     * @return a newly instantiated {@link TurnOrderTile} configured with the appropriate operational slots
     * @throws IllegalArgumentException if the provided player count does not match any key inside the configuration map
     */
    public static TurnOrderTile createTrack(int numOfPlayers){

        List<Integer> modifiers = config.get(numOfPlayers);

        if(modifiers == null){
            throw new IllegalArgumentException("Invalid player count: " + numOfPlayers);
        }

        List<TurnOrderSlot> slots = new ArrayList<>();
        for(int mod : modifiers){
            slots.add(new TurnOrderSlot(mod));
        }

        return new TurnOrderTile(slots);
    }
}