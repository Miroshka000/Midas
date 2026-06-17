package miroshka.allayshop.shop;

import miroshka.allayshop.config.ShopConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ShopCatalog {
    private final ShopConfig config;

    public ShopCatalog(ShopConfig config) {
        this.config = config;
    }

    public List<ShopCategory> categories() {
        return config.getCategories();
    }

    public List<ShopProduct> products() {
        return config.getCategories()
                .stream()
                .flatMap(category -> category.getProducts().stream())
                .toList();
    }

    public Optional<ShopProduct> findProduct(String productId) {
        return products()
                .stream()
                .filter(product -> product.getId().equalsIgnoreCase(productId))
                .findFirst();
    }

    public List<ShopProduct> regularOfferCandidates() {
        return new ArrayList<>(products()
                .stream()
                .filter(product -> product.getPrice() > 0)
                .toList());
    }
}
