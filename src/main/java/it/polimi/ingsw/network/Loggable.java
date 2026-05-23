package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Enum.Color;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The remote gateway interface responsible for handling initial client connections, authentication,
 * and pre-game configuration.
 * <p>
 * This interface represents the primary entry point (bootstrap stub) exposed by the server's registry.
 * It manages the matchmaking entryway lifecycle, allows the room host to define room limits,
 * and provides facilities to fetch global statistical records before entering an isolated gameplay session.
 * </p>
 *
 * @see Remote
 * @see RemoteException
 * @see GameObserver
 */
public interface Loggable extends Remote {

    /**
     * Authenticates a client connection and registers them into the active game lobby.
     * <p>
     * This method binds the client's unique identification parameters and structural push observer
     * link to the server's registry context, establishing the bi-directional RMI communication pipe.
     * </p>
     *
     * @param nickname the unique user profile name chosen by the player
     * @param color    the desired {@link Color} token identity requested by the player for the match
     * @param obs      the client-side {@link GameObserver} remote stub used by the server to push live state synchronization updates
     * @throws RemoteException if a network communication link drop occurs, or if the nickname/color selection
     *                         violates lobby constraints (e.g., duplicate values)
     */
    void login(String nickname, Color color, GameObserver obs) throws RemoteException;

    /**
     * Configures the maximum seating capacity required to automatically seal the lobby and initialize the match.
     * <p>
     * This parameter is typically designated once by the room's host player during the initial staging phase.
     * </p>
     *
     * @param num the definitive number of participants required for this upcoming game session
     * @throws RemoteException if a network communication failure occurs, or if the caller lacks authorization
     *                         to alter room parameters
     */
    void setTargetPlayers(int num) throws RemoteException;

    /**
     * Low-overhead heartbeat frame triggered by remote clients to verify server instance availability
     * and network pipeline integrity before or during registration.
     *
     * @throws RemoteException if the server is unreachable or the network path is disrupted
     */
    void ping() throws RemoteException;

    /**
     * Asynchronously requests the historical global leaderboard dataset stored on the server persistence layer.
     * <p>
     * The server processes the analytical lookup and pushes the resulting {@link it.polimi.ingsw.network.DTO.GlobalLeaderboardDTO}
     * payload back to the client using the provided callback observer reference.
     * </p>
     *
     * @param targetPlayers the specific player count metric filter used to sort or scope the historical database records
     * @param observer      the target {@link GameObserver} remote reference that will intercept and handle the data response dispatch
     * @throws RemoteException if a network or transport exception occurs during query routing
     */
    void getGlobalLeaderboard(int targetPlayers, GameObserver observer) throws RemoteException;
}