package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.Interfaces.EventEffect;
import it.polimi.ingsw.model.Player;
import java.util.List;

public class Sustenance implements EventEffect {
    private final int pointsPenalty;
    public Sustenance(int pointsPenalty) {
        this.pointsPenalty = pointsPenalty;
    }

    public void resolve(List<Player> players){
        for (Player p: players) {
            int toFeed = p.getTribe().numberOfCharacterCards() - p.getTribe().getArtistsCount() * 3 - p.getTribe().totalSustenanceDiscount();

            if (toFeed > 0) {
                p.changeFoodAmount(-toFeed);

                if (p.getFoodAmount() < 0) {
                    p.changePrestigePoints(p.getFoodAmount() * pointsPenalty);
                    p.setFoodAmount(0);
                }
            }
        }
    }
}
