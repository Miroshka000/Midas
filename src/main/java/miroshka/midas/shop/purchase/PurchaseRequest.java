package miroshka.midas.shop.purchase;

import miroshka.midas.shop.ShopProduct;

public record PurchaseRequest(
        String playerName,
        ShopProduct product,
        int amount,
        long price
) {
}
