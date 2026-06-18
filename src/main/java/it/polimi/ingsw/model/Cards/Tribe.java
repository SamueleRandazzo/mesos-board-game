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
     *
     * @param p the {@link Player} who owns this tribe; must not be null
     * @throws IllegalArgumentException if the provided player is null
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
     * Initializes the internal map that binds character category strings to their respective lists.
     * This setup facilitates streamlined stream operations over all character cards.
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
     * Calculates the total number of character cards currently contained within the tribe.
     *
     * @return the cumulative count of all character cards across all categories
     */
    public int numberOfCharacterCards() {
        return allCharacterCardsMap.values().stream().mapToInt(List::size).sum();
    }

    //region Getter
    /**
     * Returns the number of Artist cards currently present in the tribe.
     *
     * @return the total count of Artist cards
     */
    public int getArtistsCount() {
        return artists.size();
    }

    /**
     * Returns the number of Gatherer cards currently present in the tribe.
     *
     * @return the total count of Gatherer cards
     */
    public int getGatherersCount() {
        return gatherers.size();
    }

    /**
     * Returns the number of Builder cards currently present in the tribe.
     *
     * @return the total count of Builder cards
     */
    public int getBuildersCount() {
        return builders.size();
    }

    /**
     * Returns the number of Hunter cards currently present in the tribe.
     *
     * @return the total count of Hunter cards
     */
    public int getHuntersCount() {
        return hunters.size();
    }

    /**
     * Returns the number of Inventor cards currently present in the tribe.
     *
     * @return the total count of Inventor cards
     */
    public int getInventorsCount() {
        return inventors.size();
    }

    /**
     * Returns the number of Shaman cards currently present in the tribe.
     *
     * @return the total count of Shaman cards
     */
    public int getShamansCount() {
        return shamans.size();
    }

    /**
     * Retrieves the shamanic attributes tracker associated with this tribe.
     *
     * @return the {@link ShamanicAttributes} instance tracking shaman mechanics
     */
    public ShamanicAttributes getShamanicAttr() {
        return shamanicAttr;
    }

    /**
     * Retrieves an unmodifiable view of the map containing all character cards.
     *
     * @return an unmodifiable {@link Map} of character cards categorized by their type strings
     */
    public Map<String, List<? extends Card>> getAllCharacterCardsMap() {
        return Collections.unmodifiableMap(this.allCharacterCardsMap);
    }

    /**
     * Checks if the player has an active ability allowing them to draw an extra card from the upper row.
     *
     * @return true if the extra card drawing modification is active, false otherwise
     */
    public boolean getExtraCardFromUpper() {
        return this.extraCardFromUpper;
    }

    /**
     * Checks if the player has an active ability granting extra food whenever a bonus is triggered.
     *
     * @return true if the extra food resource modification is active, false otherwise
     */
    public boolean getExtraFoodFromBonus() {
        return this.extraFoodFromBonus;
    }

    /**
     * Aggregates and returns a complete list of all cards (both characters and buildings)
     * currently owned by this tribe.
     *
     * @return a {@link List} containing every {@link Card} instance in the tribe's tableau
     */
    public List<Card> getOwnedCards() {
        List<Card> cards = new ArrayList<>();
        cards.addAll(artists);
        cards.addAll(gatherers);
        cards.addAll(builders);
        cards.addAll(hunters);
        cards.addAll(inventors);
        cards.addAll(shamans);
        cards.addAll(instantEffectBuildings);
        cards.addAll(scoringBuildings);
        cards.addAll(sustenanceBuildings);
        cards.addAll(cavePaintingBuildings);
        cards.addAll(huntBuildings);
        cards.addAll(cardAddedBuildings);
        return cards;
    }
    //endregion

    //region Card Adder
    /**
     * Adds an Artist card to the corresponding list and triggers an evaluation for set bonuses.
     *
     * @param card the {@link Artist} card to be added; must not be null
     * @throws IllegalArgumentException if the card is null
     */
    public void addCard(Artist card) {
        if (card == null)
            throw new IllegalArgumentException("Artist card cannot be null");

        this.artists.add(card);

        this.checkSetBonus();
    }

    /**
     * Adds a Gatherer card to the corresponding list and triggers an evaluation for set bonuses.
     *
     * @param card the {@link Gatherer} card to be added; must not be null
     * @throws IllegalArgumentException if the card is null
     */
    public void addCard(Gatherer card) {
        if (card == null)
            throw new IllegalArgumentException("Gatherer card cannot be null");

        this.gatherers.add(card);

        this.checkSetBonus();
    }

    /**
     * Adds a Builder card to the corresponding list and triggers an evaluation for set bonuses.
     *
     * @param card the {@link Builder} card to be added; must not be null
     * @throws IllegalArgumentException if the card is null
     */
    public void addCard(Builder card) {
        if (card == null)
            throw new IllegalArgumentException("Builder card cannot be null");

        this.builders.add(card);

        this.checkSetBonus();
    }

    /**
     * Adds a Hunter card to the corresponding list. Automatically grants immediate food
     * resources to the owner if the card possesses a food icon, then evaluates set bonuses.
     *
     * @param card the {@link Hunter} card to be added; must not be null
     * @throws IllegalArgumentException if the card is null
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
     * Adds an Inventor card to the corresponding list, handles any active duplicate inventor
     * bonuses granted by card-added buildings, and checks for set bonuses.
     *
     * @param card the {@link Inventor} card to be added; must not be null
     * @throws IllegalArgumentException if the card is null
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
     * Adds a Shaman card to the corresponding list and immediately transfers its
     * printed stars to the tribe's shamanic attributes.
     *
     * @param card the {@link Shaman} card to be added; must not be null
     * @throws IllegalArgumentException if the card is null
     */
    public void addCard(Shaman card) {
        if (card == null)
            throw new IllegalArgumentException("Shaman card cannot be null");

        this.shamans.add(card);
        shamanicAttr.addStars(card.getShamanStars());
    }

    /**
     * Adds an Instant Effect Building to the tribe, updating shamanic tracks and
     * updating persistent modifiers like extra row picks or extra food collection rules.
     *
     * @param card the {@link InstantEffectBuilding} card to add; must not be null
     * @throws IllegalArgumentException if the card is null
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
     * Adds a Scoring Building to the tribe's collection for end-game evaluation.
     *
     * @param card the {@link ScoringBuilding} card to add; must not be null
     * @throws IllegalArgumentException if the card is null
     */
    public void addCard(ScoringBuilding card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        scoringBuildings.add(card);
    }

    /**
     * Adds a Sustenance Building to the tribe's collection, providing continuous food discounts.
     *
     * @param card the {@link SustenanceBuilding} card to add; must not be null
     * @throws IllegalArgumentException if the card is null
     */
    public void addCard(SustenanceBuilding card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        sustenanceBuildings.add(card);
    }

    /**
     * Adds a Cave Painting Building to the tribe's collection, scaling dynamic food generation.
     *
     * @param card the {@link CavePaintingBuilding} card to add; must not be null
     * @throws IllegalArgumentException if the card is null
     */
    public void addCard(CavePaintingBuilding card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        cavePaintingBuildings.add(card);
    }

    /**
     * Adds a Hunt Building to the tribe's collection, boosting late-game food and points.
     *
     * @param card the {@link HuntBuilding} card to add; must not be null
     * @throws IllegalArgumentException if the card is null
     */
    public void addCard(HuntBuilding card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        huntBuildings.add(card);
    }

    /**
     * Adds a Card Added Building to the tribe and captures the current baseline set counts
     * to accurately gauge future milestone progress.
     *
     * @param card the {@link CardAddedBuilding} card to add; must not be null
     * @throws IllegalArgumentException if the card is null
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
     * Calculates the cumulative total prestige points yielded by all scoring buildings in the tribe.
     *
     * @return the sum of prestige points from all scoring buildings
     */
    public int getTotalScoringBuildingsPoints() {
        return instantEffectBuildings.stream().mapToInt(InstantEffectBuilding::getPrestigePoints).sum()
                + sustenanceBuildings.stream().mapToInt(SustenanceBuilding::getPrestigePoints).sum()
                + cavePaintingBuildings.stream().mapToInt(CavePaintingBuilding::getPrestigePoints).sum()
                + huntBuildings.stream().mapToInt(HuntBuilding::getPrestigePoints).sum()
                + cardAddedBuildings.stream().mapToInt(CardAddedBuilding::getPrestigePoints).sum()
                + scoringBuildings.stream().mapToInt(x -> x.getPrestigePoints() + x.getTotalPoints(this)).sum();
    }

    /**
     * Computes how many complete sets of distinct character categories of size {@code setDim}
     * the tribe possesses.
     * <p>
     * The algorithm assesses the sizes of all individual character category lists, sorting them
     * in descending order, and checks the minimum threshold achieved at the targeted rank.
     * </p>
     *
     * @param setDim the required minimum size (dimension) of the distinct card category set; must be greater than 0
     * @return the total number of complete distinct sets currently held
     * @throws IllegalArgumentException if {@code setDim} is less than or equal to 0
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
     * Calculates the total food purchase discount provided by all sustenance buildings in the tribe.
     *
     * @return the total calculated food purchase discount
     */
    public int totalSustenanceDiscount() {
        return sustenanceBuildings.stream().mapToInt(x -> x.getDiscount(this)).sum();
    }

    /**
     * Calculates the total base prestige points provided directly by builder cards within the tribe.
     *
     * @return the sum of prestige points from all builder cards
     */
    public int totalBuildersPoints() {
        return builders.stream().mapToInt(Builder::getPrestigePoints).sum();
    }

    /**
     * Calculates the total construction food discount provided specifically by builder cards.
     *
     * @return the total calculated food discount for building construction
     */
    public int totalBuildersFoodDiscount() {
        return builders.stream().mapToInt(Builder::getFoodDiscount).sum();
    }

    /**
     * Calculates the total food bonus provided by all cave painting buildings in the tribe.
     *
     * @return the total food generated by cave paintings
     */
    public int totalFoodByCavePaintingBuildings() {
        return cavePaintingBuildings.stream().mapToInt(x -> x.getBonusFood(this)).sum();
    }

    /**
     * Calculates the total food bonus provided by all hunt buildings in the tribe.
     *
     * @return the total food generated by hunt buildings
     */
    public int totalFoodByHuntBuildings() {
        return huntBuildings.stream().mapToInt(x -> x.getBonusFood(this)).sum();
    }

    /**
     * Calculates the total extra prestige points provided by all hunt buildings in the tribe.
     *
     * @return the total extra prestige points generated by hunt buildings
     */
    public int totalPointsByHuntBuildings() {
        return huntBuildings.stream().mapToInt(x -> x.getExtraPoints(this)).sum();
    }

    /**
     * Calculates the total number of unique invention icons across all inventor cards in the tribe.
     * Duplicate icons are excluded from this tally.
     *
     * @return the total number of distinct invention icons owned
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
     * Converts the current state of the Tribe into a lightweight {@link TribeStatusDTO} for client-side rendering.
     * This method aggregates character cards into ordered tabular columns, flattens all
     * building cards into a single array, and packs pre-calculated point/discount structures.
     *
     * @return a new, immutable {@link TribeStatusDTO} snapshot representing the current tribe state
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
     * Helper method to extract data transfer objects ({@link CardDTO}s) from a collection of cards.
     *
     * @param cards the list of cards extending {@link Card} to process
     * @return a list of lightweight {@link CardDTO} instances representing the cards' unique IDs
     */
    private List<CardDTO> getIdsFromList(List<? extends Card> cards) {
        return cards.stream()
                .map(card -> new CardDTO(card.getId()))
                .collect(Collectors.toList());
    }
}