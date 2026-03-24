package it.polimi.ingsw.Controller;

import it.polimi.ingsw.model.Game;

public class GameController {
    private Game game;

    public GameController(Game game) {
        this.game = game;
    }

    // TODO
    public void addPlayer(String nickname) {
        try {

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void startGame() {
        try {
            game.initializeGame();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
