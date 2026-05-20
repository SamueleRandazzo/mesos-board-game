package it.polimi.ingsw.server;

import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.network.commands.ClientCommandFactory;
import it.polimi.ingsw.network.commands.ClientCommandHandler;
import it.polimi.ingsw.model.Enum.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

/**
 * Handles network communication with a single client connected via TCP Socket.
 * This class runs on a dedicated thread, managing the initial login phase and
 * continuously parsing incoming network commands from the client.
 */
public class SocketClientHandler extends Thread {
    /**
     * The TCP socket connection to the client.
     */
    private final Socket socket;

    /**
     * The main game lobby instance where players are registered.
     */
    private final Lobby lobby;

    /**
     * The remote game controller used to forward valid player actions.
     */
    private RemoteController controller;

    /**
     * The unique nickname of the player associated with this handler.
     */
    private String nickname;

    /**
     * The virtual view instance acting as the network proxy for client callbacks.
     */
    private SocketVirtualView vView;

    /**
     * Constructs a new SocketClientHandler for the specified socket connection.
     *
     * @param s the connected socket from the client
     * @param l the server lobby instance
     */
    public SocketClientHandler(Socket s, Lobby l) {
        this.socket = s;
        this.lobby = l;
    }

    /**
     * Sets the game controller instance for this connection, enabling
     * command execution on the game state.
     *
     * @param controller the remote game controller to be linked
     */
    public void setController(RemoteController controller) {
        this.controller = controller;
    }

    /**
     * The main execution loop of the thread. It opens input and output streams,
     * triggers the login phase, and then loops indefinitely to read and dispatch
     * incoming player commands until the client disconnects.
     */
    @Override
    public void run() {
        try (Scanner in = new Scanner(socket.getInputStream());
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            boolean loggedIn = handleLogin(in, out);

            while (loggedIn && in.hasNextLine()) {
                String line = in.nextLine();
                String[] parts = line.split(" ", 2);
                String header = parts[0];
                String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

                ClientCommandHandler handler = ClientCommandFactory.getHandler(header);
                if (handler != null) {
                    handler.handle(args, lobby, controller, vView);
                } else {
                    out.println("ERROR unknown_command " + header);
                }
            }

        } catch (IOException | NoSuchElementException e) {
            if (this.nickname != null) {
                System.err.println("Connection lost for player: " + this.nickname);
                lobby.handleDisconnection(this.nickname);
            }
        }
    }

    /**
     * Manages the initial negotiation phase until the player successfully logs in.
     * It expects a specific format: "LOGIN nickname color".
     *
     * @param in  the scanner hooked to the socket input stream
     * @param out the print writer hooked to the socket output stream
     * @return true if the login process completed successfully, false otherwise
     */
    private boolean handleLogin(Scanner in, PrintWriter out) {
        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] parts = line.split(" ");
            if (parts[0].equals("LOGIN") && parts.length == 3) {
                try {
                    String nick = parts[1].replace("_", " ");
                    Color chosenColor = Color.valueOf(parts[2].toUpperCase());
                    this.vView = new SocketVirtualView(out, this);
                    lobby.addPlayer(nick, chosenColor, vView);
                    this.nickname = nick;
                    out.println("PING"); // Notify that the player logged
                    return true;
                } catch (Exception e) {
                    out.println("LOGIN_ERROR " + e.getMessage());
                }
            } else {
                out.println("LOGIN_ERROR login first");
            }
        }
        return false;
    }
}