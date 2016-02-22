package uk.co.oliwali.HawkEye.entry;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.BlockUtil;

import java.sql.Timestamp;

/**
 * Represents a block-type entry in the database
 * Rollbacks will set the block to the data value
 * @author oliverw92
 */
public class BlockEntry extends DataEntry {

	public BlockEntry() { }

	public BlockEntry(int playerId, Timestamp timestamp, int dataId, int typeId, String data, int worldId, int x, int y, int z) { 
		super(playerId, timestamp, dataId, typeId, data, worldId, x, y ,z);
	}
	
	public BlockEntry(String player, DataType type, Block block) {
		setInfo(player, type, block.getLocation());
		data = BlockUtil.getBlockString(block);
	}
	public BlockEntry(Player player, DataType type, Block block) {
		setInfo(player, type, block.getLocation());
		data = BlockUtil.getBlockString(block);
	}

	public BlockEntry(Player player, DataType type, Block block, Location loc) {
		setInfo(player, type, loc);
		data = BlockUtil.getBlockString(block);
	}

	public BlockEntry(String player, DataType type, int block, int blockdata, Location loc) {
		setInfo(player, type, loc);
        if (blockdata != 0) data = block + ":" + blockdata;
        else data = Integer.toString(block);
	}
	
	@Override
	public String getStringData() {
		return BlockUtil.getBlockStringName(data);
	}

	@Override
	public boolean rollback(Block block) {
		BlockUtil.setBlockString(block, data);
		return true;
	}

	@Override
	public boolean rollbackPlayer(Block block, Player player) {
		player.sendBlockChange(block.getLocation(), BlockUtil.getIdFromString(data), BlockUtil.getDataFromString(data));
		return true;
	}

	@Override
	public boolean rebuild(Block block) {
		block.setTypeId(0);
		return true;
	}

}
