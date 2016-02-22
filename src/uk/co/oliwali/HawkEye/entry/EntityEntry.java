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

	public EntityEntry(int playerId, Timestamp timestamp, int dataId, int typeId, String data, int worldId, int x, int y, int z) { 
		super(playerId, timestamp, dataId, typeId, data, worldId, x, y ,z);
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