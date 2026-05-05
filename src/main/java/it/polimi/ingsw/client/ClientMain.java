package it.polimi.ingsw.client;

import it.polimi.ingsw.network.NetworkManager;
import it.polimi.ingsw.network.RMINetworkManager;
import it.polimi.ingsw.network.SocketNetworkManager;
import it.polimi.ingsw.view.*;
import java.util.Arrays;
import java.util.List;
import it.polimi.ingsw.view.GUI.*;

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
        } else {
            network = new RMINetworkManager();
            currentPort = RMI_PORT;
        }

        try {
            network.connect(IP, currentPort);
            System.out.println("Connected to the server on port " + currentPort);
        } catch (Exception e) {
            System.err.println("Fatal error during connection: " + e.getMessage());
            return;
        }

        if (argList.contains("--gui")) {
            JavaFXMain.startGui(network);
        } else {
            // Per la CLI
            View view = new CLIView(network);
            network.setView(view);
            view.showLogin();
        }
    }
}