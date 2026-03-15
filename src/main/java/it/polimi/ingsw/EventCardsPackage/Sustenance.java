package it.polimi.ingsw.EventCardsPackage;
import it.polimi.ingsw.Cards.*;
import it.polimi.ingsw.Enum.CharacterType;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.Cards.Tribe;

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
                player.changePrestigePoints(player.getFoodAmount() * this.getEra());
                player.setFoodAmount(0);
            }
        }
    }
}
