<<<<<<< Updated upstream:src/main/java/it/polimi/ingsw/model/EventCardsPackage/ShamanicRitual.java
package it.polimi.ingsw.model.EventCardsPackage;

import it.polimi.ingsw.model.Cards.EventCard;
import it.polimi.ingsw.model.Player;
=======
package it.polimi.ingsw.EventCardsPackage;
import it.polimi.ingsw.Cards.*;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.Game;
import java.util.HashMap;
import java.util.Map;
>>>>>>> Stashed changes:src/main/java/it/polimi/ingsw/EventCardsPackage/ShamanicRitual.java

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
        Map<Player, Integer> shamanicStarsForPlayer = new HashMap<>();
        for(Player pl: Game.game().getPlayers())
            shamanicStarsForPlayer.put(pl, pl.getTribe().getShamanicStars());

    }
}
