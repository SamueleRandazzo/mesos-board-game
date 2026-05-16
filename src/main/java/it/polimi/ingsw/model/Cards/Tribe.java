package it.polimi.ingsw.model.Cards;

import java.util.*;
import java.util.stream.Collectors;
import it.polimi.ingsw.model.BuildingCards.*;
import it.polimi.ingsw.model.CharacterCards.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Utility.*;
import it.polimi.ingsw.network.DTO.CardDTO;
import it.polimi.ingsw.network.DTO.TribeStatusDTO;

/**
 * Represents the collection of characters, buildings, and attributes owned by a specific player.
 * <p>
 * This class acts as the central hub for a player's tableau. It handles the logic for adding cards,
 * calculating tribe-wide bonuses, resolving set mechanics, and generating a data transfer object (DTO)
 * for network transmission using lightweight identifiers.
 * </p>
 */
public class Tribe {
    private final Player owner;

    private final List<InstantEffectBuilding> instantEffectBuildings;
    private final List<ScoringBuilding> scoringBuildings;
    private final List<SustenanceBuilding> sustenanceBuildings;
    private final List<CavePaintingBuilding> cavePaintingBuildings;
    private final List<HuntBuilding> huntBuildings;
    private final List<CardAddedBuilding> cardAddedBuildings;

    private final List<Artist> artists;
    private final List<Gatherer> gatherers;
    private final List<Builder> builders;
    private final List<Hunter> hunters;
    private final List<Inventor> inventors;
    private final List<Shaman> shamans;
    private Map<String, List<? extends Card>> allCharacterCardsMap;

    private final ShamanicAttributes shamanicAttr;
    private boolean extraCardFromUpper;
    private boolean extraFoodFromBonus;

    /**
     * Creates an empty tribe with no cards for the specified player.
     * * @param p The player who owns this tribe. Must not be null.
     * @throws IllegalArgumentException if the player is null.
     */
    public Tribe(Player p) {
        if (p == null)
            throw new IllegalArgumentException("Tribe must have an owner");

        this.owner = p;

        instantEffectBuildings = new ArrayList<>();
        scoringBuildings = new ArrayList<>();
        sustenanceBuildings = new ArrayList<>();
        cavePaintingBuildings = new ArrayList<>();
        huntBuildings = new ArrayList<>();
        cardAddedBuildings = new ArrayList<>();

        artists = new ArrayList<>();
        gatherers = new ArrayList<>();
        builders = new ArrayList<>();
        hunters = new ArrayList<>();
        inventors = new ArrayList<>();
        shamans = new ArrayList<>();

        initializeMap();

        shamanicAttr = new ShamanicAttributes();
        extraCardFromUpper = false;
        extraFoodFromBonus = false;
    }

    /**
     * Initializes the map that binds character category strings to their respective lists.
     */
    public void initializeMap() {
        allCharacterCardsMap = new HashMap<>();

        allCharacterCardsMap.put("ARTIST", artists);
        allCharacterCardsMap.put("GATHERER", gatherers);
        allCharacterCardsMap.put("BUILDER", builders);
        allCharacterCardsMap.put("HUNTERS", hunters);
        allCharacterCardsMap.put("INVENTORS", inventors);
        allCharacterCardsMap.put("SHAMANS", shamans);
    }

    /**
     * Calculates the total number of character cards in the tribe.
     * * @return The total number of character cards.
     */
    public int numberOfCharacterCards() {
        return allCharacterCardsMap.values().stream().mapToInt(List::size).sum();
    }

    //region Getter
    /**
     * Returns the number of Artist cards currently in the list.
     * @return The total count of Artist cards.
     */
    public int getArtistsCount() {
        return artists.size();
    }

    /**
     * Returns the number of Gatherer cards currently in the list.
     * @return The total count of Gatherer cards.
     */
    public int getGatherersCount() {
        return gatherers.size();
    }

    /**
     * Returns the number of Builder cards currently in the list.
     * @return The total count of Builder cards.
     */
    public int getBuildersCount() {
        return builders.size();
    }

    /**
     * Returns the number of Hunter cards currently in the list.
     * @return The total count of Hunter cards.
     */
    public int getHuntersCount() {
        return hunters.size();
    }

    /**
     * Returns the number of Inventor cards currently in the list.
     * @return The total count of Inventor cards.
     */
    public int getInventorsCount() {
        return inventors.size();
    }

    /**
     * Returns the number of Shaman cards currently in the list.
     * @return The total count of Shaman cards.
     */
    public int getShamansCount() {
        return shamans.size();
    }

    /**
     * Retrieves the shamanic attributes associated with this tribe.
     * * @return The shamanic attributes object.
     */
    public ShamanicAttributes getShamanicAttr() {
        return shamanicAttr;
    }

    /**
     * Retrieves an unmodifiable view of the map containing all character cards.
     * * @return An unmodifiable map of character cards categorized by type.
     */
    public Map<String, List<? extends Card>> getAllCharacterCardsMap() {
        return Collections.unmodifiableMap(this.allCharacterCardsMap);
    }

    public boolean getExtraCardFromUpper() {
        return this.extraCardFromUpper;
    }

