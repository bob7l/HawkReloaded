package uk.co.oliwali.HawkEye.blocks;

public class AttachedBlock extends TopBlock {

	@Override
   public boolean isAttached() {
		return true;
	}
	
	@Override
	public boolean isTopBlock() {
		return true;
	}
}
