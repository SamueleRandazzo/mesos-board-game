package it.polimi.ingsw.network.commands;

import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.network.RemoteController;

public interface ClientCommandHandler {
    void handle(String[] args, Lobby lobby, RemoteController controller);
}