package uk.co.oliwali.HawkEye.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.DataType;

public class BasicBlock extends Default {

	@Override
	public void logAttachedBlocks(Block b, Player p, DataType type) {
		return;
	}
}