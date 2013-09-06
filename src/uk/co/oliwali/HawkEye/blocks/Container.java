package uk.co.oliwali.HawkEye.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.util.BlockUtil;
import uk.co.oliwali.HawkEye.util.InventoryUtil;

public class Container implements HawkBlock {

	@Override
	public void Restore(Block b, int id, int data) {
		b.setTypeIdAndData(id, ((byte) data), false);
	}

	@Override
	public void logAttachedBlocks(Block b, Player p, DataType type) {
		InventoryUtil.handleHolderRemoval(p.getName(), b.getState());

		for(BlockFace face: BlockUtil.faces) {
			Block attch = b.getRelative(face);
			if (attch.getType() == Material.WALL_SIGN) {
				if (DataType.SIGN_BREAK.isLogged())
					DataManager.addEntry(new SignEntry(p, DataType.SIGN_BREAK, attch));
			}
		}
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