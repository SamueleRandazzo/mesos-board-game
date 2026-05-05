module it.polimi.ingsw {
    // Moduli JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics; // Spesso necessario esplicitamente

    // Altri moduli Java
    requires java.desktop;
    requires java.rmi;

    // Librerie esterne
    requires org.jetbrains.annotations;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;

    // Esportazioni per il funzionamento del progetto
    exports it.polimi.ingsw.view.GUI;
    exports it.polimi.ingsw.network;
    exports it.polimi.ingsw.client; // Necessario se lanci il main da qui
    exports it.polimi.ingsw.network.DTO;

    // --- SEZIONE OPENS (Cruciale per Reflection) ---


    opens it.polimi.ingsw.view.GUI to javafx.graphics, javafx.fxml;

    // Permette a Jackson di leggere i JSON e creare gli oggetti nei pacchetti model
    opens it.polimi.ingsw.model to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.model.factories to com.fasterxml.jackson.databind;

    // È caldamente consigliato esportarlo anche, se lo usi in altri moduli
    // Se hai altre classi (es. DTO) che Jackson deve leggere, aggiungi qui:
    opens it.polimi.ingsw.network.DTO to com.fasterxml.jackson.databind;
}