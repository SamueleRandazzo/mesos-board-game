package it.polimi.ingsw.model.BuildingCards;

import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Interfaces.CharacterTypeCount;
import it.polimi.ingsw.model.Player;

/**
 * Represents a specialized {@link BuildingCard} that models a Sustenance Building.
 * This building provides a dynamic food cost discount scaled by the number of
 * specific character types present within a player's tribe.
 */
public class SustenanceBuilding extends BuildingCard {
    private final int foodDiscountPerCard;
    private final CharacterTypeCount discountType;

    /**
     * Constructs a new SustenanceBuilding with the specified attributes, discount multiplier, and type filter.
     *
     * @param id                  the unique identifier of the card
     * @param era                 the historical era or age this card belongs to
     * @param minPlayer           the minimum number of players required for this card to be active
     * @param isObtainable        true if the card can currently be acquired by players
     * @param foodCost            the amount of food required to construct/buy this building
     * @param prestigePoints       the raw prestige or victory points awarded by this card
     * @param foodDiscountPerCard the amount of food discount granted per matching character card
     * @param discountType        the {@link CharacterTypeCount} strategy used to count the eligible cards for the discount
     */
    public SustenanceBuilding(String id, int era, int minPlayer, boolean isObtainable, int foodCost, int prestigePoints,
                              int foodDiscountPerCard, CharacterTypeCount discountType) {
        super(id, era, minPlayer, isObtainable, foodCost, prestigePoints);
        this.foodDiscountPerCard = foodDiscountPerCard;
        this.discountType = discountType;
    }

    /**
     * Calculates the total food discount provided by this building based on the contents
     * of the specified tribe.
     *
     * @param t the {@link Tribe} whose cards will be counted to evaluate the discount
     * @return the total calculated food discount (foodDiscountPerCard multiplied by the number of matching cards)
     */
    public int getDiscount(Tribe t) {
        return foodDiscountPerCard * discountType.cardNumber(t);
    }

    /**
     * Applies the effects of this building card to the specified player.
     * This permanently attaches this card to the player's personal {@link Tribe}.
     *
     * @param p the {@link Player} acquiring this building card
     */
    @Override
    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}