package miroshka.midas.buyer.autobuyer;

import miroshka.midas.buyer.config.BuyerConfig;
import org.allaymc.api.block.dto.Block;
import org.allaymc.api.block.type.BlockType;
import org.allaymc.api.container.ContainerTypes;
import org.allaymc.api.container.interfaces.BlockContainer;
import org.allaymc.api.entity.interfaces.EntityPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AutoBuyerTerminalService {
    private final BuyerConfig config;
    private final Map<UUID, SelectionMode> pendingSelection = new HashMap<>();

    public AutoBuyerTerminalService(BuyerConfig config) {
        this.config = config;
    }

    public void startSelection(EntityPlayer player) {
        if (!config.getAutoBuyer().isEnabled()) {
            return;
        }
        pendingSelection.put(player.getUniqueId(), SelectionMode.BIND);
    }

    public void startDeleteSelection(EntityPlayer player) {
        if (!config.getAutoBuyer().isEnabled()) {
            return;
        }
        pendingSelection.put(player.getUniqueId(), SelectionMode.DELETE);
    }

    public boolean isSelecting(EntityPlayer player) {
        return pendingSelection.containsKey(player.getUniqueId());
    }

    public SelectionResult applySelection(EntityPlayer player, Block block) {
        var mode = pendingSelection.remove(player.getUniqueId());
        if (mode == null) {
            return SelectionResult.NONE;
        }
        if (!isSupportedContainer(block)) {
            return SelectionResult.NOT_CONTAINER;
        }
        if (mode == SelectionMode.DELETE) {
            return delete(BlockKey.from(block)) ? SelectionResult.DELETED : SelectionResult.NOT_BOUND;
        }
        bind(BlockKey.from(block));
        return SelectionResult.BOUND;
    }

    private void bind(BlockKey key) {
        if (!isTerminal(key)) {
            config.getAutoBuyer().getTerminals().add(key.toTerminal());
            config.save();
        }
    }

    private boolean delete(BlockKey key) {
        var removed = config.getAutoBuyer().getTerminals().removeIf(terminal -> same(key, terminal));
        if (removed) {
            config.save();
        }
        return removed;
    }

    public boolean isTerminal(Block block) {
        return config.getAutoBuyer().isEnabled() && isSupportedContainer(block) && isTerminal(BlockKey.from(block));
    }

    private boolean isTerminal(BlockKey key) {
        return config.getAutoBuyer().getTerminals()
                .stream()
                .anyMatch(terminal -> same(key, terminal));
    }

    private boolean same(BlockKey key, AutoBuyerTerminal terminal) {
        return key.world().equals(terminal.getWorld())
                && key.dimensionId() == terminal.getDimensionId()
                && key.x() == terminal.getX()
                && key.y() == terminal.getY()
                && key.z() == terminal.getZ();
    }

    private boolean isSupportedContainer(Block block) {
        var blockEntity = block.getBlockEntity();
        if (!(blockEntity instanceof org.allaymc.api.blockentity.component.BlockEntityContainerHolderComponent holder)) {
            return isContainerBlock(block.getBlockType());
        }
        var container = holder.getContainer();
        if (container == null) {
            return false;
        }
        return container instanceof BlockContainer
                || container.getContainerType() == ContainerTypes.CHEST
                || container.getContainerType() == ContainerTypes.DOUBLE_CHEST
                || container.getContainerType() == ContainerTypes.BARREL;
    }

    private boolean isContainerBlock(BlockType<?> blockType) {
        var id = blockType.getIdentifier().toString();
        return id.endsWith(":chest") || id.endsWith(":barrel");
    }

    private enum SelectionMode {
        BIND,
        DELETE
    }

    public enum SelectionResult {
        NONE,
        NOT_CONTAINER,
        BOUND,
        DELETED,
        NOT_BOUND
    }
}
