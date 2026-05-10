package it.polimi.ingsw.model.Cards;

import it.polimi.ingsw.model.BuildingCards.*;
import it.polimi.ingsw.model.CharacterCards.*;
import it.polimi.ingsw.model.CharacterTypeCounts.*;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.model.Enum.InventionIcon;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TribeTest {

    private Player player;
    private Tribe tribe;

    @BeforeEach
    void setUp() {
        player = new Player(Color.RED, "TestPlayer");
        tribe = player.getTribe();
    }

    @Test
    void testConstructor_NullPlayerThrowsException() {
        // EXECUTE & ASSERT: Tribe must belong to a valid player
        assertThrows(IllegalArgumentException.class, () -> new Tribe(null),
                "Constructor should throw exception if Player is null.");
    }

    @Test
    void testAddBasicCharacters_CountsUpdateCorrectly() {
        // SETUP & EXECUTE: Add various characters without immediate special effects
        tribe.addCard(new Artist("artist_1", 1, 2, true));
        tribe.addCard(new Artist("artist_2", 1, 2, true));
        tribe.addCard(new Gatherer("gatherer_1", 1, 2, true));

        // ASSERT
        assertEquals(2, tribe.getArtistsCount(), "Tribe should have 2 Artists.");
        assertEquals(1, tribe.getGatherersCount(), "Tribe should have 1 Gatherer.");
        assertEquals(3, tribe.numberOfCharacterCards(), "Total characters should be 3.");
    }

    @Test
    void testHunterFoodIconLogic_ImmediateBonusApplied() {
        // SETUP: Player starts with 0 food
        player.setFoodAmount(0);

        // EXECUTE 1: Add a Hunter WITHOUT food icon
        tribe.addCard(new Hunter("hunter_1", 1, 2, true, false));
        // ASSERT 1
        assertEquals(1, tribe.getHuntersCount());
        assertEquals(0, player.getFoodAmount(), "Hunter without icon should not give food.");

        // EXECUTE 2: Add a Hunter WITH food icon.
        // MESOS RULE: Gives 1 food for EVERY Hunter in the tribe (now there are 2)
        tribe.addCard(new Hunter("hunter_2", 1, 2, true, true));

        // ASSERT 2
        assertEquals(2, player.getFoodAmount(), "Player should immediately gain 2 food (1 for each Hunter).");

        // EXECUTE 3: Add another Hunter WITH food icon.
        // Tribe now has 3 Hunters. Should give +3 food immediately.
        tribe.addCard(new Hunter("hunter_3", 1, 2, true, true));

        // ASSERT 3
        assertEquals(5, player.getFoodAmount(), "Player should have 2 + 3 = 5 food.");
    }

    @Test
    void testShamanAttributesUpdate() {
        // EXECUTE: Add a Shaman with 3 stars
        tribe.addCard(new Shaman("shaman_1", 1, 2, true, 3));

        // ASSERT
        assertEquals(1, tribe.getShamansCount());
        assertEquals(3, tribe.getShamanicAttr().getStars(), "Shamanic stars should be updated to 3.");

        // EXECUTE: Add another Shaman with 2 stars
        tribe.addCard(new Shaman("shaman_2", 1, 2, true, 2));

        // ASSERT
        assertEquals(5, tribe.getShamanicAttr().getStars(), "Shamanic stars should accumulate to 5.");
    }

    @Test
    void testBuilderCalculations() {
        // EXECUTE: Add Builders with different discounts and prestige points
        tribe.addCard(new Builder("builder_1", 1, 2, true, 2, 1)); // Discount 2, PP 1
        tribe.addCard(new Builder("builder_2", 1, 2, true, 1, 3)); // Discount 1, PP 3

        // ASSERT
        assertEquals(3, tribe.totalBuildersFoodDiscount(), "Total builder discount should be 2 + 1 = 3.");
        assertEquals(4, tribe.totalBuildersPoints(), "Total builder points should be 1 + 3 = 4.");
    }

    @Test
    void testInventorDifferentIcons() {
        // EXECUTE: Add Inventors with specific icons
        tribe.addCard(new Inventor("inventor_1", 1, 2, true, InventionIcon.CANOE));
        tribe.addCard(new Inventor("inventor_2", 1, 2, true, InventionIcon.CANOE)); // Duplicate
        tribe.addCard(new Inventor("inventor_3", 1, 2, true, InventionIcon.ROPE));

        // ASSERT
        assertEquals(3, tribe.getInventorsCount(), "There should be 3 Inventors total.");
        assertEquals(2, tribe.totalDifferentInventorIcon(), "There should be only 2 UNIQUE icons (Canoe, Rope).");
    }

    @Test
    void testSetCountOfDifferentCards() {
        // SETUP: Add 4 DIFFERENT characters (Artist, Builder, Gatherer, Hunter)
        tribe.addCard(new Artist("artist_1", 1, 2, true));
        tribe.addCard(new Builder("builder_1", 1, 2, true, 0, 0));
        tribe.addCard(new Gatherer("gatherer_1", 1, 2, true));
        tribe.addCard(new Hunter("hunter_1", 1, 2, true, false));

        // ASSERT 1: We have exactly one set of 4 different cards
        assertEquals(1, tribe.getSetCountOfDifferentCard(4), "Should find 1 set of 4 different cards.");

        // ASSERT 2: We don't have a set of 5 different cards yet
        assertEquals(0, tribe.getSetCountOfDifferentCard(5), "Should find 0 sets of 5 different cards.");

        // SETUP: Add duplicates (2 more Artists, 1 more Builder)
        tribe.addCard(new Artist("artist_2", 1, 2, true));
        tribe.addCard(new Artist("artist_3", 1, 2, true));
        tribe.addCard(new Builder("builder_2", 1, 2, true, 0, 0));

        // At this point we have: 3 Artists, 2 Builders, 1 Gatherer, 1 Hunter.
        // We can make ONE set of 4 different cards (1 Art, 1 Bld, 1 Gath, 1 Hunt).
        // The remaining are 2 Artists, 1 Builder (cannot make another set of 4).
        assertEquals(1, tribe.getSetCountOfDifferentCard(4), "Should still only find 1 set of 4 different cards.");

        // But we CAN make TWO sets of 2 different cards (e.g. [Art, Bld] and [Art, Bld])
        assertEquals(2, tribe.getSetCountOfDifferentCard(2), "Should find 2 sets of 2 different cards.");
    }

    @Test
    void testCavePaintingBuildingBonus() {
        // SETUP: Add 3 Artists
        tribe.addCard(new Artist("artist_1", 1, 2, true));
        tribe.addCard(new Artist("artist_2", 1, 2, true));
        tribe.addCard(new Artist("artist_3", 1, 2, true));

        // EXECUTE: Add a Cave Painting building that gives 2 food per Artist
        CavePaintingBuilding building = new CavePaintingBuilding("cave_1", 1, 2, true, 0, 0, 2, new ArtistsCount());
        tribe.addCard(building);

        // ASSERT: 3 Artists * 2 Extra Food = 6 Food total
        assertEquals(6, tribe.totalFoodByCavePaintingBuildings(), "Should calculate 6 food from Cave Painting building.");
    }

    @Test
    void testHuntBuildingBonus() {
        // SETUP: Add 2 Hunters
        tribe.addCard(new Hunter("hunter_1", 1, 2, true, false));
        tribe.addCard(new Hunter("hunter_2", 1, 2, true, false));

        // EXECUTE: Add a Hunt building giving 1 food and 3 points per Hunter
        HuntBuilding building = new HuntBuilding("hunt_1", 1, 2, true, 0, 0, 1, 3, new HuntersCount());
        tribe.addCard(building);

        // ASSERT
        assertEquals(2, tribe.totalFoodByHuntBuildings(), "2 Hunters * 1 Food = 2 Food.");
        assertEquals(6, tribe.totalPointsByHuntBuildings(), "2 Hunters * 3 Points = 6 Points.");
    }

    @Test
    void testSustenanceBuildingBonus() {
        // SETUP: Add 4 Gatherers
        tribe.addCard(new Gatherer("gatherer_1", 1, 2, true));
        tribe.addCard(new Gatherer("gatherer_2", 1, 2, true));
        tribe.addCard(new Gatherer("gatherer_3", 1, 2, true));
        tribe.addCard(new Gatherer("gatherer_4", 1, 2, true));

        // EXECUTE: Add a Sustenance building giving 2 discount per Gatherer
        SustenanceBuilding building = new SustenanceBuilding("sustenance_1", 1, 2, true, 0, 0, 2, new GatherersCount());
        tribe.addCard(building);

        // ASSERT
        assertEquals(8, tribe.totalSustenanceDiscount(), "4 Gatherers * 2 Discount = 8 Total Discount.");
    }

    @Test
    void testCardAddedBuilding_TriggersSetBonusAutomatically() {
        // SETUP: Player starts with 0 food
        player.setFoodAmount(0);

        // Create a CardAddedBuilding that gives 5 Food every time a set of 3 different characters is completed
        // Parameters: era, minPlayer, isObtainable, foodCost, PP, bonusOnDuplicateInventor, bonusOnSetCharacters, foodBonus, setDim
        CardAddedBuilding building = new CardAddedBuilding("card_added_1", 1, 2, true, 0, 0, false, true, 5, 3);
        tribe.addCard(building);

        // EXECUTE 1: Add first 2 distinct cards (No set of 3 yet)
        tribe.addCard(new Artist("artist_1", 1, 2, true));
        tribe.addCard(new Gatherer("gatherer_1", 1, 2, true));

        // ASSERT 1
        assertEquals(0, player.getFoodAmount(), "Set not complete, should not receive food.");

        // EXECUTE 2: Add 3rd distinct card (Completes a set of 3)
        // Note: The addCard method internally calls checkSetBonus()
        tribe.addCard(new Builder("builder_1", 1, 2, true, 0, 0));

        // ASSERT 2
        assertEquals(5, player.getFoodAmount(), "Set of 3 completed! Player should automatically receive 5 food.");

        // EXECUTE 3: Add duplicate cards (Does not complete a NEW set of 3 distinct)
        tribe.addCard(new Artist("artist_2", 1, 2, true));
        assertEquals(5, player.getFoodAmount(), "No new set completed, food remains 5.");
    }
}