package uk.co.oliwali.HawkEye.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.DataType;

public class Plant implements HawkBlock {

	@Override
	public void Restore(Block b, int id, int data) {
		Block downrel = b.getRelative(BlockFace.DOWN);
		downrel.setType(Material.SOIL);
		downrel.setData((byte) 1);
		b.setTypeIdAndData(id, ((byte) data), false);
	}

	@Override
	public void logAttachedBlocks(Block b, Player p, DataType type) {
		return;
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
