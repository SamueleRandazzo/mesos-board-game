package it.polimi.ingsw.network.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.view.View;

public interface ServerCommandHandler {
    void handle(String[] args, View view, ObjectMapper mapper);
}