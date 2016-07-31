package uk.co.oliwali.HawkEye.blocks.blockhandlers;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.Consumer;

public class BedBlockHandler implements BlockHandler {

	@Override
	public void restore(Block b, int id, int data) {
		if (data > 7) return;
		
		int beddata = 0;
		Block bed = null;

		if (data == 0) {
			bed = b.getRelative(BlockFace.SOUTH);
			beddata = 8;
		}
		if (data == 1) {
			bed = b.getRelative(BlockFace.WEST);
			beddata = 9;
		}
		if (data == 2) {
			bed = b.getRelative(BlockFace.NORTH);
			beddata = 10;
		}
		if (data == 3) {
			bed = b.getRelative(BlockFace.EAST);
			beddata = 11;
		}
		if (bed != null) {
			bed.setTypeIdAndData(id, ((byte)beddata), false);
		}
		
		b.setTypeIdAndData(id, ((byte)data), false);
	}

	@Override
	public void logAttachedBlocks(Consumer consumer, Block b, Player p, DataType type) { }

	@Override
	public Block getCorrectBlock(Block b) {
		if (b.getData() > 7) {
			return b.getRelative(getBedFace(b));
		}
		return b;
	}

	public static BlockFace getBedFace(Block block) {
		int Data = block.getData();
		switch(Data){
		case 8: return BlockFace.NORTH;
		case 9: return BlockFace.EAST;
		case 10: return BlockFace.SOUTH;
		case 11: return BlockFace.WEST;
		}
		return null;
	}
	
	@Override
	public boolean isTopBlock() {
		return false;
	}
	
	@Override
	public boolean isAttached() {
		return false;
	}
}