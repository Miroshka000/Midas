package miroshka.allayshop.shop.purchase;

import miroshka.allayshop.shop.ShopProduct;

public record PurchaseRequest(
        String playerName,
        ShopProduct product,
        int amount,
        long price
) {
}
