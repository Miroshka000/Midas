package miroshka.midas.shop.offer;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import lombok.Setter;
import miroshka.midas.shop.ShopProduct;
import miroshka.midas.shop.ShopProductType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SpecialOfferSettings extends OkaeriConfig {
    private boolean enabled = true;
    private int refreshHours = 8;
    private int refreshCheckSeconds = 60;
    private int regularProductCount = 2;
    private int discountMinPercent = 5;
    private int discountMaxPercent = 14;
    private List<ShopProduct> specialProducts = new ArrayList<>();

    public static SpecialOfferSettings defaults() {
        var settings = new SpecialOfferSettings();
        var product = new ShopProduct();
        product.setId("vip_key");
        product.setName("Ключ VIP-кейса");
        product.setDescription("Специальная цена уже указана в конфиге");
        product.setType(ShopProductType.COMMAND);
        product.setMinAmount(1);
        product.setCurrency("lumens");
        product.setPrice(15000);
        product.setAmountScrollEnabled(false);
        product.getCommand().setCommands(List.of("crate key give {player} vip 1"));
        settings.setSpecialProducts(new ArrayList<>(List.of(product)));
        return settings;
    }
}
