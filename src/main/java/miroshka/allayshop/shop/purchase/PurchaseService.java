package miroshka.allayshop.shop.purchase;

import miroshka.allayshop.shop.delivery.ShopDeliveryService;
import miroshka.allayshop.shop.economy.EconomyService;
import org.allaymc.api.entity.interfaces.EntityPlayer;

public final class PurchaseService {
    private final EconomyService economyService;
    private final ShopDeliveryService deliveryService;

    public PurchaseService(EconomyService economyService, ShopDeliveryService deliveryService) {
        this.economyService = economyService;
        this.deliveryService = deliveryService;
    }

    public PurchaseResult purchase(EntityPlayer player, PurchaseRequest request) {
        if (request.amount() <= 0) {
            return PurchaseResult.fail("Количество должно быть больше нуля.");
        }
        if (!economyService.withdraw(player, request.price())) {
            return PurchaseResult.fail("Недостаточно денег.");
        }
        deliveryService.deliver(player, request.product(), request.amount());
        return PurchaseResult.success("Покупка выполнена: " + request.product().getName() + " x" + request.amount());
    }
}
