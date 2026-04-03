package it.polimi.ingsw.Controller;

import it.polimi.ingsw.model.Game;

public class GameController {
    private Game game;

    public GameController(Game game) {
        this.game = game;
    }

    public void handleTileSelection(int tileIndex) {
        try {
            game.placePlayerTotem(tileIndex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleUpperCardSelection(int pos) {
        try {
            game.resolveUpperCardPlayerPick(pos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleLowerCardSelection(int pos) {
        try {
            game.resolveLowerCardPlayerPick(pos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleUpperBuildingSelection(int pos) {
        try {
            game.resolveUpperBuildingPlayerPick(pos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleLowerBuildingSelection(int pos) {
        try {
            game.resolveLowerBuildingPlayerPick(pos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleEndTurnRequest() {
        try {
            game.resolveEndTurn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
