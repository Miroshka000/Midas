package miroshka.midas.buyer;

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
public class BuyerProduct extends OkaeriConfig {
    private String id = "";
    private String name = "";
    private String description = "";
    private int minAmount = 1;
    @Comment("EconomyAPI currency paid for this product: coins or lumens.")
    private String currency = "coins";
    private long price = 0;
    private boolean amountScrollEnabled = true;
    private int maxAmount = 64;
    private ItemFilter item = new ItemFilter();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemFilter extends OkaeriConfig {
        private String material = "minecraft:stone";
        private int data = 0;
        private String displayName;
        private List<String> lore = new ArrayList<>();
        private Map<String, Integer> enchantments = new HashMap<>();
    }
}
