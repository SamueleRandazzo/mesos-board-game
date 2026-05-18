package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Enum.Color;

public class Player
{
    private String nickname;
    private int prestigePoints;
    private int foodAmount;
    public final Color color;
    private Tribe tribe;
    private int upperPick;
    private int lowerPick;

    public Player(Color color, String nickname){
        this.nickname = nickname;
        this.prestigePoints = 0;
        this.foodAmount = 0;
        this.color = color;
        tribe = new Tribe(this);
        upperPick = 0;
        lowerPick = 0;
    }

    public Tribe getTribe(){
        return tribe;
    }

    public int getPrestigePoints(){
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

    public Color getColor() { return this.color; }

    public String getNickname() { return this.nickname; }

    public int getUpperPick() {
        return upperPick;
    }

    public int getLowerPick() {
        return lowerPick;
    }

    public void setUpperPick(int upperPick) {
        this.upperPick = upperPick;
    }

    public void setLowerPick(int lowerPick) {
        this.lowerPick = lowerPick;
    }

    public void changeUpperPick(int var) {
        this.upperPick += var;
    }

    public void changeLowerPick(int var) {
        this.lowerPick += var;
    }
}
