package miroshka.allayshop.form;

import miroshka.allayshop.config.ShopConfig;
import miroshka.allayshop.shop.ShopCatalog;
import miroshka.allayshop.shop.ShopCategory;
import miroshka.allayshop.shop.ShopPriceCalculator;
import miroshka.allayshop.shop.ShopProduct;
import miroshka.allayshop.shop.offer.SpecialOfferItem;
import miroshka.allayshop.shop.offer.SpecialOfferService;
import miroshka.allayshop.shop.purchase.PurchaseRequest;
import miroshka.allayshop.shop.purchase.PurchaseService;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.form.Forms;
import org.allaymc.api.form.element.ImageData;

import java.util.ArrayList;
import java.util.List;

public final class ShopFormService {
    private final ShopConfig config;
    private final ShopCatalog catalog;
    private final SpecialOfferService specialOfferService;
    private final PurchaseService purchaseService;

    public ShopFormService(
            ShopConfig config,
            ShopCatalog catalog,
            SpecialOfferService specialOfferService,
            PurchaseService purchaseService
    ) {
        this.config = config;
        this.catalog = catalog;
        this.specialOfferService = specialOfferService;
        this.purchaseService = purchaseService;
    }

    public void openMain(EntityPlayer player) {
        var categories = catalog.categories();
        var form = Forms.simple()
                .title(config.getTitle())
                .content("Выберите категорию");

        form.button("Особое предложение").onClick(button -> openSpecialOffer(player));
        for (ShopCategory category : categories) {
            addCategoryButton(form, category).onClick(button -> openCategory(player, category));
        }
        form.sendTo(player.getController());
    }

    private org.allaymc.api.form.element.Button addCategoryButton(org.allaymc.api.form.type.SimpleForm form, ShopCategory category) {
        if (category.getImage() == null || category.getImage().isBlank()) {
            return form.button(category.getName());
        }
        var imageType = "url".equalsIgnoreCase(category.getImageType())
                ? ImageData.ImageType.URL
                : ImageData.ImageType.PATH;
        return form.button(category.getName(), imageType, category.getImage());
    }

    private void openCategory(EntityPlayer player, ShopCategory category) {
        var form = Forms.simple()
                .title(category.getName())
                .content(category.getDescription());
        for (ShopProduct product : category.getProducts()) {
            form.button(productButtonText(product, product.getPrice(), 0)).onClick(button -> openProduct(player, product, product.getPrice()));
        }
        form.sendTo(player.getController());
    }

    public void openSpecialOffer(EntityPlayer player) {
        var offers = specialOfferService.currentItems();
        var form = Forms.simple()
                .title("Особое предложение")
                .content("Обновляется каждые " + config.getSpecialOffer().getRefreshHours() + " часов");
        for (SpecialOfferItem offer : offers) {
            form.button(productButtonText(offer.product(), offer.offerPrice(), offer.discountPercent()))
                    .onClick(button -> openProduct(player, offer.product(), offer.offerPrice()));
        }
        form.sendTo(player.getController());
    }

    private void openProduct(EntityPlayer player, ShopProduct product, long basePrice) {
        if (!product.isAmountScrollEnabled()) {
            confirmPurchase(player, product, product.getMinAmount(), basePrice);
            return;
        }

        var amounts = amounts(product);
        var labels = amounts.stream()
                .map(amount -> amount + " шт. - " + priceFor(product, amount, basePrice))
                .toList();
        Forms.custom()
                .title(product.getName())
                .label(product.getDescription())
                .stepSlider("Количество", labels)
                .onResponse(response -> {
                    int selected = Integer.parseInt(response.get(1));
                    int amount = amounts.get(selected);
                    confirmPurchase(player, product, amount, priceFor(product, amount, basePrice));
                })
                .sendTo(player.getController());
    }

    private void confirmPurchase(EntityPlayer player, ShopProduct product, int amount, long price) {
        var result = purchaseService.purchase(player, new PurchaseRequest(player.getDisplayName(), product, amount, price));
        player.sendMessage(result.message());
    }

    private String productButtonText(ShopProduct product, long price, int discountPercent) {
        var discount = discountPercent > 0 ? " (-" + discountPercent + "%)" : "";
        return product.getName() + "\n" + product.getMinAmount() + " шт. - " + price + discount;
    }

    private long priceFor(ShopProduct product, int amount, long basePrice) {
        var copy = new ShopProduct();
        copy.setId(product.getId());
        copy.setMinAmount(product.getMinAmount());
        copy.setPrice(basePrice);
        return ShopPriceCalculator.calculate(copy, amount);
    }

    private List<Integer> amounts(ShopProduct product) {
        var amounts = new ArrayList<Integer>();
        int min = Math.max(1, product.getMinAmount());
        int max = Math.max(min, product.getMaxAmount());
        for (int amount = min; amount <= max; amount += min) {
            amounts.add(amount);
        }
        if (amounts.isEmpty()) {
            amounts.add(min);
        }
        return amounts;
    }
}
