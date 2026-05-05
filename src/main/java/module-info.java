module it.polimi.ingsw {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    requires java.rmi;

    requires org.jetbrains.annotations;
    requires com.fasterxml.jackson.databind;

    exports it.polimi.ingsw.view.GUI;
    exports it.polimi.ingsw.network;

    opens it.polimi.ingsw.view.GUI to javafx.graphics, javafx.fxml;
    opens it.polimi.ingsw.model to com.fasterxml.jackson.databind;
}