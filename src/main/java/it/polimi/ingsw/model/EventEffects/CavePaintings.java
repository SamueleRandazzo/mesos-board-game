package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.Interfaces.EventEffect;
import it.polimi.ingsw.model.Player;

import java.util.List;

public class CavePaintings implements EventEffect {
    private final int penaltyThreshold;
    private final int bonusPointsPerArtists;
    private final int penaltyPoints;

    public CavePaintings(int penaltyThreshold, int bonusPointsPerArtists, int penaltyPoints) {
        this.penaltyThreshold = penaltyThreshold;
        this.bonusPointsPerArtists = bonusPointsPerArtists;
        this.penaltyPoints = penaltyPoints;
    }

    public void resolve(List<Player> players) {
        for(Player p: players) {
            p.changeFoodAmount(p.getTribe().totalFoodByCavePaintingBuildings());

            int numberOfArtists = p.getTribe().getArtistsCount();

            if(numberOfArtists < penaltyThreshold)
                p.changePrestigePoints(penaltyPoints);
            else
                p.changePrestigePoints(bonusPointsPerArtists * numberOfArtists);
        }
    }
}