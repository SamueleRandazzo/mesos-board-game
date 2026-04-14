package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Board.OfferTile;
import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Enum.TileId;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    private static List<TribeDeck> emptyTribeDeckList() {
        return new ArrayList<>();
    }

    private static List<BuildingCard> emptyBuildingList() {
        return new ArrayList<>();
    }

    private static OfferTrack newOfferTrack() {
        List<OfferTile> tiles = new ArrayList<>();
        tiles.add(new OfferTile(0, TileId.A, 1, 0));
        tiles.add(new OfferTile(1, TileId.B, 0, 1));
        return new OfferTrack(tiles);
    }

    private static Game newGame(List<Player> players) {
        return new Game(
                players,
                emptyTribeDeckList(),
                emptyBuildingList(),
                emptyBuildingList(),
                emptyBuildingList(),
                newOfferTrack()
        );
    }

    @Test
    void handleTileSelection_shouldOccupyTileAndAdvanceTurn() throws RemoteException{
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2")
        );

        Game game = newGame(players);
        GameController controller = new GameController(game);

        assertTrue(game.getOfferTrack().getTiles().get(0).isAvailable());
        assertEquals(players.get(0), game.getCurrentActivePlayer());

        controller.handleTileSelection(0);

        assertFalse(game.getOfferTrack().getTiles().get(0).isAvailable());
        assertEquals(players.get(1), game.getCurrentActivePlayer());
    }

    @Test
    void handleEndTurnRequest_shouldAdvanceTurn() throws RemoteException {
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2")
        );

        Game game = newGame(players);
        GameController controller = new GameController(game);

        assertEquals(players.get(0), game.getCurrentActivePlayer());

        controller.handleEndTurnRequest();

        assertEquals(players.get(1), game.getCurrentActivePlayer());
    }
}