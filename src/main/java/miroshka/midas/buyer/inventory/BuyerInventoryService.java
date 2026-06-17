package miroshka.midas.buyer.inventory;

import miroshka.midas.buyer.BuyerItemMatcher;
import miroshka.midas.buyer.BuyerProduct;
import org.allaymc.api.container.ContainerTypes;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.item.ItemStack;

public final class BuyerInventoryService {
    public boolean hasSellable(EntityPlayer player, BuyerProduct product, int amount) {
        return countSellable(player, product) >= amount;
    }

    public int countSellable(EntityPlayer player, BuyerProduct product) {
        var inventory = player.getContainer(ContainerTypes.INVENTORY);
        var count = 0;
        for (ItemStack itemStack : inventory.getItemStackArray()) {
            if (canSell(product, itemStack)) {
                count += itemStack.getCount();
            }
        }
        return count;
    }

    public void removeSellable(EntityPlayer player, BuyerProduct product, int amount) {
        var inventory = player.getContainer(ContainerTypes.INVENTORY);
        var remaining = amount;
        var items = inventory.getItemStackArray();
        for (int slot = 0; slot < items.length && remaining > 0; slot++) {
            var itemStack = items[slot];
            if (!canSell(product, itemStack)) {
                continue;
            }
            var take = Math.min(remaining, itemStack.getCount());
            remaining -= take;
            if (itemStack.getCount() == take) {
                inventory.clearSlot(slot);
                continue;
            }
            var updated = itemStack.copy();
            updated.setCount(itemStack.getCount() - take);
            inventory.setItemStack(slot, updated);
        }
    }

    private boolean canSell(BuyerProduct product, ItemStack itemStack) {
        return BuyerItemMatcher.matches(product, itemStack) && BuyerItemMatcher.isPerfectCondition(itemStack);
    }
}
