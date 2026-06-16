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
    private Player currentActivePlayer;
    private boolean extraCardChoose = false; // Used to permit an extra pick after resolution state

    /**
     * Initializes the ActionResolutionState.
     * @param context the current game context
     */
    public ActionResolutionState(Game context) {
        this.currentActivePlayer = null;
    }

    /**
     * Restores the ActionResolutionState from a saved active player and extra-pick flag.
     *
     * @param context the current game context
     * @param currentActivePlayer the player currently resolving actions
     * @param extraCardChoose true if an extra upper-row card choice is pending
     */
    public ActionResolutionState(Game context,
                                 Player currentActivePlayer,
                                 boolean extraCardChoose) {
        this.currentActivePlayer = currentActivePlayer;
        this.extraCardChoose = extraCardChoose;
    }

    /**
     * Returns the player currently resolving actions in this state.
     *
     * @return the current active player, or {@code null} when not restored from a snapshot
     */
    public Player getCurrentActivePlayerForState() {
        return currentActivePlayer;
    }

    /**
     * Checks whether an extra upper-row card choice is pending.
     *
     * @return {@code true} if the extra card choice flow is active
     */
    public boolean isExtraCardChoose() {
        return extraCardChoose;
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
        if (player.getUpperPick() <= 0) {
            throw new IllegalStateException("You have no upper row picks left!");
        }

        context.executeUpperCardPick(player, pos);
        context.notifyShowTribe(player.getNickname());

        player.changeUpperPick(-1);

        if (player.getLowerPick() == 0 && player.getUpperPick() == 0) {
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
        if (player.getLowerPick() <= 0) {
            throw new IllegalStateException("You have no lower row picks left!");
        }

        context.executeLowerCardPick(player, pos);
        context.notifyShowTribe(player.getNickname());

        player.changeLowerPick(-1);

        if (player.getLowerPick() == 0 && player.getUpperPick() == 0) {
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
        if (player.getUpperPick() <= 0) {
            throw new IllegalStateException("You have no upper row picks left!");
        }

        int cost = context.getBoard().getUpperBuildingCards().get(pos).getFoodCost();
        int realCost = Math.max(cost - player.getTribe().totalBuildersFoodDiscount(), 0);

        if (player.getFoodAmount() < realCost) {
            throw new IllegalStateException("Not enough food to buy this building!");
        }

        context.executeUpperBuildingPick(player, pos);
        context.notifyShowTribe(player.getNickname());

        player.setUpperPick(-1);

        if (player.getLowerPick() == 0 && player.getUpperPick() == 0) {
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
        if (player.getLowerPick() <= 0) {
            throw new IllegalStateException("You have no upper row picks left!");
        }

        int cost = context.getBoard().getLowerBuildingCards().get(pos).getFoodCost();
        int realCost = Math.max(cost - player.getTribe().totalBuildersFoodDiscount(), 0);

        if (player.getFoodAmount() < realCost) {
            throw new IllegalStateException("Not enough food to buy this building!");
        }

        context.executeLowerBuildingPick(player, pos);
        context.notifyShowTribe(player.getNickname());

        player.changeLowerPick(-1);

        if (player.getLowerPick() == 0 && player.getUpperPick() == 0) {
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
        // if flag is true return immediately and show updated board
        if (extraCardChoose) {
            context.notifyOnShowBoard();
            return;
        }

        // Remove the totem from the Offer Track
        OfferTile tile = findTileForPlayer(context, player);
        player.changeFoodAmount(tile.getFoodBonus());
        tile.removeTotem();

        // Place totem on the Turn Order Tile (Automatically finds the first empty slot)
        int foodModifier = context.getTurnOrderTile().placeTotem(player);

        if (foodModifier > 0) {
            int bonus = player.getTribe().getExtraFoodFromBonus() ? 1 : 0;
            player.changeFoodAmount(foodModifier + bonus);
        } else if (foodModifier < 0) {
            int cost = Math.abs(foodModifier);

            if (player.getFoodAmount() >= cost) {
                player.changeFoodAmount(foodModifier);
            } else {
                player.changePrestigePoints(MISSING_FOOD_MALUS);
            }
        }

        // Notify new offer track and updated tribe
        context.notifyOnShowOfferTrack();
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
            this.extraCardChoose = true; // set extra card flag true
            context.nextRound();
            this.extraCardChoose = false; // round ended set extra card flag false
        } else {
            context.notifyActionResultTurnChanged();
        }
    }
}
