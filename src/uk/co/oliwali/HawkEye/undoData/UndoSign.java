package uk.co.oliwali.HawkEye.undoData;

import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

public class UndoSign extends UndoBlock {

	private String[] lines;

	public UndoSign(BlockState state) {
		super(state);
		this.lines = ((Sign) state).getLines();
	}

	@Override
	public void undo() {
		if (state != null) {
			state.update(true);

			Sign s2 = (Sign) state.getBlock().getState(); //Get the new state

			for (int i = 0; i < lines.length; i++) {
				s2.setLine(i, lines[i]);
			}
			s2.update();
		}
	}
}
