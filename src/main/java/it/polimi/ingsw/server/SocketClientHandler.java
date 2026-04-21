package it.polimi.ingsw.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.OfferTileDTO;
import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.RemoteController;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.*;
import java.util.function.Consumer;

// TODO
public class SocketClientHandler extends Thread {
    private Socket socket;
    private Lobby lobby;
    private final Map<String, Consumer<String[]>> actionCommands = new HashMap<>();
    private RemoteController controller;

    public SocketClientHandler(Socket s, Lobby l) {
        this.socket = s;
        this.lobby = l;
        initializeCommands();
    }

    private void initializeCommands() {
        actionCommands.put("SET_PLAYERS", args -> {
            int n = Integer.parseInt(args[0]);
            try {
                lobby.setTargetPlayers(n);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        actionCommands.put("TILE_SELECT", args -> {
            try {
                int index = Integer.parseInt(args[0]);
                controller.handleTileSelection(index);
            } catch (RemoteException e) {
                System.out.println("Internal controller error");
            } catch (RuntimeException e) {
                System.out.println("ERROR " + e.getCause().getMessage());
            }
        });
    }

    public void setController(RemoteController controller) {
        this.controller = controller;
    }

    public void run() {
        try (Scanner in = new Scanner(socket.getInputStream());
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            boolean loggedIn = false;

            while (!loggedIn && in.hasNextLine()) {
                String line = in.nextLine();
                String[] parts = line.split(" ");

                if (parts[0].equals("LOGIN") && parts.length == 3) {
                    try {
                        String nick = parts[1];
                        Color chosenColor = Color.valueOf(parts[2].toUpperCase());

                        SocketVirtualView vView = new SocketVirtualView(out, this);
                        lobby.addPlayer(nick, chosenColor, vView);

                        loggedIn = true;
                    } catch (IllegalArgumentException e) {
                        out.println("ERROR invalid color");
                    } catch (Exception e) {
                        out.println("ERROR " + e.getMessage());
                    }
                } else {
                    out.println("ERROR login first: LOGIN <nick> <color>");
                }
            }

            while (loggedIn && in.hasNextLine()) {
                try {
                    handleClientAction(in.nextLine());
                } catch (Exception e) {
                    out.println("ERROR invalid action " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Connection lost.");
        } finally {
            // lobby.removePlayer(this.nickname);
        }
    }

    private void handleClientAction(String actionLine) {
        String[] parts = actionLine.split(" ");
        if (parts.length == 0) return;

        String header = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        Consumer<String[]> action = actionCommands.get(header);
        if (action != null) {
            try {
                action.accept(args);
            } catch (Exception e) {
                System.err.println("Executing command error " + header + ": " + e.getMessage());
            }
        } else {
            System.out.println("Unknown socket command " + header);
        }
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

    }

    @Override
    public void askCardChoose() throws RemoteException {

    }
}