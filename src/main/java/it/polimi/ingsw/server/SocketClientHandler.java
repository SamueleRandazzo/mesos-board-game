package it.polimi.ingsw.server;

import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.network.commands.ClientCommandFactory;
import it.polimi.ingsw.network.commands.ClientCommandHandler;
import it.polimi.ingsw.model.Enum.Color;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class SocketClientHandler extends Thread {
    private final Socket socket;
    private final Lobby lobby;
    private RemoteController controller;
    private String nickname;
    private SocketVirtualView vView;

    public SocketClientHandler(Socket s, Lobby l) {
        this.socket = s;
        this.lobby = l;
    }

    public void setController(RemoteController controller) {
        this.controller = controller;
    }

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
                    handler.handle(args, lobby, controller, out);
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

