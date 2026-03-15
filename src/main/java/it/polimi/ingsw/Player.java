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

    public Player(Color color){ //perhaps a different implementation is needed
        this.nickname = "";
        this.password = "";
        this.prestigePoints = 0;
        this.foodAmount = 0;
        this.color = color;
        tribe = new Tribe();
    }

    public Tribe getTribe(){
        return tribe;
    }

    public int  getPrestigePoints(){
        return prestigePoints;
    }

    public void setPrestigePoints(int prestigePoints){
        this.prestigePoints = prestigePoints;
    }

    public int getFoodAmount(){
        return foodAmount;
    }

    public void setFoodAmount(int foodAmount){
        this.foodAmount = foodAmount;
    }

    public void changePrestigePoints(int variation){
        prestigePoints += variation;
    }

    public void changeFoodAmount(int variation){
        foodAmount += variation;
    }
}
