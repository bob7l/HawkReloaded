package uk.co.oliwali.HawkEye.entry;

import java.sql.Timestamp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.BlockUtil;

/**
 * Represents a block change entry - one block changing to another
 * @author oliverw92
 */
public class BlockChangeEntry extends DataEntry {

	
	private String from = null;
	private String to = null;
	
	public BlockChangeEntry(int playerId, Timestamp timestamp, int dataId, int typeId, String data, int worldId, int x, int y, int z) { 
		super(playerId, timestamp, dataId, typeId, worldId, x, y ,z);
		interpretSqlData(data);
	}

	public BlockChangeEntry(Player player, DataType type, Location loc, BlockState from, BlockState to) {
		setInfo(player, type, loc);
		this.from = BlockUtil.getBlockString(from);
		this.to = BlockUtil.getBlockString(to);
	}
	public BlockChangeEntry(Player player, DataType type, Location loc, BlockState from, int id) {
		setInfo(player, type, loc);
		this.from = BlockUtil.getBlockString(from);
		this.to = String.valueOf(id);
	}
	public BlockChangeEntry(String string, DataType type, Location loc, Block block, BlockState to) {
		setInfo(string, type, loc);
		this.from = BlockUtil.getBlockString(block);
		this.to = BlockUtil.getBlockString(to);
	}
	public BlockChangeEntry(String player, DataType type, Location loc, BlockState from, BlockState to) {
		setInfo(player, type, loc);
		this.from = BlockUtil.getBlockString(from);
		this.to = BlockUtil.getBlockString(to);
	}
	public BlockChangeEntry(Player player, DataType type, Location loc, String from, String to) {
		setInfo(player, type, loc);
		this.from = from;
		this.to = to;
	}
	public BlockChangeEntry(String player, DataType type, Location loc, String from, String to) {
		setInfo(player, type, loc);
		this.from = from;
		this.to = to;
	}
	public BlockChangeEntry(Player player, DataType type, Location loc, String from, BlockState to) {
		setInfo(player, type, loc);
		this.from = from;
		this.to = BlockUtil.getBlockString(to);
	}
	
	public BlockChangeEntry(String player, DataType type, Location loc, int blockfrom, int blockfromdata, int blockto, int blockdatato) {
		setInfo(player, type, loc);
        if (blockfromdata != 0) from = blockfrom + ":" + blockfromdata;
        else from = Integer.toString(blockfrom);
        
        if (blockdatato != 0) to = blockto + ":" + blockdatato;
        else to = Integer.toString(blockto);
	}

	public BlockChangeEntry(String player, DataType type, Location loc, BlockState from, String to) {
		setInfo(player, type, loc);
		this.from = BlockUtil.getBlockString(from);
		this.to = to;
	}

	@Override
	public String getStringData() {
		if (from == null || from.equals("0")) return BlockUtil.getBlockStringName(to);
		return BlockUtil.getBlockStringName(from) + " changed to " + BlockUtil.getBlockStringName(to);
	}

	@Override
	public String getSqlData() {
		return from + "-" + to;
	}

	@Override
	public boolean rollback(Block block) {
		if (from == null)
			block.setType(Material.AIR);
		else
			BlockUtil.setBlockString(block, from);
		return true;
	}

	@Override
	public boolean rollbackPlayer(Block block, Player player) {
		if (from == null) player.sendBlockChange(block.getLocation(), 0, (byte)0);
		else player.sendBlockChange(block.getLocation(), BlockUtil.getIdFromString(from), BlockUtil.getDataFromString(from));
		return true;
	}

	@Override
	public boolean rebuild(Block block) {
		if (to == null) return false;
		else BlockUtil.setBlockString(block, to);
		return true;
	}

	@Override
	public void interpretSqlData(String data) {
		if (data.indexOf("-") == -1) {
			from = null;
			to = data;
			System.out.print("test");
		}
		else {
			from = data.substring(0, data.indexOf("-"));
			to = data.substring(data.indexOf("-") + 1);
		}
	}

}