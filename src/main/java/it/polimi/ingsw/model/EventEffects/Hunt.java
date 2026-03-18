package it.polimi.ingsw.model.EventEffects;

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
            p.changeFoodAmount(p.getTribe().totalFoodByHuntBuildings());
            p.changePrestigePoints(p.getTribe().totalPointsByHuntBuildings());

            int numberOfHunters = p.getTribe().getHuntersCount();

            p.changeFoodAmount(numberOfHunters);
            p.changePrestigePoints(numberOfHunters * pointsPerCard);
        }
    }
}