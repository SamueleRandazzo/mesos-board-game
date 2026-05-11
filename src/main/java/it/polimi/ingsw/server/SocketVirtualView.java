package it.polimi.ingsw.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.BoardDTO;
import it.polimi.ingsw.network.DTO.LeaderboardDTO;
import it.polimi.ingsw.network.DTO.OfferTileDTO;
import it.polimi.ingsw.network.DTO.TribeStatusDTO;
import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.RemoteController;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public void onGameStarted(RemoteController controller, int totalPlayers) throws RemoteException {
        this.handler.setController(controller);
        out.println("GAME_STARTED " + totalPlayers);
    }

    @Override
    public void askMaxPlayers() throws RemoteException {
        out.println("ASK_MAX_PLAYERS");
    }

    @Override
    public void askTotemPlacement() throws RemoteException {
        try {
            out.println("ASK_TOTEM_PLACEMENT");
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    @Override
    public void onDisplayOfferTrack(List<OfferTileDTO> tiles) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(tiles);

            // Clean the JSON string to avoid breaking the socket's readLine()
            String cleanedJson = json.replace("\n", "").replace("\r", "");

            out.println("DISPLAY_OFFER_TRACK " + cleanedJson);
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

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

    @Override
    public void onShowPlayersInfo(Map<String, Color> playersInfo) throws RemoteException {
        String payload = playersInfo.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue().name())
                .collect(Collectors.joining(","));

        out.println("PLAYERS_INFO " + payload);
    }

    @Override
    public void onShowTribe(TribeStatusDTO tribe) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(tribe);

            String cleanedJson = json.replace(" ", "").replace("\n", "").replace("\r", "");

            out.println("SHOW_TRIBE " + cleanedJson);
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    @Override
    public void onDisplayBoard(BoardDTO board) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // 1. Transform the BoardDTO object into a JSON string
            String jsonBoard = mapper.writeValueAsString(board);

            // Clean the JSON string to ensure it is sent as a single line over the socket
            String cleanedJson = jsonBoard.replace("\n", "").replace("\r", "");

            // 2. Send the command over the socket following the protocol: HEADER + SPACE + JSON
            out.println("DISPLAY_BOARD " + cleanedJson);
            out.flush();

        } catch (JsonProcessingException e) {
            System.err.println("Error serializing BoardDTO: " + e.getMessage());
        }
    }

    @Override
    public void onDisplayLeaderboard(LeaderboardDTO leaderboard) throws RemoteException {
        try {
            ObjectMapper mapper = new ObjectMapper();

            String jsonLeaderboard = mapper.writeValueAsString(leaderboard);
            String cleanedJson = jsonLeaderboard.replace("\n", "").replace("\r", "");

            out.println("DISPLAY_LEADERBOARD " + cleanedJson);
            out.flush();
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing BoardDTO: " + e.getMessage());
        }
    }
}