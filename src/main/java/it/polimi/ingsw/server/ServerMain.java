package it.polimi.ingsw.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class ServerMain {
    private static final int RMI_PORT = 1234;
    private static final int TCP_PORT = 1235;

    public static void main(String[] args) {
        Lobby lobby = new Lobby();

        // RMI
        try {
            RMIServer rmiAdapter = new RMIServer(lobby);
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            registry.rebind("Loggable", rmiAdapter);
            System.out.println("[RMI] Server ready on port " + RMI_PORT);
        } catch (Exception e) {
            System.err.println("[RMI] Error: " + e.getMessage());
        }

        // TCP
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(TCP_PORT)) {
                System.out.println("[SOCKET] Server ready on port " + TCP_PORT);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    new SocketClientHandler(clientSocket, lobby).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}