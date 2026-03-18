package it.polimi.ingsw.model.Cards;

import java.util.*;

import it.polimi.ingsw.model.BuildingCards.*;
import it.polimi.ingsw.model.CharacterCards.*;
import it.polimi.ingsw.model.Utility.*;

public class Tribe {

    private final List<InstantEffectBuilding> instantEffectBuildings;
    private final List<ScoringBuilding> scoringBuildings;
    private final List<SustenanceBuilding> sustenanceBuildings;

    private final List<Artist> artists;
    private final List<Binder> binders;
    private final List<Builder> builders;
    private final List<Hunter> hunters;
    private final List<Inventor> inventors;
    private final List<Shaman> shamans;
    private Map<String, List<? extends Card>> allCardsMap;

    private final ShamanicAttributes shamanicAttr;
    private boolean extraCardFromUpper;
    private boolean extraFoodFromBonus;

    /**
     * Creates an empty tribe with no cards
     */
    public Tribe() {
        instantEffectBuildings = new ArrayList<>();
        scoringBuildings = new ArrayList<>();
        sustenanceBuildings = new ArrayList<>();

        artists = new ArrayList<>();
        binders = new ArrayList<>();
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
        allCardsMap = new HashMap<>();

        allCardsMap.put("ARTIST", artists);
        allCardsMap.put("BINDER", binders);
        allCardsMap.put("BUILDER", builders);
        allCardsMap.put("HUNTERS", hunters);
        allCardsMap.put("INVENTORS", inventors);
        allCardsMap.put("SHAMANS", shamans);
    }

    /**
     * @return total number of character cards in the tribe
     */
    public int numberOfCharacterCards() {
        return allCardsMap.values().stream().mapToInt(List::size).sum();
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
     * Returns the number of Binder cards currently in the list.
     * @return The total count of Binder cards.
     */
    public int getBindersCount() {
        return binders.size();
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
    //endregion

    //region Card Adder
    public void addCard(InstantEffectBuilding card) {
        if (card == null)
            throw new IllegalArgumentException("BuildingCard cannot be null");

        instantEffectBuildings.add(card);
        shamanicAttr.setParamByBuilding(card);
        extraCardFromUpper = extraCardFromUpper || card.isExtraCardFromUpper();
        extraFoodFromBonus = extraFoodFromBonus || card.isExtraFoodFromBonus();
    }

    /**
     * Adds an Artist card to the corresponding list.
     * @param card The Artist card to be added. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(Artist card) {
        if (card == null)
            throw new IllegalArgumentException("Artist card cannot be null");

        this.artists.add(card);
    }

    /**
     * Adds a Binder card to the corresponding list.
     * @param card The Binder card to be added. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(Binder card) {
        if (card == null)
            throw new IllegalArgumentException("Binder card cannot be null");

        this.binders.add(card);
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
    }

    /**
     * Adds a Shaman card to the corresponding list.
     * @param card The Shaman card to be added. Must not be null.
     * @throws IllegalArgumentException if the card is null.
     */
    public void addCard(Shaman card) {
        if (card == null)
            throw new IllegalArgumentException("Shaman card cannot be null");

        this.shamans.add(card);
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
    //endregion

    public int getTotalScoringBuildingsPoints() {
        int i = 0;

        for (ScoringBuilding sb: scoringBuildings)
            i += sb.getTotalPoints(this);

        return i;
    }

    /**
     * TODO: Computes how many complete sets of different cards of size {@code setDim}
     * the tribe possesses.
     *
     * @param setDim size of the set (must be > 0)
     * @return number of complete sets
     * @throws IllegalArgumentException if setDim <= 0
     */
    public int getSetNumOfDifferentCard(int setDim) {
        if (setDim <= 0)
            throw new IllegalArgumentException("setDim must be greater than 0");

        // TODO: implement logic
        return 0;
    }

    public int totalSustenanceDiscount() {
        int i = 0;

        for (SustenanceBuilding sb: sustenanceBuildings)
            i += sb.getDiscount(this);

        return i;
    }

    public int getTotalBuildersPoints() {
        return builders.stream().mapToInt(Builder::getPrestigePoints).sum();
    }
}
