package it.polimi.ingsw.Cards;
import it.polimi.ingsw.Enum.CharacterType;
import it.polimi.ingsw.Enum.EventType;


import java.util.*;

public class Tribe {
    private Set<BuildingCard> buildingCards; //verify duplicates presence
    private Set<CharacterCard> characterCards; //verify duplicates presence
    Map<CharacterType, Integer> count;
    public Tribe(){
        buildingCards = new HashSet<>();
        characterCards = new HashSet<>();
        count= new EnumMap<>(CharacterType.class);
    }
    public void addCard(CharacterCard card){
        characterCards.add(card);
        /*if(count.containsKey(card.getType())){

        }*/
    }
    public void addCard(BuildingCard card){
        buildingCards.add(card);
    }
    /*public int numberOf(CharacterType characterType){

    }*/
}
