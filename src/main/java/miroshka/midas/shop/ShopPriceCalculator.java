package miroshka.midas.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ShopPriceCalculator {
    private ShopPriceCalculator() {
    }

    public static long calculate(ShopProduct product, int amount) {
        validate(product, amount);
        var multiplier = BigDecimal.valueOf(amount)
                .divide(BigDecimal.valueOf(product.getMinAmount()), 6, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(product.getPrice())
                .multiply(multiplier)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    public static long applyDiscount(long price, int discountPercent) {
        if (discountPercent <= 0) {
            return price;
        }
        if (discountPercent >= 100) {
            throw new IllegalArgumentException("discountPercent must be less than 100");
        }
        return BigDecimal.valueOf(price)
                .multiply(BigDecimal.valueOf(100L - discountPercent))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    private static void validate(ShopProduct product, int amount) {
        if (product.getMinAmount() <= 0) {
            throw new IllegalArgumentException("minAmount must be positive for product " + product.getId());
        }
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("price must not be negative for product " + product.getId());
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
    }
}
