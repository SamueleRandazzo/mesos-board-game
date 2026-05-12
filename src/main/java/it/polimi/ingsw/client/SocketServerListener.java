package it.polimi.ingsw.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.commands.ServerCommandFactory;
import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SocketServerListener implements Runnable {
    private final Scanner in;
    private final View view;
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // Health check
    private long lastContact = System.currentTimeMillis();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final int TIMEOUT_SECONDS = 10;
    private boolean timerStarted = false;

    public SocketServerListener(InputStream is, View view) {
        this.in = new Scanner(is);
        this.view = view;
    }

    @Override
    public void run() {
        try {
            while (in.hasNextLine()) {
                String line = in.nextLine();
                this.lastContact = System.currentTimeMillis();

                String[] parts = line.split(" ", 2);
                String header = parts[0];
                String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

                if (!timerStarted && header.equals("PING")) {
                    startInactivityTimer();
                    timerStarted = true;
                }

                ServerCommandHandler handler = ServerCommandFactory.getHandler(header);
                if (handler != null) {
                    handler.handle(args, view, mapper);
                } else {
                    System.err.println("Unknown command: " + header);
                }
            }
        } catch (NoSuchElementException e) {
            // It happens if the connection suddenly drops while the scanner is waiting
        } finally {
            handleServerDisconnection();
        }
    }

    private void startInactivityTimer() {
        scheduler.scheduleAtFixedRate(() -> {
            long timeSinceLastContact = System.currentTimeMillis() - lastContact;
            if (timeSinceLastContact > TIMEOUT_SECONDS * 1000) {
                handleServerDisconnection();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    private synchronized void handleServerDisconnection() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdownNow();
            view.showFatalError("Connection to the server lost. The game is over.");
        }
    }
}