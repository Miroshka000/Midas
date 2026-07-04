package miroshka.midas.buyer.autobuyer;

import miroshka.midas.buyer.BuyerCatalog;
import miroshka.midas.buyer.BuyerItemMatcher;
import miroshka.midas.buyer.BuyerPriceCalculator;
import miroshka.midas.i18n.MessageKey;
import miroshka.midas.i18n.MessageService;
import miroshka.midas.shop.economy.EconomyService;
import org.allaymc.api.container.FakeContainerFactory;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AutoBuyerMenuService {
    private final BuyerCatalog catalog;
    private final EconomyService economyService;
    private final MessageService messages;

    public AutoBuyerMenuService(BuyerCatalog catalog, EconomyService economyService, MessageService messages) {
        this.catalog = catalog;
        this.economyService = economyService;
        this.messages = messages;
    }

    public void open(EntityPlayer player) {
        var controller = player.getController();
        if (controller == null) {
            return;
        }
        var menu = FakeContainerFactory.getFactory().createFakeDoubleChestContainer();
        menu.setCustomName(messages.tr(player, MessageKey.AUTO_BUYER_MENU_TITLE));
        menu.addCloseListener(viewer -> process(player, menu.getItemStackArray()));
        menu.addPlayer(controller);
    }

    private void process(EntityPlayer player, ItemStack[] items) {
        var payments = new LinkedHashMap<String, Long>();
        var rejected = false;
        var originalItems = new ArrayList<ItemStack>();
        var leftovers = new ArrayList<ItemStack>();
        for (ItemStack itemStack : items) {
            if (itemStack == null || itemStack.isEmptyOrAir()) {
                continue;
            }
            originalItems.add(itemStack.copy());
            var match = catalog.bestMatch(itemStack);
            if (match.isEmpty()
                    || !BuyerItemMatcher.isPerfectCondition(itemStack)
                    || itemStack.getCount() < match.get().getMinAmount()) {
                leftovers.add(itemStack.copy());
                rejected = true;
                continue;
            }

            var product = match.get();
            var sellAmount = itemStack.getCount() - itemStack.getCount() % product.getMinAmount();
            var leftover = itemStack.getCount() - sellAmount;
            payments.merge(product.getCurrency(), BuyerPriceCalculator.calculate(product, sellAmount), Long::sum);
            if (leftover > 0) {
                var copy = itemStack.copy();
                copy.setCount(leftover);
                leftovers.add(copy);
            }
        }
        if (!payments.isEmpty() && !economyService.depositAll(player, payments)) {
            returnItems(player, originalItems);
            messages.send(player, MessageKey.AUTO_BUYER_DEPOSIT_FAILED);
            return;
        }
        returnItems(player, leftovers);
        if (!payments.isEmpty()) {
            messages.send(player, MessageKey.AUTO_BUYER_SOLD, Map.of("price", formatPayments(payments)));
        }
        if (rejected) {
            messages.send(player, MessageKey.SELL_REJECTED);
        }
    }

    private String formatPayments(Map<String, Long> payments) {
        return payments.entrySet().stream()
                .map(entry -> entry.getValue() + " " + entry.getKey())
                .reduce((left, right) -> left + ", " + right)
                .orElse("0");
    }

    private void returnItems(EntityPlayer player, List<ItemStack> items) {
        for (ItemStack itemStack : items) {
            returnItem(player, itemStack);
        }
    }

    private void returnItem(EntityPlayer player, ItemStack itemStack) {
        var copy = itemStack.copy();
        if (!player.tryAddItem(copy) && copy.getCount() > 0) {
            player.dropItemInPlayerPos(copy);
        }
    }
}
