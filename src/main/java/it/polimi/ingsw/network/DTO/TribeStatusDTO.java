package it.polimi.ingsw.network.DTO;

import java.io.Serializable;
import java.util.*;

/**
 * Data Transfer Object representing the current state of a Player's Tribe.
 * <p>
 * This DTO is designed to be consumed by the View. It provides:
 * <ul>
 * <li>Unique identifiers (DTOs) for cards to allow the View to retrieve assets (images/descriptions).</li>
 * <li>Pre-calculated totals to prevent the View from executing game logic.</li>
 * <li>A structured layout to easily render character columns and a building column.</li>
 * </ul>
 */
public class TribeStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Map where the key is the character category name (e.g., "ARTISTS")
     * and the value is a list of CardDTOs belonging to that category.
     * Uses LinkedHashMap to preserve the specific UI column order.
     */
    private LinkedHashMap<String, List<CardDTO>> charactersByColumn;

    /**
     * A flat list of all building cards owned by the tribe represented as CardDTOs.
     */
    private List<CardDTO> buildingIds;

    private int totalPrestigePoints;
    private int currentFood;
    private int totalSustenanceDiscount;
    private int totalBuildingsFoodDiscount;
    private int shamanStars;

    /**
     * Default constructor for serialization frameworks.
     */
    protected TribeStatusDTO() {
    }

    /**
     * Constructs a new TribeStatusDTO.
     *
     * @param charactersByColumn         Ordered map of character types to their respective Card DTOs.
     * @param buildingIds                List of all building Card DTOs.
     * @param totalPrestigePoints        Total points calculated from buildings and characters.
     * @param currentFood                The current amount of food available to the player.
     * @param totalSustenanceDiscount    Total food discount for sustenance event.
     * @param totalBuildingsFoodDiscount Total food discount for buying buildings.
     * @param shamanStars                Total count of shaman stars accumulated.
     */
    public TribeStatusDTO(
            LinkedHashMap<String, List<CardDTO>> charactersByColumn,
            List<CardDTO> buildingIds,
            int totalPrestigePoints,
            int currentFood,
            int totalSustenanceDiscount,
            int totalBuildingsFoodDiscount,
            int shamanStars) {

        this.charactersByColumn = charactersByColumn;
        this.buildingIds = List.copyOf(buildingIds);
        this.totalPrestigePoints = totalPrestigePoints;
        this.currentFood = currentFood;
        this.totalSustenanceDiscount = totalSustenanceDiscount;
        this.totalBuildingsFoodDiscount = totalBuildingsFoodDiscount;
        this.shamanStars = shamanStars;
    }

    /**
     * Returns the character cards organized by their UI columns.
     *
     * @return An unmodifiable view of the character map.
     */
    public Map<String, List<CardDTO>> getCharactersByColumn() {
        return Collections.unmodifiableMap(charactersByColumn);
    }

    /**
     * Returns the DTOs of all buildings owned by the tribe.
     *
     * @return An unmodifiable list of building DTOs.
     */
    public List<CardDTO> getBuildingIds() {
        return buildingIds;
    }

    /**
     * @return The pre-calculated total prestige points.
     */
    public int getTotalPrestigePoints() {
        return totalPrestigePoints;
    }

    /**
     * @return The current food reserves of the player.
     */
    public int getCurrentFood() {
        return currentFood;
    }

    /**
     * @return The total food discount applicable during the Sustenance phase.
     */
    public int getTotalSustenanceDiscount() {
        return totalSustenanceDiscount;
    }

    /**
     * @return The total food discount applicable when buying new buildings.
     */
    public int getTotalBuildingsFoodDiscount() {
        return totalBuildingsFoodDiscount;
    }

    /**
     * @return The total number of Shaman stars.
     */
    public int getShamanStars() {
        return shamanStars;
    }
}