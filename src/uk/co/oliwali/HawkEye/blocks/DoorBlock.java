package uk.co.oliwali.HawkEye.blocks;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.DataType;

public class DoorBlock implements HawkBlock {

	@Override
	public void Restore(Block b, int id, int data) {
		if (data == (byte)8 || data == (byte)9) return; //This means the invalid part of the door was logged
		
		b.setTypeIdAndData(id, ((byte)data), true);

		Block block = b.getRelative(BlockFace.UP);

		Block side = null;
		Block oside = null;
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
			block.setTypeIdAndData(id, (byte)9, true);
		} else if (oid == 64 || oid == 71) {
			oside.getRelative(BlockFace.UP).setTypeIdAndData(id, (byte)9, true);
			block.setTypeIdAndData(id, (byte)8, true);
		} else {
			block.setTypeIdAndData(id, (byte)8, true);
		}
	}

	@Override
	public void logAttachedBlocks(Block b, Player p, DataType type) {
		return;
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
