package uk.co.oliwali.HawkEye.undoData;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import uk.co.oliwali.HawkEye.util.BlockUtil;

public class UndoChest extends UndoBlock {

	private ItemStack[] is;
	private BlockState extra;

	public UndoChest(BlockState state) {
		super(state);

		InventoryHolder invhold = ((InventoryHolder) state);
		ItemStack[] tmp;

		//Properly handle a doublechest
		if (invhold instanceof DoubleChest) {
			DoubleChest dc = (DoubleChest) invhold;
			tmp = dc.getInventory().getContents(); 

			//Find the chests partner
			for (BlockFace face : BlockUtil.faces) {
				Block b = state.getBlock().getRelative(face);
				if (b.getType() == Material.CHEST) {
					extra = b.getState();
				}
			}
		} else {
			tmp = invhold.getInventory().getContents();
		}

		final int len = tmp.length;

		this.is = new ItemStack[len];

		//This code insures we are getting the correct item amounts EVEN if the item drops!
		for (int i = 0; i < len; i++) {
			is[i] = tmp[i] == null ? null : tmp[i].clone();
		}
	}

	@Override
	public void undo() {
		if (is != null && state != null) {
			state.update(true);
			if (extra != null) extra.update();
			
			Inventory inv2 = ((InventoryHolder) state.getBlock().getState()).getInventory();
			inv2.setContents(is);
		}
	}
}