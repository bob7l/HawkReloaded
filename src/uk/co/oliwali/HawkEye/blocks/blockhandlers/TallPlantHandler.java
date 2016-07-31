package uk.co.oliwali.HawkEye.blocks.blockhandlers;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.blocks.BlockHandlerContainer;
import uk.co.oliwali.HawkEye.database.Consumer;
import uk.co.oliwali.HawkEye.entry.BlockEntry;

public class TallPlantHandler extends DefaultBlockHandler {

    public TallPlantHandler(BlockHandlerContainer hawkBlockType) {
        super(hawkBlockType);
    }

    @Override
    public void logAttachedBlocks(Consumer consumer, Block b, Player p, DataType type) {
        b = b.getRelative(BlockFace.UP);

        while (hawkBlockContainer.getBlockHandler(b.getTypeId()).equals(this)) {
            consumer.addEntry(new BlockEntry(p, type, b));

            b = b.getRelative(BlockFace.UP);
        }
    }

    @Override
    public boolean isTopBlock() {
        return true;
    }
}