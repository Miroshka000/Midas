package miroshka.midas.buyer.autobuyer;

import org.allaymc.api.block.dto.Block;

public record BlockKey(
        String world,
        int dimensionId,
        int x,
        int y,
        int z
) {
    public static BlockKey from(Block block) {
        var position = block.getPosition();
        return new BlockKey(
                block.getDimension().getWorld().getName(),
                block.getDimension().getDimensionType().getId(),
                position.x(),
                position.y(),
                position.z()
        );
    }

    public AutoBuyerTerminal toTerminal() {
        return new AutoBuyerTerminal(world, dimensionId, x, y, z);
    }
}
