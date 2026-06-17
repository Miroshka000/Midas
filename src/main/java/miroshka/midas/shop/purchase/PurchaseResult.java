package miroshka.midas.shop.purchase;

public record PurchaseResult(
        boolean success,
        String message
) {
    public static PurchaseResult success(String message) {
        return new PurchaseResult(true, message);
    }

    public static PurchaseResult fail(String message) {
        return new PurchaseResult(false, message);
    }
}
