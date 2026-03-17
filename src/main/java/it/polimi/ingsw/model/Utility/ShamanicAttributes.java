package it.polimi.ingsw.model.Utility;

public class ShamanicAttributes {
    private int stars;
    private int starsFromCards;
    private boolean preventLoss;
    private boolean doubleOnWinning;

    public ShamanicAttributes() {
        stars = 0;
        starsFromCards = 0;
        preventLoss = false;
        doubleOnWinning = false;
    }

    public int getStars() {
        return stars;
    }

    public boolean isPreventLoss() {
        return preventLoss;
    }

    public boolean isDoubleOnWinning() {
        return doubleOnWinning;
    }

    public void addStars(int stars) {
        this.stars += stars;
    }

    public void addStarsFromCards(int s) {
        this.stars += s;
        this.starsFromCards += s;
    }

    public void setPreventLoss(boolean preventLoss) {
        this.preventLoss = preventLoss;
    }

    public void setDoubleOnWinning(boolean doubleOnWinning) {
        this.doubleOnWinning = doubleOnWinning;
    }

    public void reset() {
        this.doubleOnWinning = false;
        this.preventLoss = false;
        this.stars = this.starsFromCards;
    }
}
