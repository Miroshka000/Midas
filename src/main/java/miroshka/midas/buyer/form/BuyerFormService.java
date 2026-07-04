package miroshka.midas.buyer.form;

import miroshka.midas.buyer.BuyerCatalog;
import miroshka.midas.buyer.BuyerCategory;
import miroshka.midas.buyer.BuyerPriceCalculator;
import miroshka.midas.buyer.BuyerProduct;
import miroshka.midas.buyer.config.BuyerConfig;
import miroshka.midas.buyer.offer.BuyerOfferItem;
import miroshka.midas.buyer.offer.BuyerOfferService;
import miroshka.midas.buyer.sell.SellRequest;
import miroshka.midas.buyer.sell.SellService;
import miroshka.midas.i18n.MessageKey;
import miroshka.midas.i18n.MessageService;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.form.Forms;
import org.allaymc.api.form.element.ImageData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class BuyerFormService {
    private final BuyerConfig config;
    private final BuyerCatalog catalog;
    private final BuyerOfferService offerService;
    private final SellService sellService;
    private final MessageService messages;

    public BuyerFormService(
            BuyerConfig config,
            BuyerCatalog catalog,
            BuyerOfferService offerService,
            SellService sellService,
            MessageService messages
    ) {
        this.config = config;
        this.catalog = catalog;
        this.offerService = offerService;
        this.sellService = sellService;
        this.messages = messages;
    }

    public void openMain(EntityPlayer player) {
        var form = Forms.simple()
                .title(config.getTitle())
                .content(messages.tr(player, MessageKey.BUYER_MAIN_CONTENT));

        form.button(messages.tr(player, MessageKey.BUYER_SPECIAL_OFFER_BUTTON, Map.of(
                "time", offerService.remainingTimeText()
        ))).onClick(button -> openSpecialOffer(player));
        for (BuyerCategory category : catalog.categories()) {
            addCategoryButton(form, category).onClick(button -> openCategory(player, category));
        }
        form.sendTo(player.getController());
    }

    public void openSpecialOffer(EntityPlayer player) {
        var form = Forms.simple()
                .title(messages.tr(player, MessageKey.BUYER_SPECIAL_OFFER_TITLE))
                .content(messages.tr(player, MessageKey.BUYER_SPECIAL_OFFER_CONTENT, Map.of(
                        "time", offerService.remainingTimeText()
                )));
        for (BuyerOfferItem offer : offerService.currentItems()) {
            form.button(productButtonText(offer.product(), offer.offerPrice(), offer.multiplier()))
                    .onClick(button -> openProduct(player, offer.product(), offer.offerPrice()));
        }
        form.sendTo(player.getController());
    }

    private org.allaymc.api.form.element.Button addCategoryButton(org.allaymc.api.form.type.SimpleForm form, BuyerCategory category) {
        if (category.getImage() == null || category.getImage().isBlank()) {
            return form.button(category.getName());
        }
        var imageType = "url".equalsIgnoreCase(category.getImageType())
                ? ImageData.ImageType.URL
                : ImageData.ImageType.PATH;
        return form.button(category.getName(), imageType, category.getImage());
    }

    private void openCategory(EntityPlayer player, BuyerCategory category) {
        var form = Forms.simple()
                .title(category.getName())
                .content(category.getDescription());
        for (BuyerProduct product : category.getProducts()) {
            form.button(productButtonText(product, product.getPrice(), 1.0)).onClick(button -> openProduct(player, product, product.getPrice()));
        }
        form.sendTo(player.getController());
    }

    private void openProduct(EntityPlayer player, BuyerProduct product, long basePrice) {
        if (!product.isAmountScrollEnabled()) {
            confirmSell(player, product, product.getMinAmount(), basePrice);
            return;
        }

        var amounts = amounts(product);
        var labels = amounts.stream()
                .map(amount -> priceLine(product, amount, priceFor(product, amount, basePrice), 1.0))
                .toList();
        Forms.custom()
                .title(product.getName())
                .label(product.getDescription())
                .stepSlider(messages.tr(player, MessageKey.AMOUNT_SLIDER_LABEL), labels)
                .onResponse(response -> {
                    int selected = Integer.parseInt(response.get(1));
                    int amount = amounts.get(selected);
                    confirmSell(player, product, amount, priceFor(product, amount, basePrice));
                })
                .sendTo(player.getController());
    }

    private void confirmSell(EntityPlayer player, BuyerProduct product, int amount, long price) {
        var result = sellService.sell(player, new SellRequest(product, amount, price));
        player.sendMessage(result.message());
    }

    private String productButtonText(BuyerProduct product, long price, double multiplier) {
        return product.getName() + "\n" + priceLine(product, product.getMinAmount(), price, multiplier);
    }

    private String priceLine(BuyerProduct product, int amount, long price, double multiplier) {
        if (multiplier > 1.0) {
            return messages.tr(MessageKey.BUYER_PRICE_BOOST_LINE, Map.of(
                    "amount", amount,
                    "price", price,
                    "currency", product.getCurrency(),
                    "multiplier", String.format(java.util.Locale.US, "%.2f", multiplier)
            ));
        }
        return messages.tr(MessageKey.PRODUCT_PRICE_LINE, Map.of(
                "amount", amount,
                "price", price,
                "currency", product.getCurrency()
        ));
    }

    private long priceFor(BuyerProduct product, int amount, long basePrice) {
        var copy = new BuyerProduct();
        copy.setId(product.getId());
        copy.setMinAmount(product.getMinAmount());
        copy.setPrice(basePrice);
        return BuyerPriceCalculator.calculate(copy, amount);
    }

    private List<Integer> amounts(BuyerProduct product) {
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
