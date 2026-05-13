package it.polimi.ingsw.model.states;

import it.polimi.ingsw.model.Board.OfferTile;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import java.util.List;

/**
 * State representing the Action Resolution Phase.
 * During this phase, players pick tribe and building cards according
 * to the parameters granted by the OfferTile they placed their totem on.
 */
public class ActionResolutionState extends GameState {

    private static final int MISSING_FOOD_MALUS = -2;
    private int upperPicksLeft;
    private int lowerPicksLeft;
    private boolean hasBoughtBuilding;
    private Player currentActivePlayer;

    /**
     * Initializes the ActionResolutionState.
     * @param context the current game context
     */
    public ActionResolutionState(Game context) {
        this.currentActivePlayer = null;
        this.upperPicksLeft = 0;
        this.lowerPicksLeft = 0;
        this.hasBoughtBuilding = false;
    }

    /**
     * Sets up the counters for the active player
     * if they haven't been initialized for the current turn yet.
     *
     * @param context      the current game context
     * @param activePlayer the player currently executing their turn
     */
    private void initializeTurnIfNeeded(Game context, Player activePlayer) {
        // If the active player is not equal to the current one, it means a new turn has started
        if (this.currentActivePlayer == null || !this.currentActivePlayer.equals(activePlayer)) {
            this.currentActivePlayer = activePlayer;

            // Find the OfferTile where the new player is located
            OfferTile playerTile = findTileForPlayer(context, activePlayer);

            // Set the remaining picks by reading the OfferTile parameters
            this.upperPicksLeft = playerTile.getTopRowDraws();
            this.lowerPicksLeft = playerTile.getBottomRowDraws();
            this.hasBoughtBuilding = false;
        }
    }

    /**
     * Searches the OfferTrack to find the specific tile where the player placed their totem.
     *
     * @param context the current game context
     * @param player  the player to search for
     * @return the OfferTile containing the player's totem
     * @throws IllegalStateException if the player has no totem on the Offer Track
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

    /**
     * Resolves the picking of a tribe card from the upper row.
     *
     * @param context the current game context
     * @param player  the player attempting to pick the card
     * @param pos     the position index of the card in the row
     * @throws IllegalStateException if it's not the player's turn, or they have no picks left
     */
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
        context.notifyShowTribe(player.getNickname());

        upperPicksLeft--;

        if (lowerPicksLeft == 0 && upperPicksLeft == 0) {
            context.resolveEndTurn();
        } else {
            context.notifyOnShowBoard();
            context.notifyActionResultTurnChanged();
        }
    }

    /**
     * Resolves the picking of a tribe card from the lower row.
     *
     * @param context the current game context
     * @param player  the player attempting to pick the card
     * @param pos     the position index of the card in the row
     * @throws IllegalStateException if it's not the player's turn, or they have no picks left
     */
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
        context.notifyShowTribe(player.getNickname());

        lowerPicksLeft--;

        if (lowerPicksLeft == 0 && upperPicksLeft == 0) {
            context.resolveEndTurn();
        } else {
            context.notifyOnShowBoard();
            context.notifyActionResultTurnChanged();
        }
    }

    /**
     * Resolves the picking of a building card from the upper row.
     *
     * @param context the current game context
     * @param player  the player attempting to pick the building
     * @param pos     the position index of the building in the row
     * @throws IllegalStateException if it's not the player's turn, they have already bought a building, or they lack food
     */
    @Override
    public void resolveUpperBuildingPick(Game context, Player player, int pos) {
        if (!player.equals(context.getCurrentActivePlayer())) {
            throw new IllegalStateException("It is not " + player.getColor() + "'s turn!");
        }

        initializeTurnIfNeeded(context, player);

        if (hasBoughtBuilding) {
            throw new IllegalStateException("You have already picked a building this turn!");
        }

        int cost = context.getBoard().getUpperBuildingCards().get(pos).getFoodCost();

        if (player.getFoodAmount() < cost) {
            throw new IllegalStateException("Not enough food to buy this building!");
        }

        context.executeUpperBuildingPick(player, pos);
        context.notifyShowTribe(player.getNickname());

        hasBoughtBuilding = true;
        upperPicksLeft--;

        if (lowerPicksLeft == 0 && upperPicksLeft == 0) {
            context.resolveEndTurn();
        } else {
            context.notifyOnShowBoard();
            context.notifyActionResultTurnChanged();
        }
    }

    /**
     * Resolves the picking of a building card from the lower row.
     *
     * @param context the current game context
     * @param player  the player attempting to pick the building
     * @param pos     the position index of the building in the row
     * @throws IllegalStateException if it's not the player's turn, they have already bought a building, or they lack food
     */
    @Override
    public void resolveLowerBuildingPick(Game context, Player player, int pos) {
        if (!player.equals(context.getCurrentActivePlayer())) {
            throw new IllegalStateException("It is not " + player.getColor() + "'s turn!");
        }

        initializeTurnIfNeeded(context, player);

        if (hasBoughtBuilding) {
            throw new IllegalStateException("You have already picked a building this turn!");
        }

        int cost = context.getBoard().getLowerBuildingCards().get(pos).getFoodCost();

        if (player.getFoodAmount() < cost) {
            throw new IllegalStateException("Not enough food to buy this building!");
        }

        context.executeLowerBuildingPick(player, pos);
        context.notifyShowTribe(player.getNickname());

        hasBoughtBuilding = true;
        lowerPicksLeft--;

        if (lowerPicksLeft == 0 && upperPicksLeft == 0) {
            context.resolveEndTurn();
        } else {
            context.notifyOnShowBoard();
            context.notifyActionResultTurnChanged();
        }
    }

    /**
     * Automatically called to end the current player's turn and apply turn order mechanics.
     * Moves the totem to the Turn Order Track and applies food modifiers.
     *
     * @param context the current game context
     * @param player  the player whose turn is ending
     * @throws IllegalStateException if the player attempts to end their turn without executing mandatory draws
     */
    @Override
    public void endTurn(Game context, Player player) {
        if (upperPicksLeft > 0 || lowerPicksLeft > 0) {
            throw new IllegalStateException("You must draw all your required tribe cards before ending your turn!");
        }

        // Remove the totem from the Offer Track
        OfferTile tile = findTileForPlayer(context, player);
        tile.removeTotem();

        // Place totem on the Turn Order Tile (Automatically finds the first empty slot)
        int foodModifier = context.getTurnOrderTile().placeTotem(player);

        if (foodModifier > 0) {
            player.changeFoodAmount(foodModifier);
        } else if (foodModifier < 0) {
            int cost = Math.abs(foodModifier);

            if (player.getFoodAmount() >= cost) {
                player.changeFoodAmount(foodModifier);
            } else {
                player.changePrestigePoints(MISSING_FOOD_MALUS);
            }
        }

        context.notifyShowTribe(player.getNickname());
        context.advanceTurn();

        // BOARD HAS CHANGED: Send the updated board before asking the next player to pick
        context.notifyOnShowBoard();

        boolean phaseFinished = context.getOfferTrack().getTiles()
                .stream().noneMatch(t -> t.getPlacedPlayer() != null);

        // Check if the Phase (and round) is over
        if (phaseFinished) {
            List<Player> nextRoundOrder = context.getTurnOrderTile().getNextRoundOrder();

            context.setTurnOrder(nextRoundOrder);
            context.setState(new TotemPlacementState());

            context.nextRound();
        } else {
            context.notifyActionResultTurnChanged();
        }
    }
}