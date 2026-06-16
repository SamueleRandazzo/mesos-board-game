package it.polimi.ingsw.network.commands.clientHandler;

import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.network.commands.ClientCommandHandler;
import it.polimi.ingsw.server.SocketVirtualView;

/**
 * Command handler responsible for processing requests to set the maximum number of players for a match.
 * This is typically executed by the lobby creator during the initial game setup phase.
 */
public class SetPlayersHandler implements ClientCommandHandler {

    /**
     * Handles the command to configure the lobby's target player capacity.
     * <p>
     * The method expects at least one argument in the {@code args} array:
     * <ul>
     * <li>{@code args[0]}: An integer string representing the desired number of players.</li>
     * </ul>
     * </p>
     * If the input is not a valid integer or if the lobby configuration fails, an error
     * message is sent back to the client, and the view prompts the client again to choose
     * the player count to avoid state lockups.
     *
     * @param args       the command arguments containing the target player count.
     * @param lobby      the current game lobby session to configure.
     * @param controller the remote controller instance.
     * @param vView      the virtual view representing the network communication with the client.
     */
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