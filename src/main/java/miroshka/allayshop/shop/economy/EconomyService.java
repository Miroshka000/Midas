package miroshka.allayshop.shop.economy;

import org.allaymc.api.entity.interfaces.EntityPlayer;

public interface EconomyService {
    boolean withdraw(EntityPlayer player, long amount);
}
