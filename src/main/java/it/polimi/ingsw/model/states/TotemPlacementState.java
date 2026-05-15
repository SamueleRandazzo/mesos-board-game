package it.polimi.ingsw.model.states;

import it.polimi.ingsw.model.Board.OfferTile;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

import java.util.List;
import java.util.stream.Collectors;

public class TotemPlacementState extends GameState {

    @Override
    public void placeTotem(Game context, Player player, int tileIndex) {
        //check if tileIndex is valid
        if (tileIndex < 0 || tileIndex >= context.getOfferTrack().getTiles().size()) {
            throw new IllegalStateException("Invalid tile index.");
        }

        OfferTile chosenTile = context.getOfferTrack().getTiles().get(tileIndex);

        if (chosenTile.isAvailable()) { //return true if the chosenTile is available

            context.executeTotemPlacement(player, tileIndex);
            context.notifyOnTotemPlaced(tileIndex);
            context.advanceTurn();
        } else {
            throw new IllegalStateException("The selected tile is already taken!");
        }

        context.notifyOnShowOfferTrack();

        //if all the totem has been placed on the track moves to next state
        if (isPhaseOver(context)) {
            //Extract the new turn order (left to the right on the Offer Track)
            List<Player> newOrder = context.getOfferTrack().getTiles().stream()
                            .filter(tile -> !tile.isAvailable())
                            .map(OfferTile::getPlacedPlayer)
                            .collect(Collectors.toList());

            //set the new order for Phase 2
            context.setTurnOrder(newOrder);

            context.notifyShowPlayerOrder();

            context.notifyOnShowBoard();

            //Move to next state
            context.setState(new ActionResolutionState(context));

            context.notifyActionResultTurnChanged();

        } else {
            context.notifyTotemPlacementTurnChanged();
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