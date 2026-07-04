package miroshka.midas.buyer.sell;

import miroshka.midas.buyer.inventory.BuyerInventoryService;
import miroshka.midas.i18n.MessageKey;
import miroshka.midas.i18n.MessageService;
import miroshka.midas.shop.economy.EconomyService;
import org.allaymc.api.entity.interfaces.EntityPlayer;

import java.util.Map;

public final class SellService {
    private final EconomyService economyService;
    private final BuyerInventoryService inventoryService;
    private final MessageService messages;

    public SellService(EconomyService economyService, BuyerInventoryService inventoryService, MessageService messages) {
        this.economyService = economyService;
        this.inventoryService = inventoryService;
        this.messages = messages;
    }

    public SellResult sell(EntityPlayer player, SellRequest request) {
        if (request.amount() <= 0) {
            return SellResult.fail(messages.tr(player, MessageKey.SELL_INVALID_AMOUNT));
        }
        if (request.amount() < request.product().getMinAmount()) {
            return SellResult.fail(messages.tr(player, MessageKey.SELL_BELOW_MINIMUM, Map.of(
                    "amount", request.product().getMinAmount()
            )));
        }
        if (!inventoryService.hasSellable(player, request.product(), request.amount())) {
            return SellResult.fail(messages.tr(player, MessageKey.SELL_REJECTED));
        }
        inventoryService.removeSellable(player, request.product(), request.amount());
        if (!economyService.deposit(player, request.product().getCurrency(), request.price())) {
            return SellResult.fail(messages.tr(player, MessageKey.SELL_DEPOSIT_FAILED));
        }
        return SellResult.success(messages.tr(player, MessageKey.SELL_SUCCESS, Map.of(
                "product", request.product().getName(),
                "amount", request.amount(),
                "price", request.price(),
                "currency", request.product().getCurrency()
        )));
    }
}
