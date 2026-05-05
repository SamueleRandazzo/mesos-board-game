package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.view.GUIView;
import javafx.application.Application;
import javafx.stage.Stage;
import it.polimi.ingsw.network.NetworkManager;

public class JavaFXMain extends Application {
    private static NetworkManager staticNetwork;

    public static void startGui(NetworkManager network) {
        staticNetwork = network;
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        GUIView view = new GUIView(staticNetwork, primaryStage);
        staticNetwork.setView(view);
        view.showLogin();
    }
}