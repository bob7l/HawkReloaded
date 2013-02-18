package uk.co.oliwali.HawkEye.entry;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.EntityUtil;
/**
 * Represents a hanging-type entry in the database
 * Rollbacks will set the block to the data value
 * @author bob7l
 */
public class HangingEntry extends DataEntry {

	public HangingEntry() { }

	public HangingEntry(Player player, DataType type, Location loc, int en, int da, int extra) {
		setInfo(player, type, loc);
		data = en + ":" + da + ":" + extra;
	}

	public HangingEntry(String player, DataType type, Location loc, int en, int da, int extra) {
		setInfo(player, type, loc);
		data = en + ":" + da + ":" + extra;
	}

	@Override
	public String getStringData() {
		return EntityUtil.getStringName(data);
	}

	@Override
	public boolean rollback(Block block) {
		EntityUtil.setBlockString(block, data);
		return true;
	}

	//Simply return true since we can't sendBlockChange (It's an entity)
	@Override
	public boolean rollbackPlayer(Block block, Player player) {
		return true;
	}

	@Override
	public boolean rebuild(Block block) {
		if (data == null) return false;
		else block.setTypeId(0);
		return true;
	}

}