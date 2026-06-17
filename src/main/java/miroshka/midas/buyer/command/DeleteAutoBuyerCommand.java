package miroshka.midas.buyer.command;

import miroshka.midas.buyer.autobuyer.AutoBuyerTerminalService;
import miroshka.midas.i18n.MessageKey;
import miroshka.midas.i18n.MessageService;
import org.allaymc.api.command.Command;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.utils.TextFormat;

public final class DeleteAutoBuyerCommand extends Command {
    private final AutoBuyerTerminalService terminalService;
    private final MessageService messages;

    public DeleteAutoBuyerCommand(AutoBuyerTerminalService terminalService, MessageService messages) {
        super("delautobuyer", messages.tr(MessageKey.AUTO_BUYER_DELETE_DESCRIPTION), "midas.command.autobuyer.delete");
        this.terminalService = terminalService;
        this.messages = messages;
    }

    @Override
    public void prepareCommandTree(CommandTree tree) {
        tree.getRoot()
                .exec(context -> {
                    if (!context.getSender().isPlayer() || !context.getSender().asPlayer().isActualPlayer()) {
                        context.getSender().sendMessage(TextFormat.RED + messages.tr(MessageKey.COMMAND_PLAYER_ONLY));
                        return context.fail();
                    }

                    terminalService.startDeleteSelection(context.getSender().asPlayer());
                    if (terminalService.isSelecting(context.getSender().asPlayer())) {
                        context.getSender().sendMessage(messages.tr(context.getSender().asPlayer(), MessageKey.AUTO_BUYER_SELECT_DELETE));
                    } else {
                        context.getSender().sendMessage(messages.tr(context.getSender().asPlayer(), MessageKey.AUTO_BUYER_DISABLED));
                    }
                    return context.success();
                });
    }
}
