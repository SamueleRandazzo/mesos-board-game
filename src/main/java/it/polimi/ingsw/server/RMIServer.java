package it.polimi.ingsw.server;

import it.polimi.ingsw.database.MatchDAO;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.GlobalLeaderboardDTO;
import it.polimi.ingsw.network.DTO.GlobalPlayerRankDTO;
import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.Loggable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * The RMI Server implementation handling remote procedure calls from clients.
 * It manages player logins, game configuration, and leaderboard requests by interacting
 * with the game {@link Lobby} and the database layer.
 */
public class RMIServer extends UnicastRemoteObject implements Loggable {
    /**
     * The game lobby instance where players are managed.
     */
    private final Lobby lobby;

    /**
     * Constructs a new RMIServer instance linked to the specified lobby.
     *
     * @param lobby the game lobby used to handle players and game states
     * @throws RemoteException if a communication-related exception occurs during export
     */
    protected RMIServer(Lobby lobby) throws RemoteException {
        this.lobby = lobby;
    }

    /**
     * Logs a player into the game lobby with a chosen nickname, color, and an observer instance
     * for receiving network callbacks.
     *
     * @param nickname the unique name chosen by the player
     * @param color    the color chosen by the player for their game pieces
     * @param observer the remote observer interface implemented by the client to receive updates
     * @throws RemoteException if an error occurs during the login process or due to network issues
     */
    @Override
    public void login(String nickname, Color color, GameObserver observer) throws RemoteException {
        try {
            lobby.addPlayer(nickname, color, observer);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    /**
     * Sets the total number of players required to start the match.
     *
     * @param num the target number of players for the upcoming game
     * @throws RemoteException if an error occurs while updating the target player count or due to network issues
     */
    @Override
    public void setTargetPlayers(int num) throws RemoteException {
        try {
            lobby.setTargetPlayers(num);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    /**
     * A lightweight heartbeat method used by clients to verify that the RMI server connection is active.
     *
     * @throws RemoteException if the server is unreachable
     */
    @Override
    public void ping() throws RemoteException {
        // Only used to check if RMI server is alive
    }

    /**
     * Retrieves the global leaderboard filtered by match size and pushes it asynchronously
     * back to the client via the provided observer.
     *
     * @param targetPlayers the match size (number of players) to filter the leaderboard statistics by
     * @param observer      the remote observer interface used to send back the leaderboard data
     * @throws RemoteException if a database error occurs or if there is a network issue communicating with the observer
     */
    @Override
    public void getGlobalLeaderboard(int targetPlayers, GameObserver observer) throws RemoteException {
        try {
            List<GlobalPlayerRankDTO> ranks = MatchDAO.getLeaderboard(targetPlayers);
            GlobalLeaderboardDTO leaderboard = new GlobalLeaderboardDTO(ranks);

            observer.onDisplayGlobalLeaderboard(leaderboard);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }
}