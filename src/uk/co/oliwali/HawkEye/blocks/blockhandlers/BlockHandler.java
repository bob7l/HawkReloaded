package uk.co.oliwali.HawkEye.blocks.blockhandlers;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.Consumer;

public interface BlockHandler {

    public void restore(Block b, int id, int data);

    public Block getCorrectBlock(Block b);

    public void logAttachedBlocks(Consumer consumer, Block b, Player p, DataType type);

    public boolean isTopBlock();

    public boolean isAttached();

}