    public boolean getExtraFoodFromBonus() {
        return this.extraFoodFromBonus;
    }
    //endregion

    //region Card Adder
    /**
     * Adds an Artist card to the corresponding list and checks for set bonuses.
     * @param card The Artist card to be added. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(Artist card) {
        if (card == null)
            throw new IllegalArgumentException("Artist card cannot be null");

        this.artists.add(card);

        this.checkSetBonus();
    }

    /**
     * Adds a Gatherer card to the corresponding list and checks for set bonuses.
     * @param card The Gatherer card to be added. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(Gatherer card) {
        if (card == null)
            throw new IllegalArgumentException("Gatherer card cannot be null");

        this.gatherers.add(card);

        this.checkSetBonus();
    }

    /**
     * Adds a Builder card to the corresponding list and checks for set bonuses.
     * @param card The Builder card to be added. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(Builder card) {
        if (card == null)
            throw new IllegalArgumentException("Builder card cannot be null");

        this.builders.add(card);

        this.checkSetBonus();
    }

    /**
     * Adds a Hunter card to the corresponding list. Grants immediate food if the card has a food icon.
     * @param card The Hunter card to be added. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(Hunter card) {
        if (card == null)
            throw new IllegalArgumentException("Hunter card cannot be null");

        this.hunters.add(card);

        if (card.hasFoodIcon()) {
            owner.changeFoodAmount(hunters.size());
        }

        this.checkSetBonus();
    }

    /**
     * Adds an Inventor card to the corresponding list and triggers specific building bonuses.
     * @param card The Inventor card to be added. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(Inventor card) {
        if (card == null)
            throw new IllegalArgumentException("Inventor card cannot be null");

        this.inventors.add(card);

        for (CardAddedBuilding building : cardAddedBuildings) {
            if (building.isBonusOnDuplicateInventor()) {
                long count = inventors.stream()
                        .filter(i -> i.getInventionIcon().equals(card.getInventionIcon()))
                        .count();

                if (count % 2 == 0) {
                    this.owner.changeFoodAmount(building.getFoodBonus());
                }
            }
        }

        this.checkSetBonus();
    }

    /**
     * Adds a Shaman card to the corresponding list and updates tribe shaman stars.
     * @param card The Shaman card to be added. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(Shaman card) {
        if (card == null)
            throw new IllegalArgumentException("Shaman card cannot be null");

        this.shamans.add(card);
        shamanicAttr.addStars(card.getShamanStars());
    }

    /**
     * Adds an Instant Effect Building to the tribe and updates persistent attributes.
     * @param card The InstantEffectBuilding card to add. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(InstantEffectBuilding card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        instantEffectBuildings.add(card);
        shamanicAttr.setParamByBuilding(card);
        extraCardFromUpper = extraCardFromUpper || card.isExtraCardFromUpper();
        extraFoodFromBonus = extraFoodFromBonus || card.isExtraFoodFromBonus();
    }

    /**
     * Adds a Scoring Building to the tribe.
     * @param card The ScoringBuilding card to add. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(ScoringBuilding card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        scoringBuildings.add(card);
    }

    /**
     * Adds a Sustenance Building to the tribe.
     * @param card The SustenanceBuilding card to add. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(SustenanceBuilding card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        sustenanceBuildings.add(card);
    }

    /**
     * Adds a Cave Painting Building to the tribe.
     * @param card The CavePaintingBuilding card to add. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(CavePaintingBuilding card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        cavePaintingBuildings.add(card);
    }

    /**
     * Adds a Hunt Building to the tribe.
     * @param card The HuntBuilding card to add. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(HuntBuilding card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        huntBuildings.add(card);
    }

    /**
     * Adds a Card Added Building to the tribe and sets its initial requirement counts.
     * @param card The CardAddedBuilding card to add. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(CardAddedBuilding card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        cardAddedBuildings.add(card);

        if (card.isBonusOnSetCharacters()) {
            card.setInitialSetCount(this.getSetCountOfDifferentCard(card.getSetDim()));
        }
    }
    //endregion

    /**
     * Calculates the total points granted by all scoring buildings.
     * @return total extra points by scoring buildings
     */
    public int getTotalScoringBuildingsPoints() {
        return scoringBuildings.stream().mapToInt(x -> x.getTotalPoints(this)).sum();
    }

    /**
     * Computes how many complete sets of different cards of size {@code setDim}
     * the tribe possesses.
     *
     * @param setDim size of the set (must be > 0)
     * @return number of complete sets
     * @throws IllegalArgumentException if setDim <= 0
     */
    public int getSetCountOfDifferentCard(int setDim) {
        if (setDim <= 0)
            throw new IllegalArgumentException("setDim must be greater than 0");

        return allCharacterCardsMap.values().stream()
                .map(List::size)
                .sorted(Comparator.reverseOrder())
                .skip(setDim - 1)
                .findFirst()
                .orElse(0);
    }

    /**
     * Calculates the total food discount provided by sustenance buildings.
     * @return total food discount by sustenance buildings
     */
    public int totalSustenanceDiscount() {
        return sustenanceBuildings.stream().mapToInt(x -> x.getDiscount(this)).sum();
    }

