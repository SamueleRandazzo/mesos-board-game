package it.polimi.ingsw.network.commands.clientHandler;

import it.polimi.ingsw.database.MatchDAO;
import it.polimi.ingsw.network.DTO.GlobalLeaderboardDTO;
import it.polimi.ingsw.network.DTO.GlobalPlayerRankDTO;
import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.network.commands.ClientCommandHandler;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.server.SocketVirtualView;

import java.util.List;

public class EndTurnHandler implements ClientCommandHandler {
    @Override
    public void handle(String[] args, Lobby lobby, RemoteController controller, SocketVirtualView vView) {
        try {
            controller.handleEndTurnRequest();
        } catch (Exception e) {
            System.err.println("Invalid move attempted: " + e.getMessage());
        }
    }
}