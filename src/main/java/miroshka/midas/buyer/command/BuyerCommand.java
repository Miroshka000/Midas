package miroshka.midas.buyer.command;

import miroshka.midas.buyer.form.BuyerFormService;
import miroshka.midas.i18n.MessageKey;
import miroshka.midas.i18n.MessageService;
import org.allaymc.api.command.Command;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.utils.TextFormat;

public final class BuyerCommand extends Command {
    private final BuyerFormService formService;
    private final MessageService messages;

    public BuyerCommand(BuyerFormService formService, MessageService messages) {
        super("buyer", messages.tr(MessageKey.BUYER_COMMAND_DESCRIPTION), "midas.command.buyer");
        this.formService = formService;
        this.messages = messages;
        this.aliases.add("sell");
    }

    @Override
    public void prepareCommandTree(CommandTree tree) {
        tree.getRoot()
                .key("open")
                .optional()
                .exec(context -> {
                    if (!context.getSender().isPlayer() || !context.getSender().asPlayer().isActualPlayer()) {
                        context.getSender().sendMessage(TextFormat.RED + messages.tr(MessageKey.COMMAND_PLAYER_ONLY));
                        return context.fail();
                    }

                    formService.openMain(context.getSender().asPlayer());
                    return context.success();
                })
                .root()
                .key("special")
                .exec(context -> {
                    if (!context.getSender().isPlayer() || !context.getSender().asPlayer().isActualPlayer()) {
                        context.getSender().sendMessage(TextFormat.RED + messages.tr(MessageKey.COMMAND_PLAYER_ONLY));
                        return context.fail();
                    }

                    formService.openSpecialOffer(context.getSender().asPlayer());
                    return context.success();
                });
    }
}