    /**
     * Calculates the total prestige points provided directly by builder cards.
     * @return total points of builder cards
     */
    public int totalBuildersPoints() {
        return builders.stream().mapToInt(Builder::getPrestigePoints).sum();
    }

    /**
     * Calculates the total food discount for buildings provided by builder cards.
     * @return total food discount of builder cards
     */
    public int totalBuildersFoodDiscount() {
        return builders.stream().mapToInt(Builder::getFoodDiscount).sum();
    }

    /**
     * Calculates the total food bonus provided by cave painting buildings.
     * @return total food from cave paintings buildings
     */
    public int totalFoodByCavePaintingBuildings() {
        return cavePaintingBuildings.stream().mapToInt(x -> x.getBonusFood(this)).sum();
    }

    /**
     * Calculates the total food bonus provided by hunt buildings.
     * @return total food from hunt buildings
     */
    public int totalFoodByHuntBuildings() {
        return huntBuildings.stream().mapToInt(x -> x.getBonusFood(this)).sum();
    }

    /**
     * Calculates the total prestige points provided by hunt buildings.
     * @return total points from hunt buildings
     */
    public int totalPointsByHuntBuildings() {
        return huntBuildings.stream().mapToInt(x -> x.getExtraPoints(this)).sum();
    }

    /**
     * Calculates the total number of distinct inventor icons owned by the tribe.
     * @return total number of different inventor icons
     */
    public int totalDifferentInventorIcon() {
        return Math.toIntExact(inventors.stream().map(Inventor::getInventionIcon).distinct().count());
    }

    /**
     * Processes food rewards for completing new sets of unique character cards.
     * <p>
     * Checks if the current number of completed sets exceeds the sum of sets
     * owned at purchase and those already rewarded. If a new set is detected,
     * it grants the food bonus and increments the reward counter to prevent
     * multiple payouts for the same set.
     * </p>
     *
     * @see CardAddedBuilding#getInitialSetCount()
     * @see CardAddedBuilding#getRewardedSetCount()
     */
    private void checkSetBonus() {
        for (CardAddedBuilding building : cardAddedBuildings) {
            if (building.isBonusOnSetCharacters()) {
                int setCountRequired = building.getInitialSetCount() + building.getRewardedSetCount();
                if (getSetCountOfDifferentCard(building.getSetDim()) > setCountRequired) {
                    this.owner.changeFoodAmount(building.getFoodBonus());
                    building.incrementRewardedSetCount();
                }
            }
        }
    }

    /**
     * Converts the current state of the Tribe into a TribeStatusDTO for the View.
     * This method aggregates all character cards into columns and summarizes
     * all building cards into a single list, while pre-calculating game totals.
     *
     * @return A new TribeStatusDTO representing the current tribe state.
     */
    public TribeStatusDTO toDTO() {
        // We use LinkedHashMap to ensure the View receives the columns in a specific, consistent order
        LinkedHashMap<String, List<CardDTO>> charColumns = new LinkedHashMap<>();

        // Mapping character cards to their IDs, column by column
        charColumns.put("ARTISTS", getIdsFromList(this.artists));
        charColumns.put("GATHERERS", getIdsFromList(this.gatherers));
        charColumns.put("BUILDERS", getIdsFromList(this.builders));
        charColumns.put("HUNTERS", getIdsFromList(this.hunters));
        charColumns.put("INVENTORS", getIdsFromList(this.inventors));
        charColumns.put("SHAMANS", getIdsFromList(this.shamans));

        // Consolidating all building types into a single flat list of IDs
        List<CardDTO> allBuildingIds = new ArrayList<>();
        allBuildingIds.addAll(getIdsFromList(this.instantEffectBuildings));
        allBuildingIds.addAll(getIdsFromList(this.scoringBuildings));
        allBuildingIds.addAll(getIdsFromList(this.sustenanceBuildings));
        allBuildingIds.addAll(getIdsFromList(this.cavePaintingBuildings));
        allBuildingIds.addAll(getIdsFromList(this.huntBuildings));
        allBuildingIds.addAll(getIdsFromList(this.cardAddedBuildings));

        // Pre-calculating totals for the View
        int totalPrestige = owner.getPrestigePoints();
        int currentFood = owner.getFoodAmount();
        int totalSustenanceFoodDiscount = totalSustenanceDiscount();
        int totalBuildingDiscount = totalBuildersFoodDiscount();
        int stars = shamanicAttr.getStars();

        return new TribeStatusDTO(
                charColumns,
                allBuildingIds,
                totalPrestige,
                currentFood,
                totalSustenanceFoodDiscount,
                totalBuildingDiscount,
                stars,
                this.extraCardFromUpper,
                this.extraFoodFromBonus
        );
    }

    /**
     * Helper method to extract CardDTOs from a list of cards.
     *
     * @param cards The list of cards.
     * @return A list of CardDTOs representing the unique IDs of the cards.
     */
    private List<CardDTO> getIdsFromList(List<? extends Card> cards) {
        return cards.stream()
                .map(card -> new CardDTO(card.getId()))
                .collect(Collectors.toList());
    }
}