package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Board.OfferTile;
import it.polimi.ingsw.model.Board.OfferTrack;
import it.polimi.ingsw.model.Cards.BuildingCard;
import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Enum.TileId;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FullGameSimulationTest {

    private static final int TOTAL_ROUNDS = 10;

    private static class FakeTribeCard extends Card implements TribeDeck {
        private FakeTribeCard(String id, int era) {
            super(id, era, 2, true);
        }

        @Override
        public void applyTo(Player player) {
            player.changePrestigePoints(1);
        }
    }

    private static class FakeBuildingCard extends BuildingCard {
        private FakeBuildingCard(String id, int era) {
            super(id, era, 2, true, 0, 0);
        }

        @Override
        public void applyTo(Player player) {
            player.changePrestigePoints(2);
        }
    }

    @Test
    void deterministicFullGame_shouldReachEndGameAndKeepConsistentState() {
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2")
        );

        Game game = new Game(
                players,
                deterministicTribeDeck(),
                emptyBuildingList(),
                emptyBuildingList(),
                emptyBuildingList(),
                deterministicOfferTrack()
        );
        game.startGame();

        for (int expectedRound = 1; expectedRound <= TOTAL_ROUNDS; expectedRound++) {
            assertEquals(expectedRound, game.getCurrentRound());

            game.placePlayerTotem(0);
            game.placePlayerTotem(1);

            game.resolveEndTurn();

            assertTrue(
                    game.getBoard().getBottomRow().size() > 0,
                    "The lower row should contain at least one card to pick during round " + expectedRound
            );
            game.resolveLowerCardPlayerPick(0);

            assertTrue(
                    game.getOfferTrack().getTiles().stream().allMatch(OfferTile::isAvailable),
                    "All offer tiles should be available after round " + expectedRound
            );
        }

        assertEquals(TOTAL_ROUNDS + 1, game.getCurrentRound());
        assertEquals(2, game.createLeaderboardDTO().getRankings().size());
        assertEquals(TOTAL_ROUNDS, players.get(1).getPrestigePoints());
        assertThrows(IllegalStateException.class, () -> game.placePlayerTotem(0));
    }

    @Test
    void deterministicFullGame_shouldAdvanceEraAndRotateBuildingRows() {
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2")
        );
        BuildingCard eraOneBuilding = new FakeBuildingCard("era1_building", 1);
        BuildingCard eraTwoBuilding = new FakeBuildingCard("era2_building", 2);

        Game game = new Game(
                players,
                deckWithEraTwoRefill(),
                List.of(eraOneBuilding),
                List.of(eraTwoBuilding),
                emptyBuildingList(),
                deterministicOfferTrack()
        );
        game.startGame();

        assertEquals(1, game.getCurrentEra());

        game.endOfRound();

        assertEquals(2, game.getCurrentEra());
        assertEquals("era2_building", game.getBoard().getUpperBuildingCards().getFirst().getId());
        assertEquals("era1_building", game.getBoard().getLowerBuildingCards().getFirst().getId());
    }

    @Test
    void actionResolution_shouldRequireAllGrantedPicksBeforeTurnEnds() {
        List<Player> players = List.of(
                new Player(Color.RED, "p1"),
                new Player(Color.BLUE, "p2")
        );

        Game game = new Game(
                players,
                deterministicTribeDeck(),
                emptyBuildingList(),
                emptyBuildingList(),
                emptyBuildingList(),
                offerTrackWithUpperAndLowerPick()
        );
        game.startGame();
        game.placePlayerTotem(0);
        game.placePlayerTotem(1);

        assertEquals("p1", game.getCurrentActivePlayer().getNickname());

        game.resolveUpperCardPlayerPick(0);

        assertEquals("p1", game.getCurrentActivePlayer().getNickname());
        assertFalse(game.getOfferTrack().getTiles().get(0).isAvailable());

        game.resolveLowerCardPlayerPick(0);

        assertEquals("p2", game.getCurrentActivePlayer().getNickname());
        assertTrue(game.getOfferTrack().getTiles().get(0).isAvailable());
    }

    private static List<TribeDeck> deterministicTribeDeck() {
        List<TribeDeck> deck = new ArrayList<>();

        addCardsForEra(deck, 1, 24);
        addCardsForEra(deck, 2, 24);
        addCardsForEra(deck, 3, 24);

        return deck;
    }

    private static void addCardsForEra(List<TribeDeck> deck, int era, int amount) {
        for (int i = 0; i < amount; i++) {
            deck.add(new FakeTribeCard("fake_era_" + era + "_" + i, era));
        }
    }

    private static List<TribeDeck> deckWithEraTwoRefill() {
        List<TribeDeck> deck = new ArrayList<>();

        addCardsForEra(deck, 1, 9);
        addCardsForEra(deck, 2, 6);

        return deck;
    }

    private static List<BuildingCard> emptyBuildingList() {
        return new ArrayList<>();
    }

    private static OfferTrack deterministicOfferTrack() {
        List<OfferTile> tiles = new ArrayList<>();
        tiles.add(new OfferTile(0, TileId.A, 0, 0, 2));
        tiles.add(new OfferTile(1, TileId.B, 0, 1, 2));
        return new OfferTrack(tiles);
    }

    private static OfferTrack offerTrackWithUpperAndLowerPick() {
        List<OfferTile> tiles = new ArrayList<>();
        tiles.add(new OfferTile(0, TileId.E, 1, 1, 2));
        tiles.add(new OfferTile(0, TileId.A, 0, 0, 2));
        return new OfferTrack(tiles);
    }
}
