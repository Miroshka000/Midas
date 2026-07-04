package miroshka.midas.form;

import miroshka.midas.config.ShopConfig;
import miroshka.midas.i18n.MessageKey;
import miroshka.midas.i18n.MessageService;
import miroshka.midas.shop.ShopCatalog;
import miroshka.midas.shop.ShopCategory;
import miroshka.midas.shop.ShopPriceCalculator;
import miroshka.midas.shop.ShopProduct;
import miroshka.midas.shop.offer.SpecialOfferItem;
import miroshka.midas.shop.offer.SpecialOfferService;
import miroshka.midas.shop.purchase.PurchaseRequest;
import miroshka.midas.shop.purchase.PurchaseService;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.form.Forms;
import org.allaymc.api.form.element.ImageData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ShopFormService {
    private final ShopConfig config;
    private final ShopCatalog catalog;
    private final SpecialOfferService specialOfferService;
    private final PurchaseService purchaseService;
    private final MessageService messages;

    public ShopFormService(
            ShopConfig config,
            ShopCatalog catalog,
            SpecialOfferService specialOfferService,
            PurchaseService purchaseService,
            MessageService messages
    ) {
        this.config = config;
        this.catalog = catalog;
        this.specialOfferService = specialOfferService;
        this.purchaseService = purchaseService;
        this.messages = messages;
    }

    public void openMain(EntityPlayer player) {
        var form = Forms.simple()
                .title(config.getTitle())
                .content(messages.tr(player, MessageKey.SHOP_MAIN_CONTENT));

        form.button(messages.tr(player, MessageKey.SHOP_SPECIAL_OFFER_BUTTON)).onClick(button -> openSpecialOffer(player));
        for (ShopCategory category : catalog.categories()) {
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
        var form = Forms.simple()
                .title(messages.tr(player, MessageKey.SHOP_SPECIAL_OFFER_TITLE))
                .content(messages.tr(player, MessageKey.SHOP_SPECIAL_OFFER_CONTENT, Map.of(
                        "hours", config.getSpecialOffer().getRefreshHours()
                )));
        for (SpecialOfferItem offer : specialOfferService.currentItems()) {
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
                .map(amount -> priceLine(product, amount, priceFor(product, amount, basePrice), 0))
                .toList();
        Forms.custom()
                .title(product.getName())
                .label(product.getDescription())
                .stepSlider(messages.tr(player, MessageKey.AMOUNT_SLIDER_LABEL), labels)
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
        return product.getName() + "\n" + priceLine(product, product.getMinAmount(), price, discountPercent);
    }

    private String priceLine(ShopProduct product, int amount, long price, int discountPercent) {
        if (discountPercent > 0) {
            return messages.tr(MessageKey.PRODUCT_PRICE_DISCOUNT_LINE, Map.of(
                    "amount", amount,
                    "price", price,
                    "currency", product.getCurrency(),
                    "discount", discountPercent
            ));
        }
        return messages.tr(MessageKey.PRODUCT_PRICE_LINE, Map.of(
                "amount", amount,
                "price", price,
                "currency", product.getCurrency()
        ));
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
