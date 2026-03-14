package it.polimi.ingsw.Cards;
import java.util.*;

public class Tribe {
    private Set<BuildingCard> buildingCards; //verify duplicates presence
    private Set<CharacterCard> characterCards; //verify duplicates presence
    public Tribe(){
        buildingCards = new HashSet<>();
        characterCards = new HashSet<>();
    }
    public void addCard(CharacterCard card){
        characterCards.add(card);
    }
    public void addCard(BuildingCard card){
        buildingCards.add(card);
    }

}
