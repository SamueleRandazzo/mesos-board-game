package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.client.SocketServerListener;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Handles client-side network operations using TCP Sockets.
 * It extends {@link NetworkManager} to translate high-level user interface actions
 * into formatted text commands sent over the socket connection to the server.
 */
public class SocketNetworkManager extends NetworkManager {
    /**
     * The print writer utilized to send raw text commands over the socket.
     */
    private PrintWriter out;

    /**
     * The TCP socket representing the connection with the server.
     */
    private Socket socket;

    /**
     * Establishes a TCP connection to the server using the provided IP address and port.
     * It initializes the output stream and spawns a dedicated background thread running
     * a {@link SocketServerListener} to asynchronously process incoming server messages.
     *
     * @param ip   the server IP address to connect to
     * @param port the server port number to connect to
     * @throws Exception if an I/O error occurs when creating the socket or the streams
     */
    @Override
    public void connect(String ip, int port) throws Exception {
        this.socket = new Socket(ip, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);

        Thread listenerThread = new Thread(new SocketServerListener(socket.getInputStream(), view));
        listenerThread.start();
    }

    /**
     * Sends a login request command to the server with the player's chosen nickname and color.
     * Spaces inside the nickname are automatically replaced with underscores to maintain
     * command protocol formatting compliance.
     *
     * @param color the chosen player color
     * @param name  the requested player nickname
     */
    @Override
    public void login(Color color, String name) {
        out.println("LOGIN " + name.replace(" ", "_") + " " + color);
        out.flush();
    }

    /**
     * Sends a command to set the target number of players required for the match setup.
     *
     * @param n the target number of players
     */
    @Override
    public void setTotalPlayers(int n) {
        out.println("SET_PLAYERS " + n);
    }

    /**
     * Sends a command notifying the selection of a specific tile on the offer track.
     *
     * @param tileIndex the zero-based index of the chosen tile on the track
     */
    @Override
    public void tileSelection(int tileIndex) {
        out.println("TILE_SELECT " + tileIndex);
    }

    /**
     * Handles card-related structural action interactions by formatting a standard
     * selection message payload over the stream.
     *
     * @param prefix the command contextual descriptor code prefix
     * @param n      the target card reference index or identifier value
     */
    @Override
    protected void handleCardAction(String prefix, int n) {
        out.println("CARD_SELECT " + prefix + " " + n);
    }

    /**
     * Sends a request to retrieve historical global statistics entries
     * filtered by specific match group setups.
     *
     * @param targetPlayers the match capacity configuration filter parameter
     */
    @Override
    public void seeGlobalLeaderboard(int targetPlayers) {
        out.println("GLOBAL_LEADERBOARD " + targetPlayers);
    }

    /**
     * Sends an explicitly formatted network signal requesting the server to commit
     * final active status closure routines for the current player's turn cycle.
     */
    @Override
    public void endTurnRequest() {
        out.println("END_TURN");
    }
}