module it.polimi.ingsw {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires java.desktop;
    requires java.rmi;

    // External libraries
    requires org.jetbrains.annotations;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;

    // Export
    exports it.polimi.ingsw.view.GUI;
    exports it.polimi.ingsw.network;
    exports it.polimi.ingsw.client;
    exports it.polimi.ingsw.network.DTO;
    exports it.polimi.ingsw.view;

    // Opens
    opens it.polimi.ingsw.view.GUI to javafx.graphics, javafx.fxml;
    opens it.polimi.ingsw.model to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.model.factories to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.network.DTO to com.fasterxml.jackson.databind;
}