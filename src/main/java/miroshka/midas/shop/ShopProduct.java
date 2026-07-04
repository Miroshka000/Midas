package miroshka.midas.shop;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ShopProduct extends OkaeriConfig {
    private String id = "";
    private String name = "";
    private String description = "";
    private ShopProductType type = ShopProductType.ITEM;
    private int minAmount = 1;
    @Comment("EconomyAPI currency for this product price: coins or lumens.")
    private String currency = "coins";
    private long price = 0;
    private boolean amountScrollEnabled = true;
    private int maxAmount = 64;
    private ItemReward item = new ItemReward();
    private CommandReward command = new CommandReward();
    private EffectReward effect = new EffectReward();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemReward extends OkaeriConfig {
        private String material = "minecraft:stone";
        private int data = 0;
        private String displayName;
        private List<String> lore = new ArrayList<>();
        private Map<String, Integer> enchantments = new HashMap<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommandReward extends OkaeriConfig {
        private List<String> commands = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EffectReward extends OkaeriConfig {
        private String type = "minecraft:speed";
        private int level = 1;
        private int seconds = 30;
        private boolean hideParticles = false;
    }
}
