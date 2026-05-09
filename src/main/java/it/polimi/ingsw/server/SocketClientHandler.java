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
                    out.println("ERROR unknown command " + header);
                }
            }

        } catch (IOException e) {
            System.err.println("Connection lost.");
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
                    SocketVirtualView vView = new SocketVirtualView(out, this);
                    lobby.addPlayer(nick, chosenColor, vView);
                    return true;
                } catch (Exception e) {
                    out.println("ERROR " + e.getMessage());
                    out.println("LOGIN");
                }
            } else {
                out.println("ERROR login first");
            }
        }
        return false;
    }
}

