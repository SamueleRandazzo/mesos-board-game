package it.polimi.ingsw.network.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.view.View;

/**
 * Functional interface implementing the Command pattern to process incoming server network packets.
 * <p>
 * Implementations of this interface encapsulate the parsing, validation, and dispatching logic
 * for a specific network command string received from the server. It bridges the low-level
 * network communication layer and the client-side presentation layer ({@link View}).
 * </p>
 * <p>
 * Each command handler isolates its own payload schema, leveraging the shared Jackson
 * {@link ObjectMapper} to deserialize complex argument nodes or attached DTO sub-trees safely.
 * </p>
 *
 * @see View
 * @see com.fasterxml.jackson.databind.ObjectMapper
 */
@FunctionalInterface
public interface ServerCommandHandler {

    /**
     * Decodes and executes the specific server command action using the provided context parameters.
     * <p>
     * When a packet matches this handler's signature, the main network dispatch loop slices the raw
     * frame payloads into tokens, feeding any trailing arguments into {@code args} for local processing.
     * </p>
     *
     * @param args   the raw string array tokens containing command-specific parameters or nested JSON payload blocks
     * @param view   the client {@link View} reference used to trigger UI re-renders, update local states, or prompt inputs
     * @param mapper the centralized, pre-configured Jackson {@link ObjectMapper} instance used for structural DTO deserialization
     */
    void handle(String[] args, View view, ObjectMapper mapper);
}