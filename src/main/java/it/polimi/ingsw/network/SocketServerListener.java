package it.polimi.ingsw.network;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.DTO.OfferTileDTO;
import it.polimi.ingsw.view.View;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

public class SocketServerListener implements Runnable {
    private final Scanner in;
    private final Map<String, Consumer<String[]>> commands = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public SocketServerListener(InputStream is, View view) {
        this.in = new Scanner(is);
        commandsInit(view);
    }

    @Override
    public void run() {
        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] parts = line.split(" ");
            String header = parts[0];

            String[] args = Arrays.copyOfRange(parts, 1, parts.length);

            Consumer<String[]> action = commands.get(header);
            if (action != null) {
                action.accept(args);
            } else {
                System.err.println("Unknown command: " + header);
            }
        }
    }

    private void commandsInit(View view) {
        commands.put("PLAYER_JOINED", args -> {
            String[] counts = args[0].split("/");
            view.showLobby(Integer.parseInt(counts[0]), Integer.parseInt(counts[1]));
        });

        commands.put("GAME_STARTED", args -> view.startGame(null));

        commands.put("ERROR", args ->  {
            view.showError(String.join(" ", args));
        });

        commands.put("LOGIN_ERROR", args ->  {
            view.showError(String.join(" ", args));
            view.showLogin();
        });

        commands.put("ASK_MAX_PLAYERS", args -> view.askMaxPlayers());

        commands.put("ASK_TOTEM_PLACEMENT", args -> {
            try {
                String json = args[0];

                List<OfferTileDTO> tiles = this.mapper.readValue(json, new TypeReference<List<OfferTileDTO>>(){});

                view.askTotemPlacement(tiles);
            } catch (Exception e) {
                view.showError("Server data error");
                e.printStackTrace();
            }
        });
    }
}