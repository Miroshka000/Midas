package miroshka.midas.buyer.offer;

import miroshka.midas.buyer.BuyerProduct;

public record BuyerOfferItem(
        BuyerProduct product,
        double multiplier,
        long offerPrice,
        boolean fixedSpecialPrice
) {
}
