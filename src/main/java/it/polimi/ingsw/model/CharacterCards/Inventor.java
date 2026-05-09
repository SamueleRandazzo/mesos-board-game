package it.polimi.ingsw.model.CharacterCards;
import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Enum.InventionIcon;
import it.polimi.ingsw.model.Interfaces.TribeDeck;
import it.polimi.ingsw.model.Player;

public class Inventor extends Card implements TribeDeck {
    private final InventionIcon inventionIcon;

    public Inventor(String id, int era, int minPlayer, boolean isObtainable, InventionIcon inventionIcon){
        super(id, era, minPlayer, isObtainable);
        this.inventionIcon = inventionIcon;
    }

    public InventionIcon getInventionIcon(){
        return this.inventionIcon;
    }

    public void applyTo(Player p) {
        p.getTribe().addCard(this);
    }
}
