package it.polimi.ingsw.network.commands;

import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.network.RemoteController;

import java.io.PrintWriter;

public interface ClientCommandHandler {
    void handle(String[] args, Lobby lobby, RemoteController controller, PrintWriter out);
}