package it.polimi.ingsw.model.states;

import it.polimi.ingsw.model.Board.OfferTile;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

public class TotemPlacementState extends GameState {

    @Override
    public void placeTotem(Game context, Player player, int tileIndex) {

        //check if it the player's turn
        if (!player.equals(context.getCurrentActivePlayer())) {
            throw new IllegalStateException("It is not" + player.getColor() + "'s turn!");
        }

        //check if tileIndex is valid
        if (tileIndex < 0 || tileIndex >= context.getOfferTrack().getTiles().size()) {
            throw new IllegalStateException("Invalid tile index: " + tileIndex);
        }

        OfferTile chosenTile = context.getOfferTrack().getTiles().get(tileIndex);

        if (chosenTile.isAvailable()) { //return true if the chosenTile is available

            chosenTile.placeTotem(player);
            context.advanceTurn();

        } else {
            throw new IllegalStateException("The selected tile is already taken!");
        }


        //if all the totem has been placed on the track moves to next state
        if (isPhaseOver(context)) {
            // TODO: Ho commentato questa riga per riuscire a runnare i test, da decomentare quando necessario
            //context.setState(new ActionResolutionState(context));
        }

    }

    /**
     * checks if all players have placed their totem on the offer track
     */
    private boolean isPhaseOver(Game context){
        long placedTotems = context.getOfferTrack().getTiles().stream()
                .filter(tile -> !tile.isAvailable())
                .count();
        return placedTotems == context.getNumPlayers();

    }

}
