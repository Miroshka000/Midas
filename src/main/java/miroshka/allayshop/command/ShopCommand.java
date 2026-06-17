package miroshka.allayshop.command;

import miroshka.allayshop.form.ShopFormService;
import org.allaymc.api.command.Command;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.utils.TextFormat;

public final class ShopCommand extends Command {
    private final ShopFormService formService;

    public ShopCommand(ShopFormService formService) {
        super("shop", "Открыть магазин", "allayshop.command.shop");
        this.formService = formService;
        this.aliases.add("market");
    }

    @Override
    public void prepareCommandTree(CommandTree tree) {
        tree.getRoot()
                .key("open")
                .optional()
                .exec(context -> {
                    if (!context.getSender().isPlayer() || !context.getSender().asPlayer().isActualPlayer()) {
                        context.getSender().sendMessage(TextFormat.RED + "Команда доступна только игроку.");
                        return context.fail();
                    }

                    formService.openMain(context.getSender().asPlayer());
                    return context.success();
                })
                .root()
                .key("special")
                .exec(context -> {
                    if (!context.getSender().isPlayer() || !context.getSender().asPlayer().isActualPlayer()) {
                        context.getSender().sendMessage(TextFormat.RED + "Команда доступна только игроку.");
                        return context.fail();
                    }

                    formService.openSpecialOffer(context.getSender().asPlayer());
                    return context.success();
                });
    }
}
