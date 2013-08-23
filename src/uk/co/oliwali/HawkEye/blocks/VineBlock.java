package uk.co.oliwali.HawkEye.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockEntry;

public class VineBlock implements HawkBlock {

	@Override
	public void logAttachedBlocks(Block b, Player p, DataType type) {
		b = b.getRelative(BlockFace.DOWN);
		while(b.getType() == Material.VINE) {
			Material b2 = b.getRelative(getVineFace(b.getData())).getType();
			if (!b2.isSolid()) {
				DataManager.addEntry(new BlockEntry(p, type, b));
			} else {
				break;
			}
			b = b.getRelative(BlockFace.DOWN);
		}
	}

	@Override
	public boolean isAttached() {
		return true;
	}

	public BlockFace getVineFace(int data) {
		switch(data) {
		case 1: return BlockFace.SOUTH;
		case 8: return BlockFace.EAST;
		case 4: return BlockFace.NORTH;
		case 2: return BlockFace.WEST;
		default: return BlockFace.NORTH;
		}
	}

	@Override
	public void Restore(Block b, int id, int data) {
		b.setTypeIdAndData(id, ((byte) data), true);
	}

	@Override
	public Block getCorrectBlock(Block b) {
		return b;
	}

	@Override
	public boolean isTopBlock() {
		return false;
	}
}