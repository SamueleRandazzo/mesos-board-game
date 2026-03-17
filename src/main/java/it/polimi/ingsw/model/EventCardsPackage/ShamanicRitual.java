package it.polimi.ingsw.model.EventCardsPackage;
import it.polimi.ingsw.model.Cards.EventCard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Game;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class ShamanicRitual extends EventCard {
    private final int victoryPrestigePoints;
    private final int defeatPrestigePoints;
    public ShamanicRitual(int era, int minPlayer, boolean isObtainable, boolean isFinal, int victoryPrestigePoints, int defeatPrestigePoints) {
        super(era, minPlayer, isObtainable, isFinal);
        this.victoryPrestigePoints = victoryPrestigePoints;
        this.defeatPrestigePoints = defeatPrestigePoints;
    }

    @Override
    protected void eventHandler(Player player){
        int maxStars = Integer.MIN_VALUE;
        int minStars = Integer.MAX_VALUE;
        Map<Player, Integer> shamanicStarsForPlayer = new HashMap<>();
        List<Player> maxPlayers = new ArrayList<>();
        List<Player> minPlayers = new ArrayList<>();

        // TODO
        //for(Player pl: Game.game().getPlayers()) shamanicStarsForPlayer.put(pl, pl.getTribe().getShamanicStars());

        for (int stars : shamanicStarsForPlayer.values()) {
            if (stars > maxStars) maxStars = stars;
            if (stars < minStars) minStars = stars;
        }

        for (Map.Entry<Player, Integer> entry : shamanicStarsForPlayer.entrySet()) {
            if (entry.getValue() == maxStars) maxPlayers.add(entry.getKey());
            if (entry.getValue() == minStars) minPlayers.add(entry.getKey());
        }

        for (Player pl : maxPlayers) pl.changePrestigePoints(victoryPrestigePoints);
        for (Player pl : minPlayers) pl.changePrestigePoints(defeatPrestigePoints);
    }
}
