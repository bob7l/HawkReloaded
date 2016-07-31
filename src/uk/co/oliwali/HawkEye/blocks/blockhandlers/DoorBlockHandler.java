package uk.co.oliwali.HawkEye.blocks.blockhandlers;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.Consumer;

public class DoorBlockHandler implements BlockHandler {

	@Override
	public void restore(Block b, int id, int data) {
		if (data == (byte)8 || data == (byte)9) return; //This means the invalid part of the door was logged
		
		b.setTypeIdAndData(id, ((byte)data), false);

		Block block = b.getRelative(BlockFace.UP);

		Block side;
		Block oside ;

		if (data == 0) {
			side = b.getRelative(BlockFace.NORTH);
			oside = b.getRelative(BlockFace.SOUTH);
		} else if (data == 1) {
			side = b.getRelative(BlockFace.EAST);
			oside = b.getRelative(BlockFace.WEST);
		} else if (data == 2) {
			side = b.getRelative(BlockFace.SOUTH);
			oside = b.getRelative(BlockFace.NORTH);
		} else {
			side = b.getRelative(BlockFace.WEST);
			oside = b.getRelative(BlockFace.EAST);
		}

		int id2 = side.getTypeId();
		int oid = oside.getTypeId();
		if (id2 == 64 || id2 == 71) {
			block.setTypeIdAndData(id, (byte)9, false);
		} else if (oid == 64 || oid == 71) {
			oside.getRelative(BlockFace.UP).setTypeIdAndData(id, (byte)9, false);
			block.setTypeIdAndData(id, (byte)8, false);
		} else {
			block.setTypeIdAndData(id, (byte)8, false);
		}
	}

	@Override
	public void logAttachedBlocks(Consumer consumer, Block b, Player p, DataType type) {
	}

	@Override
	public Block getCorrectBlock(Block b) {
		if (b.getData() == (byte)8 || b.getData() == (byte)9) { 
			return b.getRelative(BlockFace.DOWN);
		}
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
