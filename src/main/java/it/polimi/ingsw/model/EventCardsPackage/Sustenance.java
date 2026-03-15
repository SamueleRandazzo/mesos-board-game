package it.polimi.ingsw.model.EventCardsPackage;
import it.polimi.ingsw.Cards.*;
import it.polimi.ingsw.model.Cards.EventCard;
import it.polimi.ingsw.model.Enum.CharacterType;
import it.polimi.ingsw.model.Player;

public class Sustenance extends EventCard {
    public Sustenance(int era, int minPlayer, boolean isObtainable, boolean isFinal) {
        super(era, minPlayer, isObtainable, isFinal);
    }

    @Override
    public void eventHandler(Player player){
        int toFeed=player.getTribe().numberOfCharacterCards() - player.getTribe().numberOf(CharacterType.GATHERER) * 3;
        if(toFeed>0){
            player.changeFoodAmount(-toFeed);
            if(player.getFoodAmount()<0){
                player.changePrestigePoints(player.getFoodAmount() * getEra());
                player.setFoodAmount(0);
            }
        }
    }
}
