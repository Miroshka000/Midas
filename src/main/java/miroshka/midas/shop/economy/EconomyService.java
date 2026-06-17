package miroshka.midas.shop.economy;

import org.allaymc.api.entity.interfaces.EntityPlayer;

public interface EconomyService {
    boolean withdraw(EntityPlayer player, long amount);

    boolean deposit(EntityPlayer player, long amount);
}
