package miroshka.midas.buyer.offer;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import lombok.Setter;
import miroshka.midas.buyer.BuyerProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BuyerSpecialOfferSettings extends OkaeriConfig {
    private boolean enabled = true;
    private int refreshHours = 8;
    private int refreshCheckSeconds = 60;
    private int regularProductCount = 2;
    private double multiplierMin = 1.05;
    private double multiplierMax = 1.30;
    private List<BuyerProduct> specialProducts = new ArrayList<>();

    public static BuyerSpecialOfferSettings defaults() {
        var settings = new BuyerSpecialOfferSettings();
        var product = new BuyerProduct();
        product.setId("nether_star_special");
        product.setName("Звезда Незера");
        product.setDescription("Специальная цена уже указана в конфиге.");
        product.setMinAmount(1);
        product.setPrice(50000);
        product.setAmountScrollEnabled(false);
        product.setItem(new BuyerProduct.ItemFilter("minecraft:nether_star", 0, null, List.of(), Map.of()));
        settings.setSpecialProducts(new ArrayList<>(List.of(product)));
        return settings;
    }
}
