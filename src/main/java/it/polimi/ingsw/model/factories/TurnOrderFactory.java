package it.polimi.ingsw.model.factories;

import it.polimi.ingsw.model.Board.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TurnOrderFactory {

    /** Immutable Map that contains the configurations of foodModifier for each turnOrderTile,
     * Key: Number of players
     * Value: List of food Modifier for each slot
     */
    private static final Map<Integer, List<Integer>> config = Map.of(
            2, List.of(1, -1),
            3, List.of(2, 0, -1),
            4, List.of(2, 1, 0, -1),
            5, List.of(3, 1, 0, 0, -1)
    );
    /* creates the TurnOrderTile, the param is the number of player of the game and returns
       a configuration TurnOrderTile
     */
    public static TurnOrderTile createTrack(int numOfPlayers){

        List<Integer> modifiers = config.get(numOfPlayers);

        if(modifiers == null){
            throw new IllegalArgumentException("Invalid player count: "+ numOfPlayers);
        }

        List<TurnOrderSlot> slots = new ArrayList<>();
        for(int mod : modifiers){ //scorre la lista di modifiers e mod è l' intero della lista ad ogni iterazione

            slots.add(new TurnOrderSlot((mod)));
        }

        //the creation of a new TurnOrderTile allow to create different matches
        return new TurnOrderTile(slots);

    }
}
