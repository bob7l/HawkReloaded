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

public class BasicBlock extends Default {

	
	@Override
	public void logAttachedBlocks(Block b, Player p, DataType type) {
		for(BlockFace face: BlockUtil.faces) {
			Block attch = b.getRelative(face);
			if (attch.getType() == Material.WALL_SIGN) {
				if (Config.isLogged(DataType.SIGN_BREAK))
					DataManager.addEntry(new SignEntry(p, DataType.SIGN_BREAK, attch));
			}
		}
	}
}