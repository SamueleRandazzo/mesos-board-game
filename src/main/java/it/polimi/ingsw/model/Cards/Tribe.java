package it.polimi.ingsw.model.Cards;

import java.util.*;

import it.polimi.ingsw.model.BuildingCards.*;
import it.polimi.ingsw.model.CharacterCards.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Utility.*;

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
     * Creates an empty tribe with no cards
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
     * @return total number of character cards in the tribe
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
     * @return the shamanic attributes associated with this tribe
     */
    public ShamanicAttributes getShamanicAttr() {
        return shamanicAttr;
    }

    public Map<String, List<? extends Card>> getAllCharacterCardsMap() {
        return Collections.unmodifiableMap(this.allCharacterCardsMap);
    }
    //endregion

    //region Card Adder
    /**
     * Adds an Artist card to the corresponding list.
     * @param card The Artist card to be added. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(Artist card) {
        if (card == null)
            throw new IllegalArgumentException("Artist card cannot be null");

        this.artists.add(card);

        this.checkSet6Bonus();
    }

    /**
     * Adds a Gatherer card to the corresponding list.
     * @param card The Gatherer card to be added. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(Gatherer card) {
        if (card == null)
            throw new IllegalArgumentException("Gatherer card cannot be null");

        this.gatherers.add(card);

        this.checkSet6Bonus();
    }

    /**
     * Adds a Builder card to the corresponding list.
     * @param card The Builder card to be added. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(Builder card) {
        if (card == null)
            throw new IllegalArgumentException("Builder card cannot be null");

        this.builders.add(card);

        this.checkSet6Bonus();
    }

    /**
     * Adds a Hunter card to the corresponding list.
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

        this.checkSet6Bonus();
    }

    /**
     * Adds an Inventor card to the corresponding list.
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

        this.checkSet6Bonus();
    }

    /**
     * Adds a Shaman card to the corresponding list and update tribe shaman stars.
     * @param card The Shaman card to be added. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(Shaman card) {
        if (card == null)
            throw new IllegalArgumentException("Shaman card cannot be null");

        this.shamans.add(card);
        shamanicAttr.addStars(card.getShamanStars());
    }

    public void addCard(InstantEffectBuilding card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        instantEffectBuildings.add(card);
        shamanicAttr.setParamByBuilding(card);
        extraCardFromUpper = extraCardFromUpper || card.isExtraCardFromUpper();
        extraFoodFromBonus = extraFoodFromBonus || card.isExtraFoodFromBonus();
    }

    public void addCard(ScoringBuilding card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        scoringBuildings.add(card);
    }

    public void addCard(SustenanceBuilding card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        sustenanceBuildings.add(card);
    }

    public void addCard(CavePaintingBuilding card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        cavePaintingBuildings.add(card);
    }

    public void addCard(HuntBuilding card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        huntBuildings.add(card);
    }

    public void addCard(CardAddedBuilding card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        cardAddedBuildings.add(card);

        if (card.isBonusOnSet6Characters()) {
            card.setInitialSetCount(this.getSetNumOfDifferentCard(card.getSetDim()));
        }
    }
    //endregion

    /**
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
    public int getSetNumOfDifferentCard(int setDim) {
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
     * @return total food discount by sustenance buildings
     */
    public int totalSustenanceDiscount() {
        return sustenanceBuildings.stream().mapToInt(x -> x.getDiscount(this)).sum();
    }

    /**
     * @return total points of builder cards
     */
    public int totalBuildersPoints() {
        return builders.stream().mapToInt(Builder::getPrestigePoints).sum();
    }

    /**
     * @return total food discount of builder cards
     */
    public int totalBuildersFoodDiscount() {
        return builders.stream().mapToInt(Builder::getFoodDiscount).sum();
    }

    /**
     * @return total points from cave paintings buildings
     */
    public int totalFoodByCavePaintingBuildings() {
        return cavePaintingBuildings.stream().mapToInt(x -> x.getBonusFood(this)).sum();
    }

    /**
     * @return total food from hunt paintings buildings
     */
    public int totalFoodByHuntBuildings() {
        return huntBuildings.stream().mapToInt(x -> x.getBonusFood(this)).sum();
    }

    /**
     * @return total points from hunt paintings buildings
     */
    public int totalPointsByHuntBuildings() {
        return huntBuildings.stream().mapToInt(x -> x.getExtraPoints(this)).sum();
    }

    public int totalDifferentInventorIcon() {
        return Math.toIntExact(inventors.stream().map(Inventor::getInventionIcon).distinct().count());
    }

    private void checkSet6Bonus() {
        for (CardAddedBuilding building : cardAddedBuildings) {
            if (building.isBonusOnSet6Characters()
                    && getSetNumOfDifferentCard(building.getSetDim()) > building.getInitialSetCount()) {
                this.owner.changeFoodAmount(building.getFoodBonus());
            }
        }
    }
}
