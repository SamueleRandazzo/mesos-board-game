package it.polimi.ingsw.server;

import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.RemoteController;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Scanner;

// TODO
public class SocketClientHandler extends Thread {
    private Socket socket;
    private Lobby lobby;

    public SocketClientHandler(Socket s, Lobby l) { this.socket = s; this.lobby = l; }

    public void run() {
        try (Scanner in = new Scanner(socket.getInputStream());
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String line = in.nextLine();
            if (line.startsWith("LOGIN")) {
                String nick = line.split(" ")[1];

                // SocketVirtualView vView = new SocketVirtualView(out);

                // lobby.addPlayer(nick, vView);
            }
        } catch (Exception e) { /* ... */ }
    }
}

/*
class SocketVirtualView implements GameObserver {
    private PrintWriter out;
    public SocketVirtualView(PrintWriter out) { this.out = out; }

    @Override
    public void onGameStarted(RemoteController controller) throws RemoteException {
        out.println("GAME_STARTED");
    }

    @Override
    public void onPlayerJoined(int current, int target) throws RemoteException {
        out.println("PLAYER_JOINED " + current + "/" + target);
    }
}*/