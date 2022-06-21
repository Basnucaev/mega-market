package com.megamarket.service.impl.help;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransferInteger {

    private int offersSumPrice;
    private int offersCount;

    public void plusValuesFromOther(TransferInteger other) {
        this.offersSumPrice += other.getOffersSumPrice();
        this.offersCount += other.getOffersCount();
    }

    ;
}
