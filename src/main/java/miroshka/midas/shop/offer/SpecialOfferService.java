package miroshka.midas.shop.offer;

import miroshka.midas.shop.ShopCatalog;
import miroshka.midas.shop.ShopPriceCalculator;
import miroshka.midas.shop.ShopProduct;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class SpecialOfferService {
    private final ShopCatalog catalog;
    private final SpecialOfferSettings settings;
    private final Logger logger;
    private List<SpecialOfferItem> currentItems = List.of();
    private Instant expiresAt = Instant.EPOCH;

    public SpecialOfferService(ShopCatalog catalog, SpecialOfferSettings settings, Logger logger) {
        this.catalog = catalog;
        this.settings = settings;
        this.logger = logger;
    }

    public List<SpecialOfferItem> currentItems() {
        if (settings.isEnabled() && Instant.now().isAfter(expiresAt)) {
            refresh();
        }
        return currentItems;
    }

    public void refreshIfExpired() {
        if (settings.isEnabled() && Instant.now().isAfter(expiresAt)) {
            refresh();
        }
    }

    public void refresh() {
        if (!settings.isEnabled()) {
            currentItems = List.of();
            return;
        }

        var items = new ArrayList<SpecialOfferItem>();
        var regular = catalog.regularOfferCandidates();
        Collections.shuffle(regular);
        regular.stream()
                .limit(Math.max(0, settings.getRegularProductCount()))
                .map(this::discounted)
                .forEach(items::add);

        var specialProducts = new ArrayList<>(settings.getSpecialProducts());
        Collections.shuffle(specialProducts);
        specialProducts.stream()
                .findFirst()
                .map(product -> new SpecialOfferItem(product, 0, product.getPrice(), true))
                .ifPresent(items::add);

        currentItems = List.copyOf(items);
        expiresAt = Instant.now().plus(Duration.ofHours(Math.max(1, settings.getRefreshHours())));
        logger.info("Special offer refreshed: {} items, expires at {}", currentItems.size(), expiresAt);
    }

    private SpecialOfferItem discounted(ShopProduct product) {
        int min = settings.getDiscountMinPercent();
        int max = settings.getDiscountMaxPercent();
        if (min > max) {
            throw new IllegalStateException("discountMinPercent must not be greater than discountMaxPercent");
        }
        int discount = ThreadLocalRandom.current().nextInt(min, max + 1);
        long price = ShopPriceCalculator.applyDiscount(product.getPrice(), discount);
        return new SpecialOfferItem(product, discount, price, false);
    }
}
