package it.polimi.ingsw.model.SustenanceDiscountTypes;

import it.polimi.ingsw.model.Interfaces.SustenanceDiscountType;

public class BindersDiscount implements SustenanceDiscountType {
    @Override
    public int cardNumber() {
        return 0;
    }
}
