package it.polimi.ingsw.network.commands.clientHandler;

import it.polimi.ingsw.database.MatchDAO;
import it.polimi.ingsw.network.DTO.GlobalLeaderboardDTO;
import it.polimi.ingsw.network.DTO.GlobalPlayerRankDTO;
import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.network.commands.ClientCommandHandler;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.server.SocketVirtualView;

import java.util.List;

/**
 * Command handler responsible for processing global leaderboard requests sent by a socket client.
 * It retrieves the top players data from the database layer and dispatches it back to the view.
 */
public class GlobalLeaderboardHandler implements ClientCommandHandler {

    /**
     * Handles the global leaderboard command.
     * <p>
     * The method expects at least one argument in the {@code args} array:
     * <ul>
     * <li>{@code args[0]}: An integer string specifying the number of top entries to retrieve.</li>
     * </ul>
     * </p>
     * It fetches the corresponding rankings using the {@link MatchDAO}, wraps them into a
     * {@link GlobalLeaderboardDTO}, and transmits the data back to the client via the virtual view.
     *
     * @param args       the command arguments containing the number of entries to display.
     * @param lobby      the current game lobby session.
     * @param controller the remote controller instance.
     * @param vView      the virtual view representing the network communication with the client.
     */
    @Override
    public void handle(String[] args, Lobby lobby, RemoteController controller, SocketVirtualView vView) {
        try {
            int n = Integer.parseInt(args[0]);

            List<GlobalPlayerRankDTO> ranks = MatchDAO.getLeaderboard(n);
            GlobalLeaderboardDTO leaderboard = new GlobalLeaderboardDTO(ranks);

            vView.onDisplayGlobalLeaderboard(leaderboard);
        } catch (Exception e) {
            System.err.println("Invalid move attempted: " + e.getMessage());
        }
    }
}