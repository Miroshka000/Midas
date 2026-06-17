package miroshka.midas.command;

import miroshka.midas.form.ShopFormService;
import miroshka.midas.i18n.MessageKey;
import miroshka.midas.i18n.MessageService;
import org.allaymc.api.command.Command;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.utils.TextFormat;

public final class ShopCommand extends Command {
    private final ShopFormService formService;
    private final MessageService messages;

    public ShopCommand(ShopFormService formService, MessageService messages) {
        super("shop", messages.tr(MessageKey.SHOP_COMMAND_DESCRIPTION), "midas.command.shop");
        this.formService = formService;
        this.messages = messages;
        this.aliases.add("market");
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
