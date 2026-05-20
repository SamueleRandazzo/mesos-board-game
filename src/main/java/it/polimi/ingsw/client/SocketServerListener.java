package it.polimi.ingsw.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.commands.ServerCommandFactory;
import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Client-side network listener that handles asynchronous incoming text protocol streams
 * from the server via TCP Sockets. It continuously parses received messages, manages an
 * inactivity/timeout checker based on server heartbeats, and dispatches command packets
 * to a dedicated execution queue to prevent blocking the read channel.
 */
public class SocketServerListener implements Runnable {
    /**
     * Scanner bound to the socket input stream to read incoming text rows.
     */
    private final Scanner in;

    /**
     * The active user interface view interface layer instance.
     */
    private final View view;

    /**
     * Jackson Object Mapper used for deserializing text-json args.
     * Configured to safely ignore unknown properties during mapping.
     */
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * Timestamp tracking the exact local system epoch time when the last packet was received.
     */
    private long lastContact = System.currentTimeMillis();

    /**
     * Scheduled thread pool worker in charge of cyclic timeout checks.
     */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Timeout boundary threshold limit defined in seconds before declaring link loss.
     */
    private static final int TIMEOUT_SECONDS = 10;

    /**
     * Flag indicating whether the cyclic monitoring task routine has been officially kicked off.
     */
    private boolean timerStarted = false;

    /**
     * Thread executor pool responsible for running specific server network commands sequentially
     * without blocking the network reader stream loop.
     */
    private final ExecutorService commandExecutor = Executors.newSingleThreadExecutor();

    /**
     * Constructs a SocketServerListener attached to a target input stream connection.
     *
     * @param is   the {@link InputStream} tied to the network connection socket link
     * @param view the user interface {@link View} reference mapping user screens
     */
    public SocketServerListener(InputStream is, View view) {
        this.in = new Scanner(is);
        this.view = view;
    }

    /**
     * Main execution lifecycle routine. It processes incoming network lines sequentially,
     * filters out or intercepts heartbeat PING markers to synchronize link health metrics,
     * and delegates operational commands task actions onto the handler executor queue pool.
     */
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

                if (header.equals("PING")) {
                    continue;
                }

                commandExecutor.submit(() -> {
                    ServerCommandHandler handler = ServerCommandFactory.getHandler(header);
                    if (handler != null) {
                        handler.handle(args, view, mapper);
                    }
                });
            }
        } catch (NoSuchElementException e) {
            // It happens if the connection suddenly drops while the scanner is waiting
        } finally {
            commandExecutor.shutdown();
            handleServerDisconnection();
        }
    }

    /**
     * Spawns a cyclic worker thread running at fixed intervals to measure timespans
     * passed since the last confirmed transaction took place, evaluating connection health.
     */
    private void startInactivityTimer() {
        scheduler.scheduleAtFixedRate(() -> {
            long timeSinceLastContact = System.currentTimeMillis() - lastContact;
            if (timeSinceLastContact > TIMEOUT_SECONDS * 1000) {
                handleServerDisconnection();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    /**
     * Safely breaks down background task managers when connection losses occur,
     * and forwards a terminal notification trigger to the active UI layout view wrapper.
     */
    private synchronized void handleServerDisconnection() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdownNow();
            view.showFatalError("Connection to the server lost. The game is over.");
        }
    }
}