package miroshka.midas.buyer.sell;

import miroshka.midas.buyer.BuyerProduct;

public record SellRequest(
        BuyerProduct product,
        int amount,
        long price
) {
}
