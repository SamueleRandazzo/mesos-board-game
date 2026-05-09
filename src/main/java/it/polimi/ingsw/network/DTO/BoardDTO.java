package it.polimi.ingsw.network.DTO;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Data Transfer Object representing the current state of the game board.
 * <p>
 * This class provides a snapshot of the visible card rows on the board.
 * By using {@link CardDTO}, it minimizes network traffic and allows the
 * View to render cards using local assets and descriptions based on the card IDs.
 * </p>
 */
public class BoardDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<CardDTO> upperTribeRow;
    private final List<CardDTO> lowerTribeRow;
    private final List<CardDTO> upperBuildingRow;
    private final List<CardDTO> lowerBuildingRow;

    /**
     * Constructs a new BoardDTO with the specified card rows.
     *
     * @param upperTribeRow     Cards in the upper tribe row (Characters/Events).
     * @param lowerTribeRow     Cards in the lower tribe row (Characters/Events).
     * @param upperBuildingRow  Cards in the upper building row.
     * @param lowerBuildingRow  Cards in the lower building row.
     */
    public BoardDTO(List<CardDTO> upperTribeRow,
                    List<CardDTO> lowerTribeRow,
                    List<CardDTO> upperBuildingRow,
                    List<CardDTO> lowerBuildingRow) {
        this.upperTribeRow = List.copyOf(upperTribeRow);
        this.lowerTribeRow = List.copyOf(lowerTribeRow);
        this.upperBuildingRow = List.copyOf(upperBuildingRow);
        this.lowerBuildingRow = List.copyOf(lowerBuildingRow);
    }

    /**
     * @return An unmodifiable list of card IDs in the upper tribe row.
     */
    public List<CardDTO> getUpperTribeRow() {
        return Collections.unmodifiableList(upperTribeRow);
    }

    /**
     * @return An unmodifiable list of card IDs in the lower tribe row.
     */
    public List<CardDTO> getLowerTribeRow() {
        return Collections.unmodifiableList(lowerTribeRow);
    }

    /**
     * @return An unmodifiable list of card IDs in the upper building row.
     */
    public List<CardDTO> getUpperBuildingRow() {
        return Collections.unmodifiableList(upperBuildingRow);
    }

    /**
     * @return An unmodifiable list of card IDs in the lower building row.
     */
    public List<CardDTO> getLowerBuildingRow() {
        return Collections.unmodifiableList(lowerBuildingRow);
    }
}