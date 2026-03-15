package it.polimi.ingsw.model.EventCardsPackage;
import it.polimi.ingsw.Cards.*;
import it.polimi.ingsw.model.Cards.EventCard;
import it.polimi.ingsw.model.Enum.CharacterType;
import it.polimi.ingsw.model.Player;

public class Hunt extends EventCard {
    public Hunt(int era, int minPlayer, boolean isObtainable, boolean isFinal) {
        super(era, minPlayer, isObtainable, isFinal);
    }

    @Override
    protected void eventHandler(Player player){
        int numberOfHunters=player.getTribe().numberOf(CharacterType.HUNTER);
        player.changeFoodAmount(numberOfHunters);
        player.changePrestigePoints(numberOfHunters * getEra());
    }
}
