package it.polimi.ingsw.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.polimi.ingsw.model.Game;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class PersistenceManager {
    public static final Path DEFAULT_SAVE_PATH = Path.of("saves", "current-game.json");

    private final Path savePath;
    private final ObjectMapper objectMapper;
    private final GameSnapshotMapper snapshotMapper;

    public PersistenceManager() {
        this(DEFAULT_SAVE_PATH);
    }

    public PersistenceManager(Path savePath) {
        this.savePath = savePath;
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        this.snapshotMapper = new GameSnapshotMapper();
    }

    public boolean hasSave() {
        return Files.exists(savePath);
    }

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

    public synchronized Game loadGame() {
        try {
            GameSnapshot snapshot = objectMapper.readValue(savePath.toFile(), GameSnapshot.class);
            return snapshotMapper.toGame(snapshot);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load saved game state.", e);
        }
    }

    public synchronized void deleteSave() {
        try {
            Files.deleteIfExists(savePath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to delete saved game state.", e);
        }
    }

    public Path getSavePath() {
        return savePath;
    }

    private void moveAtomically(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
