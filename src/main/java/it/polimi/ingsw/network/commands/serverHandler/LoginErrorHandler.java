package it.polimi.ingsw.network.commands.serverHandler;

import it.polimi.ingsw.network.commands.ServerCommandHandler;
import it.polimi.ingsw.view.View;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LoginErrorHandler implements ServerCommandHandler {
    @Override
    public void handle(String[] args, View view, ObjectMapper mapper) {
        view.showError(String.join(" ", args));
        view.showLogin();
    }
}