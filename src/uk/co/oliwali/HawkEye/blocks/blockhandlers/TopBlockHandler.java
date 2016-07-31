package uk.co.oliwali.HawkEye.blocks.blockhandlers;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.Consumer;

public class TopBlockHandler implements BlockHandler {

    @Override
    public void restore(Block b, int id, int data) {
        b.setTypeIdAndData(id, ((byte) data), false);
    }

    @Override
    public void logAttachedBlocks(Consumer consumer, Block b, Player p, DataType type) {
    }

    @Override
    public Block getCorrectBlock(Block b) {
        return b;
    }

    @Override
    public boolean isTopBlock() {
        return true;
    }

    @Override
    public boolean isAttached() {
        return true;
    }
}
