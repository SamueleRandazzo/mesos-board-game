package it.polimi.ingsw.network.commands.clientHandler;

import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.network.commands.ClientCommandHandler;
import it.polimi.ingsw.server.SocketVirtualView;

public class SetPlayersHandler implements ClientCommandHandler {
    @Override
    public void handle(String[] args, Lobby lobby, RemoteController controller, SocketVirtualView vView) {
        try {
            int n = Integer.parseInt(args[0]);
            lobby.setTargetPlayers(n);
        } catch (NumberFormatException e) {
            System.err.println("Invalid move attempted: " + e.getMessage());

            vView.onShowError("Insert_a_valid_number");
            vView.askMaxPlayers();
        } catch (Exception e) {
            System.err.println("Invalid move attempted: " + e.getMessage());

            String errorMsg = e.getMessage().replace(" ", "_");
            vView.onShowError(errorMsg);

            vView.askMaxPlayers();
        }
    }
}