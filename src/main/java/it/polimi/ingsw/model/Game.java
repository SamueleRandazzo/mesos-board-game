package it.polimi.ingsw.model;

import java.util.*;

public class Game { //I would implement as a singleton
    private List<Player> players;
    private static Game game;
    private Game(){
        //to implement
    }
    public static Game game(){
        if(game==null)
            game=new Game();
        return game;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
