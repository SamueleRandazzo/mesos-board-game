package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientMain;
import it.polimi.ingsw.server.ServerMain;

/**
 * Main launcher that routes execution to either the Server or the Client
 * based on the command-line arguments provided.
 */
public class Main {
    /**
     * Starts the server when {@code --server} is provided, otherwise starts the client.
     *
     * @param args command-line arguments used to choose the application mode
     */
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--server")) {
            System.out.println("Starting Mesos Server Infrastructure...");
            ServerMain.main(args);
        } else {
            System.out.println("Starting Mesos Client Dashboard...");
            ClientMain.main(args);
        }
    }
}
