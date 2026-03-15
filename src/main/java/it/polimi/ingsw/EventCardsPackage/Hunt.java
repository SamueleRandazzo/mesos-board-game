package it.polimi.ingsw.EventCardsPackage;
import it.polimi.ingsw.Cards.*;
import it.polimi.ingsw.Enum.CharacterType;
import it.polimi.ingsw.Player;

public class Hunt extends EventCard {
    public Hunt(int era, int minPlayer, boolean isObtainable, boolean isFinal) {
        super(era, minPlayer, isObtainable, isFinal);
    }

    @Override
    public void eventHandler(Player player){
        int numberOfHunters=player.getTribe().numberOf(CharacterType.HUNTER);
        player.changeFoodAmount(numberOfHunters);
        player.changePrestigePoints(numberOfHunters * getEra());
    }
}
