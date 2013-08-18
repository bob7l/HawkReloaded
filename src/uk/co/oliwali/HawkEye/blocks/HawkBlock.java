package uk.co.oliwali.HawkEye.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.DataType;

public interface HawkBlock {

	public void Restore(Block b, int id, int data);
	
	public Block getCorrectBlock(Block b);
	
	public void logAttachedBlocks(Block b, Player p, DataType type);
	
	public boolean isTopBlock();
	
	public boolean isAttached();
	
}
