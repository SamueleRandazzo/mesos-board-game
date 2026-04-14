package it.polimi.ingsw.client;

import it.polimi.ingsw.network.Loggable;
import it.polimi.ingsw.view.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class ClientMain {
    static int PORT = 1234;
    static String IP = "127.0.0.1";

    public static void main(String[] args) {
        View view = new CLIView();
        if (Arrays.asList(args).contains("--cli")) {
            view = new CLIView();
        } else {
            // view = new GUIView();
        }

        try {
            Registry registry = LocateRegistry.getRegistry(IP, PORT);
            Loggable server = (Loggable) registry.lookup("Loggable");

            ClientObserver observer = new ClientObserver(view);
            UnicastRemoteObject.exportObject(observer, 0);
            view.setObserver(observer);

            view.showLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
