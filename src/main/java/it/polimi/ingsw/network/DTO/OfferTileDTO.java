package it.polimi.ingsw.network.DTO;
import java.io.Serializable;

public class OfferTileDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int index;
    private final int foodBonus;
    private final int topRowDraws;
    private final int bottomRowDraws;
    private final String nickname;

    public OfferTileDTO(int index, int foodBonus, int topRowDraws, int bottomRowDraws, String nicknameOccupante) {
        this.index = index;
        this.foodBonus = foodBonus;
        this.topRowDraws = topRowDraws;
        this.bottomRowDraws = bottomRowDraws;
        this.nickname = nicknameOccupante;
    }

    public int getIndex() { return index; }
    public int getFoodBonus() { return foodBonus; }
    public int getTopRowDraws() { return topRowDraws; }
    public int getBottomRowDraws() { return bottomRowDraws; }
    public String getNickname() { return nickname; }

    public boolean isAvailable() {
        return nickname == null;
    }
}