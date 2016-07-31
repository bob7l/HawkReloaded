package uk.co.oliwali.HawkEye.blocks.blockhandlers;

public class AttachedBlockHandler extends TopBlockHandler {

	@Override
   public boolean isAttached() {
		return true;
	}
	
	@Override
	public boolean isTopBlock() {
		return true;
	}
}
