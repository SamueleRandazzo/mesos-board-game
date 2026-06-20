package it.polimi.ingsw.client;

import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.network.RMINetworkManager;
import it.polimi.ingsw.network.SocketNetworkManager;
import it.polimi.ingsw.view.*;
import java.util.Arrays;
import java.util.List;
import it.polimi.ingsw.view.GUI.*;

/**
 * The main entry point for the client application.
 * This class parses command-line arguments to initialize the network connection
 * (either via RMI or Socket) and launches the appropriate user interface (CLI or GUI).
 */
public class ClientMain {

    /**
     * The default port number used for RMI connections.
     */
    static final int RMI_PORT = 1234;

    /**
     * The default port number used for Socket connections.
     */
    static final int SOCKET_PORT = 1235;

    /**
     * The IP address of the server to connect to.
     */
    static String SERVER_IP;

    /**
     * The main method that starts the client application.
     * It handles argument parsing to configure the network protocol and the view type.
     * * <p>Expected arguments:</p>
     * <ul>
     * <li>The first argument must always be the server's <b>IP address</b>.</li>
     * <li><code>--socket</code>: Optional flag to use Socket connection (defaults to RMI if omitted).</li>
     * <li><code>--gui</code>: Optional flag to launch the Graphical User Interface (defaults to CLI if omitted).</li>
     * </ul>
     *
     * @param args command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        List<String> argList = Arrays.asList(args);
        NetworkManager network;

        String inputIP = !argList.isEmpty() ? argList.getLast() : "";
        String ipv4Regex = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

        if (inputIP.isEmpty() || !inputIP.matches(ipv4Regex)) {
            SERVER_IP = "127.0.0.1";
            System.out.println("[WARNING] IP '" + inputIP + "' is empty or invalid. Set default to: " + SERVER_IP);
        } else {
            SERVER_IP = inputIP;
        }

        int currentPort;

        if (argList.contains("--socket")) {
            network = new SocketNetworkManager();
            currentPort = SOCKET_PORT;
            System.out.println("[SOCKET] Client started.");
        } else {
            network = new RMINetworkManager();
            currentPort = RMI_PORT;
            System.out.println("[RMI] Client started.");
        }

        if (argList.contains("--cli")) {
            View view = new CLIView(network);
            network.setView(view);

            try {
                network.connect(SERVER_IP, currentPort);
            } catch (Exception e) {
                System.err.println("Server error: " + e.getMessage());
                view.showError("Impossible connect to server (" + SERVER_IP + ":" + currentPort + ")");
            }

            view.showLogin();
        } else {
            JavaFXMain.startGui(network, SERVER_IP, currentPort);
        }
    }
}