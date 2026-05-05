package it.polimi.ingsw.server;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.commands.ServerCommandFactory;
import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;
import java.io.InputStream;
import java.util.*;

public class SocketServerListener implements Runnable {
    private final Scanner in;
    private final View view;
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public SocketServerListener(InputStream is, View view) {
        this.in = new Scanner(is);
        this.view = view;
    }

    @Override
    public void run() {
        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] parts = line.split(" ", 2);
            String header = parts[0];
            String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

            ServerCommandHandler handler = ServerCommandFactory.getHandler(header);
            if (handler != null) {
                handler.handle(args, view, mapper);
            } else {
                System.err.println("Unknown command: " + header);
            }
        }
    }
}