package miroshka.midas;

import lombok.Getter;
import miroshka.midas.buyer.BuyerCatalog;
import miroshka.midas.buyer.autobuyer.AutoBuyerListener;
import miroshka.midas.buyer.autobuyer.AutoBuyerMenuService;
import miroshka.midas.buyer.autobuyer.AutoBuyerTerminalService;
import miroshka.midas.buyer.command.AutoBuyerCommand;
import miroshka.midas.buyer.command.BuyerCommand;
import miroshka.midas.buyer.command.DeleteAutoBuyerCommand;
import miroshka.midas.buyer.config.BuyerConfig;
import miroshka.midas.buyer.form.BuyerFormService;
import miroshka.midas.buyer.inventory.BuyerInventoryService;
import miroshka.midas.buyer.offer.BuyerOfferService;
import miroshka.midas.buyer.sell.SellService;
import miroshka.midas.command.ShopCommand;
import miroshka.midas.config.ShopConfig;
import miroshka.midas.form.ShopFormService;
import miroshka.midas.i18n.MessageService;
import miroshka.midas.shop.ShopCatalog;
import miroshka.midas.shop.delivery.ShopDeliveryService;
import miroshka.midas.shop.economy.EconomyApiService;
import miroshka.midas.shop.offer.SpecialOfferService;
import miroshka.midas.shop.purchase.PurchaseService;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;
import org.slf4j.Logger;

public final class MidasPlugin extends Plugin {
    @Getter
    private ShopConfig shopConfig;
    @Getter
    private BuyerConfig buyerConfig;
    private SpecialOfferService shopOfferService;
    private BuyerOfferService buyerOfferService;

    @Override
    public void onEnable() {
        var dataFolder = getPluginContainer().dataFolder();
        this.shopConfig = ShopConfig.loadConfig(dataFolder);
        this.buyerConfig = BuyerConfig.loadConfig(dataFolder);
        var messageService = new MessageService();

        var economyService = new EconomyApiService();
        var shopCatalog = new ShopCatalog(shopConfig);
        var deliveryService = new ShopDeliveryService(Server.getInstance());
        var purchaseService = new PurchaseService(economyService, deliveryService, messageService);
        this.shopOfferService = new SpecialOfferService(shopCatalog, shopConfig.getSpecialOffer(), getPluginLogger());
        this.shopOfferService.refresh();

        var buyerCatalog = new BuyerCatalog(buyerConfig);
        var buyerInventoryService = new BuyerInventoryService();
        var sellService = new SellService(economyService, buyerInventoryService, messageService);
        this.buyerOfferService = new BuyerOfferService(buyerCatalog, buyerConfig.getSpecialOffer(), getPluginLogger(), messageService);
        this.buyerOfferService.refresh();
        var autoBuyerTerminalService = new AutoBuyerTerminalService(buyerConfig);
        var autoBuyerMenuService = new AutoBuyerMenuService(buyerCatalog, economyService, messageService);

        var shopFormService = new ShopFormService(shopConfig, shopCatalog, shopOfferService, purchaseService, messageService);
        var buyerFormService = new BuyerFormService(buyerConfig, buyerCatalog, buyerOfferService, sellService, messageService);
        Registries.COMMANDS.register(new ShopCommand(shopFormService, messageService));
        Registries.COMMANDS.register(new BuyerCommand(buyerFormService, messageService));
        Registries.COMMANDS.register(new AutoBuyerCommand(autoBuyerTerminalService, messageService));
        Registries.COMMANDS.register(new DeleteAutoBuyerCommand(autoBuyerTerminalService, messageService));
        Server.getInstance().getEventBus().registerListener(new AutoBuyerListener(autoBuyerTerminalService, autoBuyerMenuService, messageService));

        Server.getInstance().getScheduler().scheduleRepeating(
                this,
                shopOfferService::refreshIfExpired,
                Math.max(20, shopConfig.getSpecialOffer().getRefreshCheckSeconds() * 20)
        );
        Server.getInstance().getScheduler().scheduleRepeating(
                this,
                buyerOfferService::refreshIfExpired,
                Math.max(20, buyerConfig.getSpecialOffer().getRefreshCheckSeconds() * 20)
        );

        logger().info(
                "Midas enabled with {} shop categories and {} buyer categories",
                shopConfig.getCategories().size(),
                buyerConfig.getCategories().size()
        );
    }

    @Override
    public void onDisable() {
        logger().info("Midas disabled");
    }

    private Logger logger() {
        return getPluginLogger();
    }
}
