package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.Enum.CharacterType;
import it.polimi.ingsw.model.Interfaces.EventEffect;
import it.polimi.ingsw.model.Player;

import java.util.List;

public class Hunt implements EventEffect {
    private final int pointsPerCard;

    public Hunt(int pointsPerCard) {
        this.pointsPerCard = pointsPerCard;
    }

    public void resolve(List<Player> players){
        for (Player p: players) {
            int numberOfHunters = p.getTribe().numberOf(CharacterType.HUNTER);

            p.changeFoodAmount(numberOfHunters);
            p.changePrestigePoints(numberOfHunters * pointsPerCard);
        }
    }
}