package miroshka.midas.shop.economy;

import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.economyapi.EconomyAPI;

import java.math.BigDecimal;

public final class EconomyApiService implements EconomyService {
    private final String currency;

    public EconomyApiService(String currency) {
        this.currency = currency;
    }

    @Override
    public boolean withdraw(EntityPlayer player, long amount) {
        var api = EconomyAPI.API.get();
        if (api == null) {
            return false;
        }
        var account = api.getOrCreateAccount(player.getUniqueId());
        var currencyObject = api.getCurrency(currency);
        if (account == null || currencyObject == null) {
            return false;
        }
        var price = BigDecimal.valueOf(amount);
        if (account.getBalance(currencyObject).compareTo(price) < 0) {
            return false;
        }
        return account.setBalance(currencyObject, account.getBalance(currencyObject).subtract(price));
    }

    @Override
    public boolean deposit(EntityPlayer player, long amount) {
        var api = EconomyAPI.API.get();
        if (api == null || amount < 0) {
            return false;
        }
        var account = api.getOrCreateAccount(player.getUniqueId());
        var currencyObject = api.getCurrency(currency);
        if (account == null || currencyObject == null) {
            return false;
        }
        return account.setBalance(currencyObject, account.getBalance(currencyObject).add(BigDecimal.valueOf(amount)));
    }
}
