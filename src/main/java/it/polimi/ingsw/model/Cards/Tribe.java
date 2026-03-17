package it.polimi.ingsw.model.Cards;
import java.util.Map;

import it.polimi.ingsw.model.Enum.BuildingType;
import it.polimi.ingsw.model.Enum.CharacterType;
import it.polimi.ingsw.model.Utility.*;


import java.util.*;

public class Tribe {
    private final Set<BuildingCard> buildingCards; //verify duplicates presence
    private final Set<CharacterCard> characterCards; //verify duplicates presence
    private final Map<CharacterType, Integer> count;
    private ShamanicAttributes shamanicAttr;

    public Tribe(){
        buildingCards = new HashSet<>();
        characterCards = new HashSet<>();
        count = new EnumMap<>(CharacterType.class);
        for (CharacterType type : CharacterType.values()) {
            count.put(type, 0);
        }
        shamanicAttr = new ShamanicAttributes();
    }

    public int getShamanicStars(){
        return shamanicAttr.getStars();
    }

    public void addCard(CharacterCard card){
        characterCards.add(card);
        count.put(card.getType(), count.get(card.getType()) + 1);
        shamanicAttr.addStarsFromCards(card.getShamanStars());
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

    public ShamanicAttributes getShamanicAttr() {
        return shamanicAttr;
    }
}
