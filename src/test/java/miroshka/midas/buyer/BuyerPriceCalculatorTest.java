package miroshka.midas.buyer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuyerPriceCalculatorTest {
    @Test
    void calculatesProportionalSellPrice() {
        var product = new BuyerProduct();
        product.setId("carrot");
        product.setMinAmount(8);
        product.setPrice(4000);

        assertEquals(4000, BuyerPriceCalculator.calculate(product, 8));
        assertEquals(8000, BuyerPriceCalculator.calculate(product, 16));
        assertEquals(16000, BuyerPriceCalculator.calculate(product, 32));
    }

    @Test
    void appliesBuyerOfferMultiplier() {
        assertEquals(4800, BuyerPriceCalculator.applyMultiplier(4000, 1.2));
    }
}
