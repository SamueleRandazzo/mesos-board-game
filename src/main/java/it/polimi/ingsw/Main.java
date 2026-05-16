package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientMain;
import it.polimi.ingsw.server.ServerMain;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Main launcher that routes execution to either the Server or the Client
 * based on the command-line arguments provided.
 */
public class Main {
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
