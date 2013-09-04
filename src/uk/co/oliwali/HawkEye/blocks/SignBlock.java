package uk.co.oliwali.HawkEye.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.util.BlockUtil;
import uk.co.oliwali.HawkEye.util.Config;

public class SignBlock implements HawkBlock {

	@Override
	public void Restore(Block b, int id, int data) {
		b.setTypeIdAndData(id, ((byte) data), false);
	}

	@Override
	public void logAttachedBlocks(Block b, Player p, DataType type) {
		Block topb = b.getRelative(BlockFace.UP);
		HawkBlock hb = HawkBlockType.getHawkBlock(topb.getTypeId());
		if (hb.isTopBlock()) {
			hb.logAttachedBlocks(topb, p, type);
			if (hb instanceof SignBlock && Config.isLogged(DataType.SIGN_BREAK))
				DataManager.addEntry(new SignEntry(p, DataType.SIGN_BREAK, hb.getCorrectBlock(topb)));
		}

		for(BlockFace face: BlockUtil.faces) {
			Block attch = b.getRelative(face);
			if (attch.getType() == Material.WALL_SIGN && Config.isLogged(DataType.SIGN_BREAK))
				DataManager.addEntry(new SignEntry(p, DataType.SIGN_BREAK, attch));
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