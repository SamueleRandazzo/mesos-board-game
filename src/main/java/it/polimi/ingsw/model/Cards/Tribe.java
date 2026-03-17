package it.polimi.ingsw.model.Cards;

import java.util.*;
import it.polimi.ingsw.model.Enum.CharacterType;
import it.polimi.ingsw.model.Utility.*;

/**
 * Represents the tribe of a player, containing all acquired
 * {@link BuildingCard} and {@link CharacterCard}. The tribe also keeps
 * track of the number of characters of each {@link CharacterType} and
 * manages shamanic attributes.
 *
 * Duplicate cards are automatically ignored thanks to the use of {@link HashSet}.
 */
public class Tribe {

    private final Set<BuildingCard> buildingCards;
    private final Set<CharacterCard> characterCards;
    private final Map<CharacterType, Integer> count;
    private ShamanicAttributes shamanicAttr;

    /**
     * Creates an empty tribe with no cards and zero shamanic stars.
     */
    public Tribe() {
        buildingCards = new HashSet<>();
        characterCards = new HashSet<>();
        count = new EnumMap<>(CharacterType.class);

        for (CharacterType type : CharacterType.values()) {
            count.put(type, 0);
        }

        shamanicAttr = new ShamanicAttributes();
    }

    /**
     * Adds a {@link CharacterCard} to the tribe.
     * If the card is new, the internal counters and shamanic stars are updated.
     *
     * @param card the character card to add (must not be null)
     * @throws IllegalArgumentException if card is null
     */
    public void addCard(CharacterCard card) {
        if (card == null)
            throw new IllegalArgumentException("CharacterCard cannot be null");

        // Update only if the card is actually added (no duplicates)
        if (characterCards.add(card)) {
            count.put(card.getType(), count.get(card.getType()) + 1);
            shamanicAttr.addStarsFromCards(card.getShamanStars());
        }
    }

    /**
     * Adds a {@link BuildingCard} to the tribe.
     * Duplicate cards are ignored automatically.
     *
     * @param card the building card to add (must not be null)
     * @throws IllegalArgumentException if card is null
     */
    public void addCard(BuildingCard card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        buildingCards.add(card);
    }

    /**
     * Returns the number of character cards of a specific type.
     *
     * @param characterType the type to count (must not be null)
     * @return number of cards of that type
     * @throws IllegalArgumentException if characterType is null
     */
    public int numberOf(CharacterType characterType) {
        if (characterType == null)
            throw new IllegalArgumentException("CharacterType cannot be null");

        return count.get(characterType);
    }

    /**
     * @return total number of character cards in the tribe
     */
    public int numberOfCharacterCards() {
        int sum = 0;
        for (CharacterType type : CharacterType.values()) {
            sum += numberOf(type);
        }
        return sum;
    }

    /**
     * Computes the total prestige points of all character cards of a given type.
     *
     * @param type the character type to consider (must not be null)
     * @return total prestige points for that type
     * @throws IllegalArgumentException if type is null
     */
    public int getTotalPrestigePointsByType(CharacterType type) {
        if (type == null)
            throw new IllegalArgumentException("CharacterType cannot be null");

        int total = 0;

        for (CharacterCard c : characterCards) {
            if (c.getType() == type)
                total += c.getPrestigePoints();
        }

        return total;
    }

    /**
     * @return the shamanic attributes associated with this tribe
     */
    public ShamanicAttributes getShamanicAttr() {
        return shamanicAttr;
    }

    /**
     * @return total shamanic stars accumulated by the tribe
     */
    public int getShamanicStars() {
        return shamanicAttr.getStars();
    }

    /**
     * TODO: Computes how many complete sets of different cards of size {@code setDim}
     * the tribe possesses.
     *
     * @param setDim size of the set (must be > 0)
     * @return number of complete sets
     * @throws IllegalArgumentException if setDim <= 0
     */
    public int getSetNumOfDifferentCard(int setDim) {
        if (setDim <= 0)
            throw new IllegalArgumentException("setDim must be greater than 0");

        // TODO: implement logic
        return 0;
    }
}
