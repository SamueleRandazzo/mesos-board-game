package it.polimi.ingsw.model.Interfaces;

import it.polimi.ingsw.model.Cards.*;
import it.polimi.ingsw.model.Enum.*;
import it.polimi.ingsw.model.*;
import java.util.List;

public interface BuildingEffect {
    public void applyEffect(Player p, List<BuildingType> typeList);
}
