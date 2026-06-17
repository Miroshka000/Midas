package miroshka.midas.shop.offer;

import miroshka.midas.shop.ShopProduct;

public record SpecialOfferItem(
        ShopProduct product,
        int discountPercent,
        long offerPrice,
        boolean fixedSpecialPrice
) {
}
