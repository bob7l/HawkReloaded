package uk.co.oliwali.HawkEye.undoData;

import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class UndoChest extends UndoBlock {

	private ItemStack[] is;

	public UndoChest(BlockState state) {
		super(state);
		ItemStack[] tmp = ((InventoryHolder) state).getInventory().getContents();

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
			Inventory inv2 = ((InventoryHolder) state.getBlock().getState()).getInventory();
			inv2.setContents(is);
		}
	}
}