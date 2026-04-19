package it.polimi.ingsw.model.states;

import it.polimi.ingsw.model.Board.OfferTile;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

import java.util.List;

public class ActionResolutionState extends GameState {

    private static final int MISSING_FOOD_MALUS = -2;
    private int upperPicksLeft;
    private int lowerPicksLeft;
    private boolean hasBoughtBuilding;
    private Player currentActivePlayer;

    public ActionResolutionState(Game context) {
        this.currentActivePlayer = null;
        this.upperPicksLeft = 0;
        this.lowerPicksLeft = 0;
        this.hasBoughtBuilding = false;
    }

    /**
     * Sets up the counters for the active player
     * if they haven't been set up yet.
     */

    private void initializeTurnIfNeeded(Game context, Player activePlayer) {
        //if the active player is not equals to the current one means that new turn is started
        if (this.currentActivePlayer ==  null || !this.currentActivePlayer.equals(activePlayer)) {
            this.currentActivePlayer = activePlayer;

            //find the OfferTile where the new player is
            OfferTile playerTile = findTileForPlayer(context, activePlayer);

            //set the remaining picks reading the OfferTile
            this.upperPicksLeft = playerTile.getTopRowDraws();
            this.lowerPicksLeft = playerTile.getBottomRowDraws();
            this.hasBoughtBuilding = false;
        }
    }

    /**
     * Searches the OfferTrack to find where the player placed their totem.
     */

    private OfferTile findTileForPlayer(Game context, Player player) {
        for (OfferTile tile : context.getOfferTrack().getTiles()) {

            if (player.equals(tile.getPlacedPlayer())) {
                return tile;
            }
        }
        throw new IllegalStateException("Error: Active player has no totem on the Offer Track!");

    }


    // -- ACTION METHODS --
    @Override
    public void resolveUpperCardPick(Game context, Player player, int pos) {
        if (!player.equals(context.getCurrentActivePlayer())) {
            throw new IllegalStateException("It is not " + player.getColor() + "'s turn!");

        }

        initializeTurnIfNeeded(context, player);

        if (upperPicksLeft <= 0) {
            throw new IllegalStateException("You have no upper row picks left!");

        }

        context.executeUpperCardPick(player, pos);

        upperPicksLeft--;

    }

    @Override
    public void resolveLowerCardPick(Game context, Player player, int pos) {
        if (!player.equals(context.getCurrentActivePlayer())) {
            throw new IllegalStateException("It is not " + player.getColor() + "'s turn!");

        }

        initializeTurnIfNeeded(context, player);

        if (lowerPicksLeft <= 0) {
            throw new IllegalStateException("You have no lower row picks left!");

        }

        context.executeLowerCardPick(player, pos);

        lowerPicksLeft--;

    }

    @Override
    public void resolveUpperBuildingPick(Game context, Player player, int pos) {
        if (!player.equals(context.getCurrentActivePlayer())) {
            throw new IllegalStateException("It is not " + player.getColor() + "'s turn!");

        }

        initializeTurnIfNeeded(context, player);

        if (hasBoughtBuilding) {
            throw new IllegalStateException("You have already pick a building this turn");

        }

        int cost = context.getBoard().getUpperBuildingCards().get(pos).getFoodCost();

        if(player.getFoodAmount() < cost){
            throw new IllegalStateException("Not enough food to buy this building!");
        }

        context.executeUpperBuildingPick(player, pos);

        hasBoughtBuilding = true;

        upperPicksLeft--;

    }

    @Override
    public void resolveLowerBuildingPick(Game context, Player player, int pos) {
        if (!player.equals(context.getCurrentActivePlayer())) {
            throw new IllegalStateException("It is not " + player.getColor() + "'s turn!");

        }

        initializeTurnIfNeeded(context, player);

        if (hasBoughtBuilding) {
            throw new IllegalStateException("You have already pick a building this turn");

        }

        int cost = context.getBoard().getLowerBuildingCards().get(pos).getFoodCost();

        if(player.getFoodAmount() < cost){
            throw new IllegalStateException("Not enough food to buy this building!");
        }


        context.executeLowerBuildingPick(player, pos);

        hasBoughtBuilding = true;

        lowerPicksLeft--;
    }

    @Override
    public void endTurn(Game context, Player player){
        if(upperPicksLeft > 0 || lowerPicksLeft > 0){
            throw new IllegalStateException("You must draw all your required tribe cards before ending your turn!");

        }

        //remove the totem from the Offer Track
        OfferTile tile = findTileForPlayer(context, player);

        tile.removeTotem();

        //Place totem on the Turn Order Tile (Automatically finds the first empty slot)
        int foodModifier = context.getTurnOrderTile().placeTotem(player);

        if(foodModifier > 0){
            player.changeFoodAmount(foodModifier);
        } else if(foodModifier < 0) {
            int cost = Math.abs(foodModifier);

            if(player.getFoodAmount() >= cost) {
                player.changeFoodAmount(foodModifier);
            }else {
                player.changePrestigePoints(MISSING_FOOD_MALUS);
            }
        }

        context.advanceTurn();

        //check if the Phase (and round) is over
        if(context.getCurrentPlayerIndex() == (context.getNumPlayers() - 1)) {
            List<Player> nextRoundOrder = context.getTurnOrderTile().getNextRoundOrder();

            context.setTurnOrder(nextRoundOrder);

            context.nextRound();

            context.setState(new TotemPlacementState());
            context.notifyTotemPlacementTurnChanged();
        } else {
            context.notifyActionResultTurnChanged();
        }
    }
}