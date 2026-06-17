package miroshka.midas.buyer.offer;

import miroshka.midas.buyer.BuyerCatalog;
import miroshka.midas.buyer.BuyerPriceCalculator;
import miroshka.midas.buyer.BuyerProduct;
import miroshka.midas.i18n.MessageKey;
import miroshka.midas.i18n.MessageService;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class BuyerOfferService {
    private final BuyerCatalog catalog;
    private final BuyerSpecialOfferSettings settings;
    private final Logger logger;
    private final MessageService messages;
    private List<BuyerOfferItem> currentItems = List.of();
    private Instant expiresAt = Instant.EPOCH;

    public BuyerOfferService(BuyerCatalog catalog, BuyerSpecialOfferSettings settings, Logger logger, MessageService messages) {
        this.catalog = catalog;
        this.settings = settings;
        this.logger = logger;
        this.messages = messages;
    }

    public List<BuyerOfferItem> currentItems() {
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

        var items = new ArrayList<BuyerOfferItem>();
        var regular = catalog.regularOfferCandidates();
        Collections.shuffle(regular);
        regular.stream()
                .limit(Math.max(0, settings.getRegularProductCount()))
                .map(this::boosted)
                .forEach(items::add);

        var specialProducts = new ArrayList<>(settings.getSpecialProducts());
        Collections.shuffle(specialProducts);
        specialProducts.stream()
                .findFirst()
                .map(product -> new BuyerOfferItem(product, 1.0, product.getPrice(), true))
                .ifPresent(items::add);

        currentItems = List.copyOf(items);
        expiresAt = Instant.now().plus(Duration.ofHours(Math.max(1, settings.getRefreshHours())));
        logger.info("Buyer special offer refreshed: {} items, expires at {}", currentItems.size(), expiresAt);
    }

    public String remainingTimeText() {
        var seconds = Math.max(0, Duration.between(Instant.now(), expiresAt).toSeconds());
        var hours = seconds / 3600;
        var minutes = (seconds % 3600) / 60;
        return messages.tr(MessageKey.BUYER_REMAINING_TIME, Map.of(
                "hours", hours,
                "minutes", minutes
        ));
    }

    private BuyerOfferItem boosted(BuyerProduct product) {
        double min = settings.getMultiplierMin();
        double max = settings.getMultiplierMax();
        if (min > max) {
            throw new IllegalStateException("multiplierMin must not be greater than multiplierMax");
        }
        double multiplier = ThreadLocalRandom.current().nextDouble(min, max + 0.000001);
        long price = BuyerPriceCalculator.applyMultiplier(product.getPrice(), multiplier);
        return new BuyerOfferItem(product, multiplier, price, false);
    }
}
