package miroshka.midas.buyer;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class BuyerPriceCalculator {
    private BuyerPriceCalculator() {
    }

    public static long calculate(BuyerProduct product, int amount) {
        validate(product, amount);
        var multiplier = BigDecimal.valueOf(amount)
                .divide(BigDecimal.valueOf(product.getMinAmount()), 6, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(product.getPrice())
                .multiply(multiplier)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    public static long applyMultiplier(long price, double multiplier) {
        if (multiplier <= 0) {
            throw new IllegalArgumentException("multiplier must be positive");
        }
        return BigDecimal.valueOf(price)
                .multiply(BigDecimal.valueOf(multiplier))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    private static void validate(BuyerProduct product, int amount) {
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
