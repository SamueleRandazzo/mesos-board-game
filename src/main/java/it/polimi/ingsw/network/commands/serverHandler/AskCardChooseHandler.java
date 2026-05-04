package it.polimi.ingsw.network.commands.serverHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;

public class AskCardChooseHandler implements ServerCommandHandler {
    @Override
    public void handle(String[] args, View view, ObjectMapper mapper) {
        view.askCardChoose();
    }
}