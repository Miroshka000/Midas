package miroshka.allayshop.shop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShopPriceCalculatorTest {
    @Test
    void calculatesProportionalPrice() {
        var product = new ShopProduct();
        product.setId("carrot");
        product.setMinAmount(8);
        product.setPrice(4000);

        assertEquals(4000, ShopPriceCalculator.calculate(product, 8));
        assertEquals(8000, ShopPriceCalculator.calculate(product, 16));
        assertEquals(16000, ShopPriceCalculator.calculate(product, 32));
    }

    @Test
    void appliesDiscount() {
        assertEquals(9000, ShopPriceCalculator.applyDiscount(10000, 10));
    }
}
