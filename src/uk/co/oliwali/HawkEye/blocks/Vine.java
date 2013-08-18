package uk.co.oliwali.HawkEye.blocks;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockEntry;

public class Vine extends Default {

	@Override
	public void logAttachedBlocks(Block b, Player p, DataType type) {
		b = b.getRelative(BlockFace.DOWN);
		while(HawkBlockType.getHawkBlock(b.getTypeId()).equals(this)) {
			DataManager.addEntry(new BlockEntry(p, type, b));
			b = b.getRelative(BlockFace.DOWN);
		}
	}
	
	@Override
	public boolean isAttached() {
		return true;
	}
}