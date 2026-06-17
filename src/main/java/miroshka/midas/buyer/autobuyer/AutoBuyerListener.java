package miroshka.midas.buyer.autobuyer;

import miroshka.midas.i18n.MessageKey;
import miroshka.midas.i18n.MessageService;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.eventbus.EventHandler;
import org.allaymc.api.eventbus.event.player.PlayerInteractBlockEvent;

public final class AutoBuyerListener {
    private final AutoBuyerTerminalService terminalService;
    private final AutoBuyerMenuService menuService;
    private final MessageService messages;

    public AutoBuyerListener(AutoBuyerTerminalService terminalService, AutoBuyerMenuService menuService, MessageService messages) {
        this.terminalService = terminalService;
        this.menuService = menuService;
        this.messages = messages;
    }

    @EventHandler
    public void onInteract(PlayerInteractBlockEvent event) {
        EntityPlayer player = event.getPlayer();
        if (!player.isActualPlayer()) {
            return;
        }
        var block = event.getInteractInfo().getClickedBlock();
        if (terminalService.isSelecting(player)) {
            event.setCancelled(true);
            switch (terminalService.applySelection(player, block)) {
                case BOUND -> messages.send(player, MessageKey.AUTO_BUYER_BOUND);
                case DELETED -> messages.send(player, MessageKey.AUTO_BUYER_DELETED);
                case NOT_BOUND -> messages.send(player, MessageKey.AUTO_BUYER_NOT_BOUND);
                case NOT_CONTAINER -> messages.send(player, MessageKey.AUTO_BUYER_NOT_CONTAINER);
                case NONE -> {
                }
            }
            return;
        }
        if (terminalService.isTerminal(block)) {
            event.setCancelled(true);
            menuService.open(player);
        }
    }
}
