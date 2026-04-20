package it.polimi.ingsw.server;

import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.OfferTileDTO;
import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.RemoteController;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

// TODO
public class SocketClientHandler extends Thread {
    private Socket socket;
    private Lobby lobby;

    public SocketClientHandler(Socket s, Lobby l) { this.socket = s; this.lobby = l; }

    public void run() {
        try (Scanner in = new Scanner(socket.getInputStream());
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            if (in.hasNextLine()) {
                String line = in.nextLine();
                String[] parts = line.split(" ");

                if (parts.length == 3 && parts[0].equals("LOGIN")) {
                    String nick = parts[1];
                    String colorString = parts[2].toUpperCase();

                    try {
                        Color chosenColor = Color.valueOf(colorString);

                        SocketVirtualView vView = new SocketVirtualView(out);

                        lobby.addPlayer(nick, chosenColor, vView);

                    } catch (IllegalArgumentException e) {
                        out.println("ERROR Invalid color!");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Socket handler error: " + e.getMessage());
        }
    }
}

class SocketVirtualView implements GameObserver {
    private PrintWriter out;

    public SocketVirtualView(PrintWriter out) {
        this.out = out;
    }

    @Override
    public void onPlayerJoined(int current, int target) throws RemoteException {
        out.println("PLAYER_JOINED " + current + "/" + target);
    }


    @Override
    public void onGameStarted(RemoteController controller) throws RemoteException {
        out.println("GAME_STARTED");
    }

    @Override
    public void askMaxPlayers() throws RemoteException {

    }

    @Override
    public void askTotemPlacement(List<OfferTileDTO> tiles) throws RemoteException {

    }

    @Override
    public void onShowError(String error) throws RemoteException {

    }

    @Override
    public void askCardChoose() throws RemoteException {

    }
}