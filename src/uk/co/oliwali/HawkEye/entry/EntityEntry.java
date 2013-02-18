package uk.co.oliwali.HawkEye.entry;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.EntityUtil;
/**
 * Represents a mob-type entry in the database
 * Rollbacks will set the mob to the data value
 * @author bob7l
 */
public class EntityEntry extends DataEntry {

	public EntityEntry() { }

	public EntityEntry(String player, DataType type, Location loc, String en) {
		setInfo(player, type, loc);
		data = en;
	}

	@Override
	public String getStringData() {
		return data;
	}

	@Override
	public boolean rollback(Block block) {
		EntityUtil.setEntityString(block, data);
		return true;
	}

	//Simply return true since we can't sendBlockChange (It's an entity)
	@Override
	public boolean rollbackPlayer(Block block, Player player) {
		return true;
	}

	//Simply return true since we can't rebuild a death
	@Override
	public boolean rebuild(Block block) {
		return true;
	}

}