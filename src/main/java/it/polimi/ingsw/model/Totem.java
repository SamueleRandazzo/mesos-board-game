package it.polimi.ingsw.model;


import it.polimi.ingsw.model.Enum.Color;

public class Totem {

    private final Color color;

    /* Constructor: assigns a color (basically a player) to the totem, the param is the color of the owner player*/
    public Totem(Color color){
        this.color = color;
    }

    /* returns the color of this totem*/
    public Color getColor(){

        return this.color;
    }
}
