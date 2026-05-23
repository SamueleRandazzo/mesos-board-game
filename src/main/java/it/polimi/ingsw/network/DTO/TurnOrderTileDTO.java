package it.polimi.ingsw.network.DTO;

import it.polimi.ingsw.model.Enum.Color;
import java.io.Serializable;

/**
 * A Data Transfer Object (DTO) representing a single snapshot slot within the turn order track.
 * <p>
 * This class captures the dynamic occupancy state of a turn priority slot, bundling together
 * the player's network identity, their systemic color registration, and the active food resource
 * modifier linked to that explicit sequence position.
 * </p>
 *
 * @see Serializable
 * @see Color
 * @see it.polimi.ingsw.model.Board.TurnOrderSlot
 */
public class TurnOrderTileDTO implements Serializable {

    /** The serial version UID for ensuring structural binary compatibility during network deserialization. */
    private static final long serialVersionUID = 1L;

    private String nickname;
    private Color color;
    private int foodBonus;

    /**
     * Default constructor strictly reserved for automated reflection, object mapping,
     * or network serialization frameworks (e.g., Jackson, RMI).
     */
    protected TurnOrderTileDTO() {}

    /**
     * Constructs a populated TurnOrderTileDTO mapping the status of an explicit turn order slot.
     *
     * @param nickname  the unique profile name of the player currently occupying this slot;
     *                  may be {@code null} if the position is open or unassigned
     * @param color     the {@link Color} identity associated with the occupying player's components;
     *                  may be {@code null} if empty
     * @param foodBonus the structural resource modifier or adjustment applied to whoever claims this spot
     */
    public TurnOrderTileDTO(String nickname, Color color, int foodBonus) {
        this.nickname = nickname;
        this.color = color;
        this.foodBonus = foodBonus;
    }

    /**
     * Retrieves the unique profile name of the player holding this turn priority position.
     *
     * @return a {@link String} containing the player's nickname, or {@code null} if the slot is vacant
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Retrieves the player color profile registered to this specific track node.
     *
     * @return the {@link Color} constant matching the slot occupant, or {@code null} if vacant
     */
    public Color getColor() {
        return color;
    }

    /**
     * Retrieves the food resource modifier rating triggered by this position during phase updates.
     * <p>
     * Positive integers represent resource payouts, zero means neutral priority,
     * and negative integers indicate resource deductions or consumption penalties.
     * </p>
     *
     * @return the operational food modifier integer value
     */
    public int getFoodBonus() {
        return foodBonus;
    }
}