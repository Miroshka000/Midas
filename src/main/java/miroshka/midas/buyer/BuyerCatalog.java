package miroshka.midas.buyer;

import miroshka.midas.buyer.config.BuyerConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class BuyerCatalog {
    private final BuyerConfig config;

    public BuyerCatalog(BuyerConfig config) {
        this.config = config;
    }

    public List<BuyerCategory> categories() {
        return config.getCategories();
    }

    public List<BuyerProduct> products() {
        return config.getCategories()
                .stream()
                .flatMap(category -> category.getProducts().stream())
                .toList();
    }

    public Optional<BuyerProduct> findProduct(String productId) {
        return products()
                .stream()
                .filter(product -> product.getId().equalsIgnoreCase(productId))
                .findFirst();
    }

    public List<BuyerProduct> regularOfferCandidates() {
        return new ArrayList<>(products()
                .stream()
                .filter(product -> product.getPrice() > 0)
                .toList());
    }

    public Optional<BuyerProduct> bestMatch(org.allaymc.api.item.ItemStack itemStack) {
        return products()
                .stream()
                .filter(product -> BuyerItemMatcher.matches(product, itemStack))
                .max(Comparator.comparingLong(BuyerProduct::getPrice));
    }
}
