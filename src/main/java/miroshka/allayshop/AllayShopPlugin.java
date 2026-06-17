package miroshka.allayshop;

import miroshka.allayshop.command.ShopCommand;
import miroshka.allayshop.config.ShopConfig;
import miroshka.allayshop.form.ShopFormService;
import miroshka.allayshop.shop.ShopCatalog;
import miroshka.allayshop.shop.delivery.ShopDeliveryService;
import miroshka.allayshop.shop.economy.EconomyApiService;
import miroshka.allayshop.shop.offer.SpecialOfferService;
import miroshka.allayshop.shop.purchase.PurchaseService;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;
import org.slf4j.Logger;

public final class AllayShopPlugin extends Plugin {
    private ShopConfig config;
    private SpecialOfferService specialOfferService;

    @Override
    public void onEnable() {
        this.config = ShopConfig.loadConfig(getPluginContainer().dataFolder());
        var catalog = new ShopCatalog(config);
        var deliveryService = new ShopDeliveryService(Server.getInstance());
        var purchaseService = new PurchaseService(new EconomyApiService(config.getCurrency()), deliveryService);
        this.specialOfferService = new SpecialOfferService(catalog, config.getSpecialOffer(), getPluginLogger());
        this.specialOfferService.refresh();

        var formService = new ShopFormService(config, catalog, specialOfferService, purchaseService);
        Registries.COMMANDS.register(new ShopCommand(formService));

        Server.getInstance().getScheduler().scheduleRepeating(
                this,
                specialOfferService::refreshIfExpired,
                Math.max(20, config.getSpecialOffer().getRefreshCheckSeconds() * 20)
        );
        getPluginLogger().info("AllayShop enabled with {} categories", config.getCategories().size());
    }

    @Override
    public void onDisable() {
        logger().info("AllayShop disabled");
    }

    public ShopConfig getConfig() {
        return config;
    }

    private Logger logger() {
        return getPluginLogger();
    }
}
