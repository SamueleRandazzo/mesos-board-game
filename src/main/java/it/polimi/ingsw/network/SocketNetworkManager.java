package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.client.SocketServerListener;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketNetworkManager extends NetworkManager {
    private PrintWriter out;
    private Socket socket;

    @Override
    public void connect(String ip, int port) throws Exception {
        this.socket = new Socket(ip, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);

        Thread listenerThread = new Thread(new SocketServerListener(socket.getInputStream(), view));
        listenerThread.start();
    }

    @Override
    public void login(Color color, String name) {
        out.println("LOGIN " + name.replace(" ", "_") + " " + color);
        out.flush();
    }

    @Override
    public void setTotalPlayers(int n) {
        out.println("SET_PLAYERS " + n);
    }

    @Override
    public void tileSelection(int tileIndex) {
        // Format: TILE_SELECT nickname index
        String safeNickname = this.nickname.replace(" ", "_");
        out.println("TILE_SELECT " + safeNickname + " " + tileIndex);
        out.flush();
    }

    /**
     * Handles the transmission of a card selection action over the socket connection.
     * <p>
     * It formats the request as a text command combining the action identifier,
     * the player's nickname (with spaces replaced by underscores to maintain
     * parameter integrity), the row prefix, and the card index.
     * </p>
     *
     * @param nickname the nickname of the player making the move.
     * @param prefix   the row identifier (e.g., "U", "B", "BU", "BB").
     * @param n        the index of the chosen card.
     */
    @Override
    protected void handleCardAction(String nickname, String prefix, int n) {
        // Ensure the nickname doesn't break the space-separated protocol
        String safeNickname = nickname.replace(" ", "_");

        // Assuming 'out' is your PrintWriter connected to the socket
        out.println("CARD_SELECT " + safeNickname + " " + prefix + " " + n);
    }

    @Override
    public void seeGlobalLeaderboard(int targetPlayers) {
        out.println("GLOBAL_LEADERBOARD " + targetPlayers);
    }
}