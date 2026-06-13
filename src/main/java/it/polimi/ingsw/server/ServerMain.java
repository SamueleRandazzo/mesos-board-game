package it.polimi.ingsw.server;

import it.polimi.ingsw.database.DatabaseManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    private static final int RMI_PORT = 1234;
    private static final int TCP_PORT = 1235;

    public static void main(String[] args) {
        String serverIp;
        try {
            serverIp = java.net.InetAddress.getLocalHost().getHostAddress();
            System.out.println("[SERVER] Detected local IP: " + serverIp);
            System.setProperty("java.rmi.server.hostname", serverIp);
        } catch (Exception e) {
            System.err.println("Unable to detect local IP automatically, using localhost");
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
        }

        if (args.length < 3) {
            System.err.println("WARNING: DB credential not provided");
            System.err.println("Ranking functionality disabled.");
        } else {
            String dbUser = args[1];
            String dbPass = args[2];

            DatabaseManager.init(dbUser, dbPass);
        }

        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "5000");
        System.setProperty("sun.rmi.transport.connectionTimeout", "5000");

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
                System.err.println(e);
            }
        }).start();
    }
}