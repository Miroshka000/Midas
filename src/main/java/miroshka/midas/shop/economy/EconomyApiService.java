package miroshka.midas.shop.economy;

import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.economyapi.EconomyAPI;

import java.math.BigDecimal;
import java.util.Map;

public final class EconomyApiService implements EconomyService {
    @Override
    public boolean withdraw(EntityPlayer player, String currency, long amount) {
        var api = EconomyAPI.API.get();
        if (api == null || amount < 0 || currency == null || currency.isBlank()) {
            return false;
        }
        var account = api.getOrCreateAccount(player.getUniqueId());
        var currencyObject = api.getCurrency(currency.trim());
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
    public boolean deposit(EntityPlayer player, String currency, long amount) {
        var api = EconomyAPI.API.get();
        if (api == null || amount < 0 || currency == null || currency.isBlank()) {
            return false;
        }
        var account = api.getOrCreateAccount(player.getUniqueId());
        var currencyObject = api.getCurrency(currency.trim());
        if (account == null || currencyObject == null) {
            return false;
        }
        return account.setBalance(currencyObject, account.getBalance(currencyObject).add(BigDecimal.valueOf(amount)));
    }

    @Override
    public boolean depositAll(EntityPlayer player, Map<String, Long> amountsByCurrency) {
        var api = EconomyAPI.API.get();
        if (api == null || amountsByCurrency == null || amountsByCurrency.isEmpty()) {
            return false;
        }
        var account = api.getOrCreateAccount(player.getUniqueId());
        if (account == null) {
            return false;
        }
        for (var entry : amountsByCurrency.entrySet()) {
            if (entry.getValue() == null || entry.getValue() < 0 || entry.getKey() == null || entry.getKey().isBlank()) {
                return false;
            }
            if (entry.getValue() == 0) {
                continue;
            }
            if (api.getCurrency(entry.getKey().trim()) == null) {
                return false;
            }
        }
        for (var entry : amountsByCurrency.entrySet()) {
            if (entry.getValue() == 0) {
                continue;
            }
            var currencyObject = api.getCurrency(entry.getKey().trim());
            var balance = account.getBalance(currencyObject);
            if (!account.setBalance(currencyObject, balance.add(BigDecimal.valueOf(entry.getValue())))) {
                return false;
            }
        }
        return true;
    }
}
