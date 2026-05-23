package it.polimi.ingsw.network.commands;

import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.server.SocketVirtualView;

/**
 * Functional interface implementing the Command pattern to handle incoming client network requests on the server.
 * <p>
 * Implementations of this interface are responsible for decoding, validating, and executing
 * network packets or text strings sent by a client. It acts as the direct entry point for client
 * intents into the server architecture, bridging the network transport layer with the core game controllers.
 * </p>
 * <p>
 * Each handler isolates the domain logic required to process a specific message token, safely interacting
 * with the pre-game structures ({@link Lobby}), the active gameplay coordinator ({@link RemoteController}),
 * or responding directly back to the requesting connection channel via its dedicated network view wrapper.
 * </p>
 *
 * @see Lobby
 * @see RemoteController
 * @see SocketVirtualView
 */
@FunctionalInterface
public interface ClientCommandHandler {

    /**
     * Processes and executes the specific client request within the server runtime context.
     * <p>
     * When the server network dispatch loop intercepts a matching command signature, it extracts
     * the raw argument tokens and invokes this method to apply state modifications or trigger game events.
     * </p>
     *
     * @param args       the raw string tokens containing command parameters, payload values, or packed identifiers
     * @param lobby      the pre-game {@link Lobby} management context, used for handling connection steps, room setups, or matchmaking
     * @param controller the active session's {@link RemoteController}, used to dispatch validated gameplay inputs down to the model layer
     * @param out        the connection-specific {@link SocketVirtualView} representing the single client who initiated the request,
     *                   allowing direct synchronous feedback, error replies, or localized data pushes
     */
    void handle(String[] args, Lobby lobby, RemoteController controller, SocketVirtualView out);
}