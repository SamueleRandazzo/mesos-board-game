package it.polimi.ingsw.server;

import it.polimi.ingsw.database.MatchDAO;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.GlobalLeaderboardDTO;
import it.polimi.ingsw.network.DTO.GlobalPlayerRankDTO;
import it.polimi.ingsw.network.DTO.TribeStatusDTO;
import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.Loggable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class RMIServer extends UnicastRemoteObject implements Loggable {
    private final Lobby lobby;

    protected RMIServer(Lobby lobby) throws RemoteException {
        this.lobby = lobby;
    }

    @Override
    public void login(String nickname, Color color, GameObserver observer) throws RemoteException {
        try {
            lobby.addPlayer(nickname, color, observer);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public void setTargetPlayers(int num) throws RemoteException {
        try {
            lobby.setTargetPlayers(num);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public void ping() throws RemoteException {
        // Only used to check if RMI server is alive
    }

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

    /**
     * Fetches a specific player's tribe status from the active match/lobby
     * and asynchronously notifies ONLY the requesting client via observer.
     */
    @Override
    public void seeOtherPlayerTribe(String nickname, GameObserver observer) throws RemoteException {
        try {
            TribeStatusDTO tribe = lobby.getTribeStatusByNickname(nickname);

            if (tribe == null) {
                throw new Exception("Player not found.");
            }

            observer.onShowTribe(nickname, tribe);

        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }
}