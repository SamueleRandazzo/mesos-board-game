package it.polimi.ingsw;
import it.polimi.ingsw.Enum.Color;
import it.polimi.ingsw.Enum.EventType;
import it.polimi.ingsw.Cards.*;

public class Player
{
    private String nickname;
    private String password;
    private int prestigePoints;
    private int foodAmount;
    private Color color;
    private Tribe tribe;

    public Player(Color color){ //perhaps a different implementation is needed
        this.nickname = "";
        this.password = "";
        this.prestigePoints = 0;
        this.foodAmount = 0;
        this.color = color;
        tribe = new Tribe();
    }

    public void changePrestigePoints(int variation){
        prestigePoints += variation;
    }

    public void getTribe(){

    }
}
