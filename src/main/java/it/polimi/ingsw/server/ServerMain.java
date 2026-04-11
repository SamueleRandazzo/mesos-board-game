package it.polimi.ingsw.server;

import it.polimi.ingsw.Loggable;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerMain implements Loggable {
    static int PORT = 1234;

    public static void main( String[] args ) {
        try {
            System.out.println( "Hello from Server!" );
            ServerMain obj = new ServerMain();

            Loggable stub = (Loggable) UnicastRemoteObject.exportObject(obj, PORT);

            Registry registry = LocateRegistry.createRegistry(PORT);
            registry.bind("Loggable", stub);
            System.out.println("Server ready");

        } catch (Exception e) {
            System.err.println("Server error: " + e);
            e.printStackTrace();
        }
    }

    public boolean login(String nickname) throws RemoteException {
        return true;
    }

    public boolean logout(String nickname) throws RemoteException {
        return true;
    }
}
