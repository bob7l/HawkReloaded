package uk.co.oliwali.HawkEye.undoData;

import org.bukkit.block.BlockState;
import uk.co.oliwali.HawkEye.HawkEye;

public class UndoBlock {

	protected BlockState state;

	public UndoBlock(BlockState state) {
		this.state = state;
	}

	public void undo() {
		if (state != null) {
			final int id = state.getTypeId();
			final int data = state.getData().getData();
			HawkEye.getBlockHandlerContainer().getBlockHandler(id).restore(state.getBlock(), id, data);
		}
	}

	public BlockState getState() {
		return state;
	}
}
