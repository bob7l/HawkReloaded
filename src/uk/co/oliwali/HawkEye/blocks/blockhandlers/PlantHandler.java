package uk.co.oliwali.HawkEye.blocks.blockhandlers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.Consumer;

public class PlantHandler implements BlockHandler {

    @Override
    public void restore(Block b, int id, int data) {
        Block downrel = b.getRelative(BlockFace.DOWN);
        downrel.setType(Material.SOIL);
        downrel.setData((byte) 1);
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
        return false;
    }
}
