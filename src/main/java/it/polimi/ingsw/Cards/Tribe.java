package it.polimi.ingsw.Cards;
import java.util.Map;
import it.polimi.ingsw.Enum.CharacterType;


import java.util.*;

public class Tribe {
    private final Set<BuildingCard> buildingCards; //verify duplicates presence
    private final Set<CharacterCard> characterCards; //verify duplicates presence
    private final Map<CharacterType, Integer> count;

    public Tribe(){
        buildingCards = new HashSet<>();
        characterCards = new HashSet<>();
        count= new EnumMap<>(CharacterType.class);
        for (CharacterType type : CharacterType.values()) {
            count.put(type, 0);
        }
    }

    public void addCard(CharacterCard card){
        characterCards.add(card);
        count.put(card.getType(), count.get(card.getType()) + 1);
    }

    public void addCard(BuildingCard card){
        buildingCards.add(card);
    }

    public int numberOf(CharacterType characterType){
        return count.get(characterType);
    }

    public int numberOfCharacterCards(){
        int sum = 0;
        for (CharacterType type : CharacterType.values()) {
            sum += numberOf(type);
        }
        return sum;
    }
}
