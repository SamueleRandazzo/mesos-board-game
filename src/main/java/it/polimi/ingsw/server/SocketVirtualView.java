package it.polimi.ingsw.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.Enum.Color;
import it.polimi.ingsw.network.DTO.*;
import it.polimi.ingsw.network.GameObserver;
import it.polimi.ingsw.network.RemoteController;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SocketVirtualView implements GameObserver {
    private PrintWriter out;
    private SocketClientHandler handler;

    public SocketVirtualView(PrintWriter out, SocketClientHandler handler) {
        this.out = out;
        this.handler = handler;
    }

    @Override
    public void onPlayerJoined(int current, int target) {
        out.println("PLAYER_JOINED " + current + "/" + target);
    }

    @Override
    public void onGameStarted(RemoteController controller, int totalPlayers) {
        this.handler.setController(controller);
        out.println("GAME_STARTED " + totalPlayers);
    }

    @Override
    public void askMaxPlayers() {
        out.println("ASK_MAX_PLAYERS");
    }

    @Override
    public void askTotemPlacement() {
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
    public void askCardChoose() {
        try {
            out.println("ASK_CARD_CHOOSE");
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    @Override
    public void onShowMessage(String message) {
        try {
            out.println("MESSAGE " + message);
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    @Override
    public void onShowPlayersOrder(List<String> playersOrder) {
        try {
            String joinedNames = String.join(",", playersOrder);
            out.println("PLAYERS_ORDER " + joinedNames);
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    @Override
    public void onShowPlayersInfo(Map<String, Color> playersInfo) {
        String payload = playersInfo.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue().name())
                .collect(Collectors.joining(","));

        out.println("PLAYERS_INFO " + payload);
    }

    /**
     * Sends the updated tribe status of a specific player over the socket connection.
     * The message format follows the protocol: SHOW_TRIBE [nickname] [jsonPayload].
     *
     * @param nickname the nickname of the player who owns the tribe
     * @param tribe    the updated TribeStatusDTO object
     */
    @Override
    public void onShowTribe(String nickname, TribeStatusDTO tribe) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(tribe);
            String cleanedJson = json.replace("\n", "").replace("\r", "");

            out.println("SHOW_TRIBE " + nickname + " " + cleanedJson);
            out.flush();
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
    public void onDisplayLeaderboard(LeaderboardDTO leaderboard, String globalRank) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            String jsonLeaderboard = mapper.writeValueAsString(leaderboard);
            String cleanedJson = jsonLeaderboard.replace("\n", "").replace("\r", "");

            out.println("DISPLAY_LEADERBOARD " + cleanedJson + " " + globalRank.replace(" ", "_"));
            out.flush();
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing BoardDTO: " + e.getMessage());
        }
    }

    @Override
    public void ping() throws RemoteException{
        out.println("PING");
        if (out.checkError()) {
            throw new RemoteException("Socket connection lost during ping");
        }
    }

    @Override
    public void onShowFatalError(String error) {
        try {
            out.println("SHOW_FATAL_ERROR " + error);
            out.flush();
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    @Override
    public void onDisplayGlobalLeaderboard(GlobalLeaderboardDTO leaderboard) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonList = mapper.writeValueAsString(leaderboard);
            String cleanedJson = jsonList.replace("\n", "").replace("\r", "");

            out.println("DISPLAY_GLOBAL_LEADERBOARD " + cleanedJson);
            out.flush();
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    public void onShowError(String error) {
        try {
            out.println("ERROR " + error);
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    @Override
    public void onDisplayTurnOrderTile(List<TurnOrderTileDTO> turnOrderTile) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(turnOrderTile);
            String cleanedJson = json.replace("\n", "").replace("\r", "");
            out.println("DISPLAY_TURN_ORDER_TILE " + cleanedJson);
            out.flush();
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    @Override
    public void onShowEventMessage(String message) throws RemoteException {
        try {
            out.println("EVENT_MESSAGE " + message);
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    @Override
    public void askEndTurnOrBuyBuilding() throws RemoteException {
        try {
            out.println("END_TURN_OR_BUY_BUILDING");
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }
}