package miroshka.allayshop.shop.delivery;

import miroshka.allayshop.shop.ShopProduct;
import miroshka.allayshop.shop.ShopProductType;
import org.allaymc.api.container.ContainerTypes;
import org.allaymc.api.entity.effect.EffectType;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.item.type.ItemType;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;
import org.allaymc.api.utils.identifier.IdentifierUtils;

public final class ShopDeliveryService {
    private final Server server;

    public ShopDeliveryService(Server server) {
        this.server = server;
    }

    public void deliver(EntityPlayer player, ShopProduct product, int amount) {
        switch (product.getType()) {
            case ITEM -> giveItem(player, product, amount);
            case COMMAND -> executeCommands(player, product, amount);
            case EFFECT -> giveEffect(player, product);
        }
    }

    private void giveItem(EntityPlayer player, ShopProduct product, int amount) {
        var identifier = IdentifierUtils.tryParse(product.getItem().getMaterial());
        ItemType<?> itemType = identifier == null ? null : Registries.ITEMS.get(identifier);
        if (itemType == null) {
            throw new IllegalArgumentException("Unknown item material: " + product.getItem().getMaterial());
        }
        var remaining = amount;
        var maxStackSize = itemType.getItemData().maxStackSize();
        while (remaining > 0) {
            int count = Math.min(remaining, maxStackSize);
            remaining -= count;
            var itemStack = itemType.createItemStack(count, product.getItem().getData());
            applyItemMeta(itemStack, product);
            player.getContainer(ContainerTypes.INVENTORY).tryAddItem(itemStack);
            if (itemStack.getCount() > 0) {
                player.dropItemInPlayerPos(itemStack);
            }
        }
    }

    private void applyItemMeta(org.allaymc.api.item.ItemStack itemStack, ShopProduct product) {
        if (product.getItem().getDisplayName() != null && !product.getItem().getDisplayName().isBlank()) {
            itemStack.setCustomName(product.getItem().getDisplayName());
        }
        if (!product.getItem().getLore().isEmpty()) {
            itemStack.setLore(product.getItem().getLore());
        }
        product.getItem().getEnchantments().forEach((name, level) -> {
            var identifier = IdentifierUtils.tryParse(name);
            var enchantment = identifier == null ? null : Registries.ENCHANTMENTS.getByK2(identifier);
            if (enchantment == null) {
                throw new IllegalArgumentException("Unknown enchantment: " + name);
            }
            itemStack.addEnchantment(enchantment, level);
        });
    }

    private void executeCommands(EntityPlayer player, ShopProduct product, int amount) {
        for (String command : product.getCommand().getCommands()) {
            var prepared = command
                    .replace("{player}", player.getDisplayName())
                    .replace("{amount}", Integer.toString(amount));
            Registries.COMMANDS.execute(server, prepared);
        }
    }

    private void giveEffect(EntityPlayer player, ShopProduct product) {
        var identifier = IdentifierUtils.tryParse(product.getEffect().getType());
        EffectType effectType = identifier == null ? null : Registries.EFFECTS.getByK2(identifier);
        if (effectType == null) {
            throw new IllegalArgumentException("Unknown effect type: " + product.getEffect().getType());
        }
        player.addEffect(effectType.createInstance(
                product.getEffect().getLevel(),
                product.getEffect().getSeconds() * 20,
                !product.getEffect().isHideParticles()
        ));
    }
}
