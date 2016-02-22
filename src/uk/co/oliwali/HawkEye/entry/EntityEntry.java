package uk.co.oliwali.HawkEye.entry;

import org.bukkit.Location;
import org.bukkit.block.Block;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.EntityUtil;

import java.sql.Timestamp;
/**
 * Represents a mob-type entry in the database
 * Rollbacks will set the mob to the data value
 * @author bob7l
 */
public class EntityEntry extends DataEntry {

	public EntityEntry() { }

	public EntityEntry(String player, Timestamp timestamp, int dataId, DataType type, String data, String world, int x, int y, int z) {
		super(player, timestamp, dataId, type, data, world, x, y, z);
	}
	
	public EntityEntry(String player, DataType type, Location loc, String data) {
		super(player, type, loc, data);
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

}