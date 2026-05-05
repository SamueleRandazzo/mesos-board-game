package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.view.GUIView;
import javafx.application.Application;
import javafx.stage.Stage;
import it.polimi.ingsw.network.NetworkManager;

public class JavaFXMain extends Application {
    private static NetworkManager staticNetwork;
    private static String IP;
    private static int port;

    public static void startGui(NetworkManager network,String IP, int port) {
        staticNetwork = network;
        JavaFXMain.IP = IP;
        JavaFXMain.port = port;
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        GUIView view = new GUIView(staticNetwork, primaryStage);
        staticNetwork.setView(view);

        //shows the login scene
        view.showLogin();

        //try the connection with server
        try {
            staticNetwork.connect(IP, port);
            System.out.println("Connesso al server " + IP + " su porta:" + port);
        } catch (Exception e) {
            System.err.println("Impossibile connettersi al server: " + e.getMessage());
            // Mostra l'errore direttamente nella GUI usando il controller corrente
            view.showError("Impossibile connettersi al server (" + IP + ":" + port + ")");
        }
    }
}