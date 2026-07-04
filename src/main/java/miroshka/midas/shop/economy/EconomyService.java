package miroshka.midas.shop.economy;

import org.allaymc.api.entity.interfaces.EntityPlayer;

import java.util.Map;

public interface EconomyService {
    boolean withdraw(EntityPlayer player, String currency, long amount);

    boolean deposit(EntityPlayer player, String currency, long amount);

    boolean depositAll(EntityPlayer player, Map<String, Long> amountsByCurrency);
}
