package it.polimi.ingsw.model.BuildingEffects;

import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Enum.BuildingType;
import it.polimi.ingsw.model.Interfaces.BuildingEffect;
import it.polimi.ingsw.model.*;

import java.util.List;

public class ShamanicRitualEffect implements BuildingEffect {
    private final int extraStars;
    private final boolean preventLoss;
    private final boolean doubleOnWinning;

    ShamanicRitualEffect(int extraStars, boolean preventLoss, boolean doubleOnWinning) {
        this.extraStars = extraStars;
        this.preventLoss = preventLoss;
        this.doubleOnWinning = doubleOnWinning;
    }

    /** Return extra stars */
    public int getExtraStars() {
        return this.extraStars;
    }

    /** Return double points on winning flag */
    public boolean isDoubleOnWinning() {
        return this.doubleOnWinning;
    }

    /** Return prevent loss flag */
    public boolean isPreventLoss() {
        return preventLoss;
    }

    /** Apply building effect */
    public void applyEffect(Player p, List<BuildingType> l) {
        if (!l.contains(BuildingType.SHAMANIC))
            return;
        p.getTribe().getShamanicAttr().addStars(this.getExtraStars());
        p.getTribe().getShamanicAttr().setPreventLoss(this.isPreventLoss());
        p.getTribe().getShamanicAttr().setDoubleOnWinning(this.isDoubleOnWinning());
    }
}
