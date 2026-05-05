package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.server.SocketServerListener;

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
        out.println("LOGIN " + name + " " + color);
        out.flush();
    }

    @Override
    public void setTotalPlayers(int n) {
        out.println("SET_PLAYERS " + n);
    }

    @Override
    public void tileSelection(int tileIndex) {
        out.println("TILE_SELECT " + tileIndex);
    }

    @Override
    protected void handleCardAction(String prefix, int n) {
        out.println("CARD_SELECT " + prefix + " " + n);
    }
}