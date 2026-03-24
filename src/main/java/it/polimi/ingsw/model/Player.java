package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Enum.Color;

public class Player
{
    private String nickname;
    private String password;
    private int prestigePoints;
    private int foodAmount;
    private final Color color;
    private Tribe tribe;

    public Player(Color color){ //perhaps a different implementation is needed
        this.nickname = "";
        this.password = "";
        this.prestigePoints = 0;
        this.foodAmount = 0;
        this.color = color;
        tribe = new Tribe(this);
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
