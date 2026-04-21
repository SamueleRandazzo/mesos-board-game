package it.polimi.ingsw.client;

import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.network.RMINetworkManager;
import it.polimi.ingsw.network.SocketNetworkManager;
import it.polimi.ingsw.view.*;
import java.util.Arrays;
import java.util.List;

public class ClientMain {
    static final int RMI_PORT = 1234;
    static final int SOCKET_PORT = 1235;
    static final String IP = "127.0.0.1";

    public static void main(String[] args) {
        List<String> argList = Arrays.asList(args);

        NetworkManager network;
        int currentPort;

        if (argList.contains("--socket")) {
            network = new SocketNetworkManager();
            currentPort = SOCKET_PORT;
            System.out.println("SOCKET start...");
        } else {
            network = new RMINetworkManager();
            currentPort = RMI_PORT;
            System.out.println("RMI start");
        }

        View view;
        if (argList.contains("--gui")) {
            // view = new GUIView(network);
            view = new CLIView(network);
        } else {
            view = new CLIView(network);
        }

        try {
            network.setView(view);
            network.connect(IP, currentPort);
            System.out.println("Connected to the server on the port " + currentPort);

            view.showLogin();
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}