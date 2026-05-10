package it.polimi.ingsw.model.Interfaces;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import java.util.List;

public interface EventEffect {
     void resolve(List<Player> p, Game game);
}
