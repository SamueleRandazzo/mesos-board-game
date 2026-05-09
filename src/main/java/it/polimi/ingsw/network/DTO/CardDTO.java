package it.polimi.ingsw.network.DTO;

import java.io.Serializable;

/**
 * A lightweight Data Transfer Object (DTO) representing a game card.
 * <p>
 * Instead of transmitting the entire card state or its assets over the network,
 * this DTO only carries the unique identifier of the card. The client can then
 * use this ID to retrieve the corresponding visual assets (e.g., images) and
 * detailed descriptions from its local configuration files (e.g., JSON datasets).
 * </p>
 */
public class CardDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The unique identifier of the card. */
    private String cardId;

    /**
     * Default constructor for serialization frameworks.
     */
    protected CardDTO() {

    }

    /**
     * Constructs a new {@code CardDTO} with the specified card ID.
     *
     * @param cardId the unique identifier of the card
     */
    public CardDTO(String cardId) {
        this.cardId = cardId;
    }

    /**
     * Retrieves the unique identifier of the card.
     *
     * @return the card ID
     */
    public String getCardId() {
        return cardId;
    }
}