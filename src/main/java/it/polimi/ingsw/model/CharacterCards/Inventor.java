package it.polimi.ingsw.model.CharacterCards;
import it.polimi.ingsw.model.Cards.Card;
import it.polimi.ingsw.model.Cards.Tribe;
import it.polimi.ingsw.model.Enum.InventionIcon;
import it.polimi.ingsw.model.Interfaces.PlayerTribe;
import it.polimi.ingsw.model.Interfaces.TribeDeck;

public class Inventor extends Card implements TribeDeck, PlayerTribe {
    private final InventionIcon inventionIcon;

    public Inventor(int era, int minPlayer, boolean isObtainable, InventionIcon inventionIcon){
        super(era, minPlayer, isObtainable);
        this.inventionIcon = inventionIcon;
    }

    public InventionIcon getInventionIcon(){
        return this.inventionIcon;
    }

    @Override
    public void addToTribe(Tribe tribe) {
        tribe.addCard(this);
    }
}
