package miroshka.midas.buyer.sell;

public record SellResult(
        boolean success,
        String message
) {
    public static SellResult success(String message) {
        return new SellResult(true, message);
    }

    public static SellResult fail(String message) {
        return new SellResult(false, message);
    }
}
