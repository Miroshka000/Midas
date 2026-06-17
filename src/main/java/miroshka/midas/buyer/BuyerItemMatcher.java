package miroshka.midas.buyer;

import org.allaymc.api.item.ItemStack;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.utils.identifier.IdentifierUtils;

public final class BuyerItemMatcher {
    private BuyerItemMatcher() {
    }

    public static boolean matches(BuyerProduct product, ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmptyOrAir()) {
            return false;
        }
        var identifier = IdentifierUtils.tryParse(product.getItem().getMaterial());
        var itemType = identifier == null ? null : Registries.ITEMS.get(identifier);
        if (itemType == null || itemStack.getItemType() != itemType) {
            return false;
        }
        if (itemStack.getMeta() != product.getItem().getData()) {
            return false;
        }
        if (product.getItem().getDisplayName() != null
                && !product.getItem().getDisplayName().isBlank()
                && !product.getItem().getDisplayName().equals(itemStack.getCustomName())) {
            return false;
        }
        if (!product.getItem().getLore().isEmpty() && !product.getItem().getLore().equals(itemStack.getLore())) {
            return false;
        }
        return product.getItem().getEnchantments().entrySet()
                .stream()
                .allMatch(entry -> {
                    var enchantmentId = IdentifierUtils.tryParse(entry.getKey());
                    var enchantment = enchantmentId == null ? null : Registries.ENCHANTMENTS.getByK2(enchantmentId);
                    return enchantment != null && itemStack.getEnchantmentLevel(enchantment) == entry.getValue();
                });
    }

    public static boolean isPerfectCondition(ItemStack itemStack) {
        return itemStack != null && itemStack.getMaxDamage() <= 0 || itemStack != null && itemStack.getDamage() == 0;
    }
}
