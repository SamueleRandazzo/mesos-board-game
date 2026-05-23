package it.polimi.ingsw.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.polimi.ingsw.model.Game;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Main coordinator responsible for managing persistent game save files on disk.
 * <p>
 * This manager provides synchronous transactional access to read, write, or drop
 * game session snapshots. It encapsulates a Jackson {@link ObjectMapper} configured with structural
 * indentation and relies on a {@link GameSnapshotMapper} to separate runtime domain mechanics
 * from flat JSON storage nodes.
 * </p>
 * <p>
 * <b>Data Integrity Guardrails:</b> To guarantee fault-tolerant write routines and protect saves
 * from unexpected server breakdowns, power outages, or data corruptions, updates are marshaled
 * into an intermediate temporary file before being committed to the main target path via an
 * atomic move command sequence.
 * </p>
 *
 * @see Game
 * @see GameSnapshotMapper
 * @see com.fasterxml.jackson.databind.ObjectMapper
 */
public class PersistenceManager {

    /** The standard default destination target path reserved for standard single-session save files. */
    public static final Path DEFAULT_SAVE_PATH = Path.of("saves", "current-game.json");

    private final Path savePath;
    private final ObjectMapper objectMapper;
    private final GameSnapshotMapper snapshotMapper;

    /**
     * Constructs a baseline PersistenceManager binding storage interactions to the
     * default path designated by {@link #DEFAULT_SAVE_PATH}.
     */
    public PersistenceManager() {
        this(DEFAULT_SAVE_PATH);
    }

    /**
     * Constructs a custom PersistenceManager binding storage operations to an explicit file path destination.
     * <p>
     * This initialization instantiates and tweaks the local Jackson mapping properties,
     * enforcing pretty-printed output structures for easier administrative debugging.
     * </p>
     *
     * @param savePath the target {@link Path} destination where game data sheets will be maintained
     */
    public PersistenceManager(Path savePath) {
        this.savePath = savePath;
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        this.snapshotMapper = new GameSnapshotMapper();
    }

    /**
     * Checks whether a valid game backup file currently exists at the designated storage path.
     *
     * @return {@code true} if the target backup file exists and can be located; {@code false} otherwise
     */
    public boolean hasSave() {
        return Files.exists(savePath);
    }

    /**
     * Serializes and writes the current state of a live {@link Game} session onto disk in a thread-safe manner.
     * <p>
     * This operation dynamically checks and builds missing parent container folders, flattens the domain tree
     * into a snapshot schema, maps it into a temporary mirror file (suffix {@code .tmp}), and replaces
     * the existing asset file atomically.
     * </p>
     *
     * @param game the live {@link Game} engine context to be persistently encoded
     * @throws IllegalStateException if an underlying {@link IOException} compromises file handles, disk sectors,
     *                               or serialization routines
     */
    public synchronized void saveGame(Game game) {
        try {
            Path parent = savePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Path tempPath = savePath.resolveSibling(savePath.getFileName() + ".tmp");
            objectMapper.writeValue(tempPath.toFile(), snapshotMapper.toSnapshot(game));
            moveAtomically(tempPath, savePath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save game state.", e);
        }
    }

    /**
     * Reads, deserializes, and re-hydrates a saved game backup file into a functional runtime {@link Game} engine instance.
     *
     * @return a live, executable {@link Game} instance restored to the exact state it was in when saved
     * @throws IllegalStateException if the file data is unreadable, corrupted, or incompatible with the current
     *                               structural domain entities
     */
    public synchronized Game loadGame() {
        try {
            GameSnapshot snapshot = objectMapper.readValue(savePath.toFile(), GameSnapshot.class);
            return snapshotMapper.toGame(snapshot);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load saved game state.", e);
        }
    }

    /**
     * Deletes the persistent save file from disk if it exists, effectively clearing the backup state.
     * <p>
     * This method is typically invoked when a game session concludes naturally, ensuring that
     * old, completed match states are not accidentally reloaded on subsequent server restarts.
     * </p>
     *
     * @throws IllegalStateException if the file cannot be deleted due to permission restrictions or access lockouts
     */
    public synchronized void deleteSave() {
        try {
            Files.deleteIfExists(savePath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to delete saved game state.", e);
        }
    }

    /**
     * Retrieves the definitive file path designated for database transactions within this manager.
     *
     * @return the operational target {@link Path} resource
     */
    public Path getSavePath() {
        return savePath;
    }

    /**
     * Executes a secure file swap operation, defaulting to native atomic steps before dropping
     * back to generic structural copy-replacements.
     *
     * @param source the newly prepared file path template acting as the input source
     * @param target the production file destination being overwritten
     * @throws IOException if a physical block crash or streaming block failure disrupts file moving
     */
    private void moveAtomically(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}