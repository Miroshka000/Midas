package miroshka.midas.shop.purchase;

import miroshka.midas.i18n.MessageKey;
import miroshka.midas.i18n.MessageService;
import miroshka.midas.shop.delivery.ShopDeliveryService;
import miroshka.midas.shop.economy.EconomyService;
import org.allaymc.api.entity.interfaces.EntityPlayer;

import java.util.Map;

public final class PurchaseService {
    private final EconomyService economyService;
    private final ShopDeliveryService deliveryService;
    private final MessageService messages;

    public PurchaseService(EconomyService economyService, ShopDeliveryService deliveryService, MessageService messages) {
        this.economyService = economyService;
        this.deliveryService = deliveryService;
        this.messages = messages;
    }

    public PurchaseResult purchase(EntityPlayer player, PurchaseRequest request) {
        if (request.amount() <= 0) {
            return PurchaseResult.fail(messages.tr(player, MessageKey.PURCHASE_INVALID_AMOUNT));
        }
        if (!economyService.withdraw(player, request.product().getCurrency(), request.price())) {
            return PurchaseResult.fail(messages.tr(player, MessageKey.PURCHASE_NOT_ENOUGH_MONEY));
        }
        deliveryService.deliver(player, request.product(), request.amount());
        return PurchaseResult.success(messages.tr(player, MessageKey.PURCHASE_SUCCESS, Map.of(
                "product", request.product().getName(),
                "amount", request.amount()
        )));
    }
}
