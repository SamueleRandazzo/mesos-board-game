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

/**
 * Acts as a virtual view on the server side for a client connected via TCP Socket.
 * It implements the {@link GameObserver} interface, translating model events and
 * controller requests into plain-text protocol strings or JSON payloads sent over the socket.
 */
public class SocketVirtualView implements GameObserver {
    /**
     * The print writer linked to the client's socket output stream.
     */
    private PrintWriter out;

    /**
     * The network handler associated with this client connection.
     */
    private SocketClientHandler handler;

    /**
     * Constructs a new SocketVirtualView with the specified output stream and network handler.
     *
     * @param out     the network print writer used to send messages to the client
     * @param handler the client handler associated with this connection
     */
    public SocketVirtualView(PrintWriter out, SocketClientHandler handler) {
        this.out = out;
        this.handler = handler;
    }

    /**
     * Notifies the client that a player has joined the lobby, providing the current count.
     *
     * @param current the current number of players in the lobby
     * @param target  the required number of players to start the match
     */
    @Override
    public void onPlayerJoined(int current, int target) {
        out.println("PLAYER_JOINED " + current + "/" + target);
    }

    /**
     * Notifies the client that the game has started, attaches the active controller
     * to the network handler, and sends the total player count.
     *
     * @param controller   the remote controller managing the match state
     * @param totalPlayers the total number of players participating
     */
    @Override
    public void onGameStarted(RemoteController controller, int totalPlayers) {
        this.handler.setController(controller);
        out.println("GAME_STARTED " + totalPlayers);
    }

    /**
     * Requests the first player to define the maximum number of players for the match.
     */
    @Override
    public void askMaxPlayers() {
        out.println("ASK_MAX_PLAYERS");
    }

    /**
     * Requests the client to choose where to place their initial totem on the board.
     */
    @Override
    public void askTotemPlacement() {
        try {
            out.println("ASK_TOTEM_PLACEMENT");
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    /**
     * Sends the current list of available offer tiles on the track serialized as a single-line JSON string.
     *
     * @param tiles the list of OfferTileDTO objects representing the track state
     */
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

    /**
     * Requests the client to pick a card during their choice phase.
     */
    @Override
    public void askCardChoose() {
        try {
            out.println("ASK_CARD_CHOOSE");
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    /**
     * Forwards a generic text message or notification to the client.
     *
     * @param message the string content to display
     */
    @Override
    public void onShowMessage(String message) {
        try {
            out.println("MESSAGE " + message);
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    /**
     * Sends the turn sequence of players for the current round as a comma-separated string.
     *
     * @param playersOrder the ordered list of player nicknames
     */
    @Override
    public void onShowPlayersOrder(List<String> playersOrder) {
        try {
            String joinedNames = String.join(",", playersOrder);
            out.println("PLAYERS_ORDER " + joinedNames);
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    /**
     * Sends general registration info linking each active player to their chosen color.
     * Format: nickname1:COLOR1,nickname2:COLOR2...
     *
     * @param playersInfo a map pairing player nicknames with their assigned colors
     */
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

    /**
     * Sends the updated game board state to the client as a single-line JSON payload.
     *
     * @param board the complete BoardDTO object representing the grid/map status
     */
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

    /**
     * Sends the final match leaderboard alongside the player's updated historical global ranking.
     *
     * @param leaderboard the match results summary data transfer object
     * @param globalRank  the updated global ranking position text for the client
     */
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

    /**
     * Sends a connection heartbeat check to the client. Throws a RemoteException
     * if the printer stream flags an internal socket error.
     *
     * @throws RemoteException if the TCP link is closed or broken during transmission
     */
    @Override
    public void ping() throws RemoteException{
        out.println("PING");
        if (out.checkError()) {
            throw new RemoteException("Socket connection lost during ping");
        }
    }

    /**
     * Transmits a severe or unrecoverable error message that forces a client-side shutdown or disconnect.
     *
     * @param error the fatal error context string
     */
    @Override
    public void onShowFatalError(String error) {
        try {
            out.println("SHOW_FATAL_ERROR " + error);
            out.flush();
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    /**
     * Sends the entire global database records leaderboard to the client as a single-line JSON payload.
     *
     * @param leaderboard the top rankings data collected from the database tier
     */
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

    /**
     * Sends a non-fatal validation error notification (e.g., an illegal game move) back to the client.
     *
     * @param error the description message detailing what went wrong
     */
    public void onShowError(String error) {
        try {
            out.println("ERROR " + error);
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    /**
     * Dispatches the updated layout of active turn order tiles to the client.
     *
     * @param turnOrderTile the list containing the current order sequence metadata
     */
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

    /**
     * Sends an atmospheric or systemic in-game log event narrative statement over the stream.
     *
     * @param message the event log narrative text
     * @throws RemoteException if a network socket error occurs during writing
     */
    @Override
    public void onShowEventMessage(String message) throws RemoteException {
        try {
            out.println("EVENT_MESSAGE " + message);
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    /**
     * Directs the client to make a choice during their resolution phase between passing
     * (ending their turn) or completing a structural asset building purchase.
     *
     * @throws RemoteException if a network socket error occurs during writing
     */
    @Override
    public void askEndTurnOrBuyBuilding() throws RemoteException {
        try {
            out.println("END_TURN_OR_BUY_BUILDING");
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }
}