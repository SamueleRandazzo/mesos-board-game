package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Enum.Color;

/**
 * Represents a player in the match. It stores the player's profile details
 * (nickname and color), their current in-game resources (prestige points and food),
 * their personal {@link Tribe} layout, and tracking values for available card picks.
 */
public class Player
{
    /**
     * The unique identity name of the player.
     */
    private String nickname;

    /**
     * The total amount of victory/prestige points accumulated by the player.
     */
    private int prestigePoints;

    /**
     * The current amount of food resources owned by the player.
     */
    private int foodAmount;

    /**
     * The game pieces color assigned to this player.
     */
    public final Color color;

    /**
     * The player's personal tribe grid layout where acquired cards are organized.
     */
    private Tribe tribe;

    /**
     * The number of available picks or actions remaining for the upper track/deck row.
     */
    private int upperPick;

    /**
     * The number of available picks or actions remaining for the lower track/deck row.
     */
    private int lowerPick;

    /**
     * Constructs a new Player instance with a specified color and nickname.
     * Initial resources (prestige and food) and card picks are set to 0,
     * and a blank personal Tribe is assigned.
     *
     * @param color    the color assigned to the player
     * @param nickname the unique identification name of the player
     */
    public Player(Color color, String nickname){
        this.nickname = nickname;
        this.prestigePoints = 0;
        this.foodAmount = 0;
        this.color = color;
        tribe = new Tribe(this);
        upperPick = 0;
        lowerPick = 0;
    }

    /**
     * Retrieves the player's personal tribe container.
     *
     * @return the {@link Tribe} reference owned by this player
     */
    public Tribe getTribe(){
        return tribe;
    }

    /**
     * Gets the player's current total prestige points.
     *
     * @return the prestige points value
     */
    public int getPrestigePoints(){
        return prestigePoints;
    }

    /**
     * Overwrites the player's total prestige points with a precise value.
     *
     * @param prestigePoints the new total prestige points count to set
     */
    public void setPrestigePoints(int prestigePoints){
        this.prestigePoints = prestigePoints;
    }

    /**
     * Gets the player's current food amount.
     *
     * @return the total food units owned
     */
    public int getFoodAmount(){
        return foodAmount;
    }

    /**
     * Overwrites the player's total food units with a precise value.
     *
     * @param foodAmount the new total food count to set
     */
    public void setFoodAmount(int foodAmount){
        this.foodAmount = foodAmount;
    }

    /**
     * Alters the current prestige points counter by adding or subtracting a delta variation.
     *
     * @param variation the amount of prestige points to add (positive) or remove (negative)
     */
    public void changePrestigePoints(int variation){
        prestigePoints += variation;
    }

    /**
     * Alters the current food counter by adding or subtracting a delta variation.
     *
     * @param variation the amount of food units to add (positive) or remove (negative)
     */
    public void changeFoodAmount(int variation){
        foodAmount += variation;
    }

    /**
     * Gets the assigned color of this player's game markers.
     *
     * @return the {@link Color} enum value
     */
    public Color getColor() { return this.color; }

    /**
     * Gets the player's unique profile identification nickname string.
     *
     * @return the profile nickname string
     */
    public String getNickname() { return this.nickname; }

    /**
     * Gets the current remaining upper track card pick allowance value.
     *
     * @return the upper pick count
     */
    public int getUpperPick() {
        return upperPick;
    }

    /**
     * Gets the current remaining lower track card pick allowance value.
     *
     * @return the lower pick count
     */
    public int getLowerPick() {
        return lowerPick;
    }

    /**
     * Direct-sets a definitive counter allowance bound for the upper track picks.
     *
     * @param upperPick the updated absolute target upper pick value to apply
     */
    public void setUpperPick(int upperPick) {
        this.upperPick = upperPick;
    }

    /**
     * Direct-sets a definitive counter allowance bound for the lower track picks.
     *
     * @param lowerPick the updated absolute target lower pick value to apply
     */
    public void setLowerPick(int lowerPick) {
        this.lowerPick = lowerPick;
    }

    /**
     * Increases or decreases the upper track card pick count by a specific delta variation.
     *
     * @param var the amount to modify the upper pick counter by
     */
    public void changeUpperPick(int var) {
        this.upperPick += var;
    }

    /**
     * Increases or decreases the lower track card pick count by a specific delta variation.
     *
     * @param var the amount to modify the lower pick counter by
     */
    public void changeLowerPick(int var) {
        this.lowerPick += var;
    }
}