package it.polimi.ingsw;
import it.polimi.ingsw.Enum.Color;
import it.polimi.ingsw.Cards.*;

public class Player
{
    private String nickname;
    private String password;
    private int prestigePoints;
    private int foodAmount;
    private Color color;
    private Tribe tribe;
    private void sustenance(){

    }
    private void hunt(){

    }
    private void shamanicRitual(){

    }
    private void cavePaintings(){

    }
    public void changePrestigePoints(int variation){
        prestigePoints += variation;
    }
}
