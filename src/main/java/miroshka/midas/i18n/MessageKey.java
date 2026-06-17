package miroshka.midas.i18n;

public enum MessageKey {
    COMMAND_PLAYER_ONLY("midas:command.player_only"),

    SHOP_COMMAND_DESCRIPTION("midas:command.shop.description"),
    SHOP_MAIN_CONTENT("midas:shop.main.content"),
    SHOP_SPECIAL_OFFER_BUTTON("midas:shop.special.button"),
    SHOP_SPECIAL_OFFER_TITLE("midas:shop.special.title"),
    SHOP_SPECIAL_OFFER_CONTENT("midas:shop.special.content"),
    AMOUNT_SLIDER_LABEL("midas:common.amount_slider"),
    PRODUCT_PRICE_LINE("midas:common.product_price_line"),
    PRODUCT_PRICE_DISCOUNT_LINE("midas:shop.product_price_discount_line"),

    PURCHASE_INVALID_AMOUNT("midas:purchase.invalid_amount"),
    PURCHASE_NOT_ENOUGH_MONEY("midas:purchase.not_enough_money"),
    PURCHASE_SUCCESS("midas:purchase.success"),

    BUYER_COMMAND_DESCRIPTION("midas:command.buyer.description"),
    BUYER_MAIN_CONTENT("midas:buyer.main.content"),
    BUYER_SPECIAL_OFFER_BUTTON("midas:buyer.special.button"),
    BUYER_SPECIAL_OFFER_TITLE("midas:buyer.special.title"),
    BUYER_SPECIAL_OFFER_CONTENT("midas:buyer.special.content"),
    BUYER_PRICE_BOOST_LINE("midas:buyer.price_boost_line"),
    BUYER_REMAINING_TIME("midas:buyer.remaining_time"),

    SELL_INVALID_AMOUNT("midas:sell.invalid_amount"),
    SELL_BELOW_MINIMUM("midas:sell.below_minimum"),
    SELL_REJECTED("midas:sell.rejected"),
    SELL_DEPOSIT_FAILED("midas:sell.deposit_failed"),
    SELL_SUCCESS("midas:sell.success"),

    AUTO_BUYER_MENU_TITLE("midas:autobuyer.menu_title"),
    AUTO_BUYER_SET_DESCRIPTION("midas:command.autobuyer.set.description"),
    AUTO_BUYER_DELETE_DESCRIPTION("midas:command.autobuyer.delete.description"),
    AUTO_BUYER_SELECT_BIND("midas:autobuyer.select_bind"),
    AUTO_BUYER_SELECT_DELETE("midas:autobuyer.select_delete"),
    AUTO_BUYER_DISABLED("midas:autobuyer.disabled"),
    AUTO_BUYER_BOUND("midas:autobuyer.bound"),
    AUTO_BUYER_DELETED("midas:autobuyer.deleted"),
    AUTO_BUYER_NOT_BOUND("midas:autobuyer.not_bound"),
    AUTO_BUYER_NOT_CONTAINER("midas:autobuyer.not_container"),
    AUTO_BUYER_DEPOSIT_FAILED("midas:autobuyer.deposit_failed"),
    AUTO_BUYER_SOLD("midas:autobuyer.sold");

    private final String translationKey;

    MessageKey(String translationKey) {
        this.translationKey = translationKey;
    }

    public String translationKey() {
        return translationKey;
    }
}
