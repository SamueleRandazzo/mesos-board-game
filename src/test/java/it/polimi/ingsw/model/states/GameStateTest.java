package it.polimi.ingsw.model.states;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Enum.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    private GameState state = new GameState() {};

    private Game dummyGame() {
        return null; // non serve realmente per questi test
    }

    private Player dummyPlayer() {
        return new Player(Color.RED, "p1");
    }

    @Test
    void placeTotem_shouldThrowByDefault() {
        assertThrows(IllegalStateException.class,
                () -> state.placeTotem(dummyGame(), dummyPlayer(), 0));
    }

    @Test
    void endTurn_shouldThrowByDefault() {
        assertThrows(IllegalStateException.class,
                () -> state.endTurn(dummyGame(), dummyPlayer()));
    }

    @Test
    void resolveUpperCardPick_shouldThrowByDefault() {
        assertThrows(IllegalStateException.class,
                () -> state.resolveUpperCardPick(dummyGame(), dummyPlayer(), 0));
    }

    @Test
    void resolveLowerCardPick_shouldThrowByDefault() {
        assertThrows(IllegalStateException.class,
                () -> state.resolveLowerCardPick(dummyGame(), dummyPlayer(), 0));
    }
}