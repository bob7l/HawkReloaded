package uk.co.oliwali.HawkEye.blocks.blockhandlers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.Consumer;
import uk.co.oliwali.HawkEye.entry.BlockEntry;

public class DoublePlantHandler extends BasicBlockHandler {

	@Override
	public void logAttachedBlocks(Consumer consumer, Block b, Player p, DataType type) {
		Block b2 = b.getRelative(BlockFace.UP);

		if (b2.getType() == Material.DOUBLE_PLANT) {
			consumer.addEntry(new BlockEntry(p, type, b2));
		} else {
			b2 = b.getRelative(BlockFace.DOWN);

			if (b2.getType() == Material.DOUBLE_PLANT) {
				consumer.addEntry(new BlockEntry(p, type, b2));
			}
		}
	}

	@Override
	public boolean isTopBlock() {
		return true;
	}
}