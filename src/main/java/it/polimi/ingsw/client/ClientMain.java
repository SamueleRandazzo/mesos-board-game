package it.polimi.ingsw.client;

import it.polimi.ingsw.Loggable;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain {
    static int PORT = 1234;
    static String ADDRESS = "";

    public static void main(String[] args) {
        try {
            System.out.println("Client Started!");
            Registry registry = LocateRegistry.getRegistry(ADDRESS, PORT);

            Loggable stub = (Loggable) registry.lookup("Loggable");
        } catch (Exception e) {
            System.err.println("Server error: " + e);
            e.printStackTrace();
        }
    }
}
