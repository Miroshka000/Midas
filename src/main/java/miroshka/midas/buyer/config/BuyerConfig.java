package miroshka.midas.buyer.config;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import lombok.Getter;
import lombok.Setter;
import miroshka.midas.buyer.BuyerCategory;
import miroshka.midas.buyer.BuyerProduct;
import miroshka.midas.buyer.autobuyer.AutoBuyerSettings;
import miroshka.midas.buyer.offer.BuyerSpecialOfferSettings;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Header("Midas buyer configuration")
public class BuyerConfig extends OkaeriConfig {
    @Comment("Main buyer form title.")
    private String title = "Скупщик";

    @Comment("Categories shown in the buyer form.")
    private List<BuyerCategory> categories = defaultCategories();

    @Comment("Special offer settings for buyer.")
    private BuyerSpecialOfferSettings specialOffer = BuyerSpecialOfferSettings.defaults();

    @Comment("Auto-buyer terminal settings.")
    private AutoBuyerSettings autoBuyer = new AutoBuyerSettings();

    public static BuyerConfig loadConfig(Path dataFolder) {
        return ConfigManager.create(BuyerConfig.class, config -> {
            config.withConfigurer(new YamlSnakeYamlConfigurer());
            config.withBindFile(dataFolder.resolve("buyer.yml"));
            config.saveDefaults();
            config.load(true);
        });
    }

    private static List<BuyerCategory> defaultCategories() {
        var food = new BuyerCategory();
        food.setId("food");
        food.setName("Еда");
        food.setDescription("Предметы, которые можно продать скупщику.");
        food.setImageType("path");
        food.setImage("textures/items/carrot");

        var carrot = new BuyerProduct();
        carrot.setId("carrot");
        carrot.setName("Морковь");
        carrot.setDescription("Обычная морковь.");
        carrot.setMinAmount(8);
        carrot.setPrice(4000);
        carrot.setAmountScrollEnabled(true);
        carrot.setMaxAmount(640);
        carrot.setItem(new BuyerProduct.ItemFilter("minecraft:carrot", 0, null, List.of(), Map.of()));

        food.setProducts(new ArrayList<>(List.of(carrot)));
        return new ArrayList<>(List.of(food));
    }
}
