package miroshka.allayshop.shop.offer;

import miroshka.allayshop.shop.ShopProduct;

public record SpecialOfferItem(
        ShopProduct product,
        int discountPercent,
        long offerPrice,
        boolean fixedSpecialPrice
) {
}
