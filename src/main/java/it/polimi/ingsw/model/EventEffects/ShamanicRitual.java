package it.polimi.ingsw.model.EventEffects;

import it.polimi.ingsw.model.Interfaces.EventEffect;
import it.polimi.ingsw.model.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class ShamanicRitual implements EventEffect {
    private final int victoryPrestigePoints;
    private final int defeatPrestigePoints;

    public ShamanicRitual(int victoryPrestigePoints, int defeatPrestigePoints) {
        this.victoryPrestigePoints = victoryPrestigePoints;
        this.defeatPrestigePoints = defeatPrestigePoints;
    }

    public void resolve(List<Player> players) {
        int maxStars = Integer.MIN_VALUE;
        int minStars = Integer.MAX_VALUE;

        List<Player> maxPlayers = new ArrayList<>();
        List<Player> minPlayers = new ArrayList<>();

        Map<Player, Integer> shamanicStarsForPlayer = new HashMap<>();
        for (Player p: players) {
            shamanicStarsForPlayer.put(p, p.getTribe().getShamanicAttr().getStars());
        }

        for (int stars : shamanicStarsForPlayer.values()) {
            if (stars > maxStars) maxStars = stars;
            if (stars < minStars) minStars = stars;
        }

        for (Map.Entry<Player, Integer> entry : shamanicStarsForPlayer.entrySet()) {
            if (entry.getValue() == maxStars) maxPlayers.add(entry.getKey());
            if (entry.getValue() == minStars) minPlayers.add(entry.getKey());
        }

        for (Player pl : maxPlayers)
            pl.changePrestigePoints(pl.getTribe().getShamanicAttr().isDoubleOnWinning() ? victoryPrestigePoints * 2 : victoryPrestigePoints);

        for (Player pl : minPlayers)
            pl.changePrestigePoints(pl.getTribe().getShamanicAttr().isPreventLoss() ? 0 : defeatPrestigePoints);
    }
}
