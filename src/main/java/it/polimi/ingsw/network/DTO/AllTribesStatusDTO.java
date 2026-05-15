package it.polimi.ingsw.network.DTO;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * Data Transfer Object containing the status of all players' tribes in the game.
 * Allows each client to have a local copy of the entire game state for display purposes.
 */
public class AllTribesStatusDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Map associating player nicknames with their respective TribeStatusDTO. */
    private final Map<String, TribeStatusDTO> allTribes;

    /**
     * Constructs the DTO with a map of all tribes.
     * @param allTribes A map where keys are nicknames and values are tribe statuses.
     */
    public AllTribesStatusDTO(Map<String, TribeStatusDTO> allTribes) {
        this.allTribes = Map.copyOf(allTribes);
    }

    /**
     * Returns an unmodifiable map of all tribes.
     * @return map of nicknames to tribe statuses.
     */
    public Map<String, TribeStatusDTO> getAllTribes() {
        return Collections.unmodifiableMap(allTribes);
    }
}