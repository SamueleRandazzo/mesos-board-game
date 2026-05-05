package it.polimi.ingsw.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.OfferTileDTO;
import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.RemoteController;
import it.polimi.ingsw.network.commands.ClientCommandFactory;
import it.polimi.ingsw.network.commands.ClientCommandHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
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
                    String nick = parts[1];
                    Color chosenColor = Color.valueOf(parts[2].toUpperCase());
                    SocketVirtualView vView = new SocketVirtualView(out, this);
                    lobby.addPlayer(nick, chosenColor, vView);
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

class SocketVirtualView implements GameObserver {
    private PrintWriter out;
    private SocketClientHandler handler;

    public SocketVirtualView(PrintWriter out, SocketClientHandler handler) {
        this.out = out;
        this.handler = handler;
    }

    @Override
    public void onPlayerJoined(int current, int target) throws RemoteException {
        out.println("PLAYER_JOINED " + current + "/" + target);
    }

    @Override
    public void onGameStarted(RemoteController controller) throws RemoteException {
        this.handler.setController(controller);
        out.println("GAME_STARTED");
    }

    @Override
    public void askMaxPlayers() throws RemoteException {
        out.println("ASK_MAX_PLAYERS");
    }

    @Override
    public void askTotemPlacement(List<OfferTileDTO> tiles) throws RemoteException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(tiles);

            String cleanedJson = json.replace(" ", "").replace("\n", "").replace("\r", "");

            out.println("ASK_TOTEM_PLACEMENT " + cleanedJson);
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    @Override
    public void onShowError(String error) throws RemoteException {
        try {
            out.println("ERROR " + error);
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    //TODO
    @Override
    public void askCardChoose() throws RemoteException {
        try {
            out.println("ASK_CARD_CHOOSE");
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    @Override
    public void onShowMessage(String message) throws RemoteException {
        try {
            out.println("MESSAGE " + message);
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    @Override
    public void onShowPlayersOrder(List<String> playersOrder) throws RemoteException {
        try {
            String joinedNames = String.join(",", playersOrder);
            out.println("PLAYERS_ORDER " + joinedNames);
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }
}