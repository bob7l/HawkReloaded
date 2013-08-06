package uk.co.oliwali.HawkEye.entry;

import java.sql.Timestamp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.DataType;

/**
 * Used for simple rollbacks - sets the block to air regardless of the data
 * @author oliverw92
 */
public class SimpleRollbackEntry extends DataEntry {

	public SimpleRollbackEntry(int playerId, Timestamp timestamp, int dataId, int typeId, String data, String plugin, int worldId, int x, int y, int z) { 
		super(playerId, timestamp, dataId, typeId, data, plugin, worldId, x, y ,z);
		interpretSqlData(data);
	}
	
	public SimpleRollbackEntry() { }

	public SimpleRollbackEntry(Player player, DataType type, Location loc, String data) {
		setInfo(player, type, loc);
		this.data = data;
	}
	public SimpleRollbackEntry(String player, DataType type, Location loc, String data) {
		setInfo(player, type, loc);
		this.data = data;
	}

	@Override
	public boolean rollback(Block block) {
		block.setType(Material.AIR);
		return true;
	}

	@Override
	public boolean rollbackPlayer(Block block, Player player) {
		player.sendBlockChange(block.getLocation(), Material.AIR, (byte)0);
		return true;
	}

}
