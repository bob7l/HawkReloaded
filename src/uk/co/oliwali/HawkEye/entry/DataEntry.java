package uk.co.oliwali.HawkEye.entry;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.undoData.UndoBlock;
import uk.co.oliwali.HawkEye.undoData.UndoChest;
import uk.co.oliwali.HawkEye.undoData.UndoSign;
import uk.co.oliwali.HawkEye.util.Util;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Represents a HawkEye database entry
 * This class can be extended and overriden by sub-entry classes to allow customisation of rollbacks etc
 * @author oliverw92
 */
public class DataEntry {

    private int dataId;

    private Timestamp timestamp;

    private String player = null;

    private String world;

    private double x;

    private double y;

    private double z;

    private UndoBlock undo;

    protected DataType type = null;

    protected String data = null;

    public DataEntry() { }

    public DataEntry(int playerId, Timestamp timestamp, int dataId, int typeId, String data, int worldId, int x, int y, int z) {
        this(playerId, timestamp, dataId, typeId, worldId, x, y ,z);
        this.data = data;
    }

    /**
     * Dataless constructor. Used by subclasses that have local data fields
     */
    public DataEntry(int playerId, Timestamp timestamp, int dataId, int typeId, int worldId, int x, int y, int z) {
        // TODO: Optimize DataType.fromId(), DataManager.getPlayer(), DataManager.getWorld();
        this.player = DataManager.getPlayer(playerId);
        this.timestamp = timestamp;
        this.dataId = dataId;
        this.type = DataType.fromId(typeId);
        this.world = DataManager.getWorld(worldId);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public DataEntry(Player player, DataType type, Location loc, String data) {
    	this(player.getName(), type, loc, data);
    }

    public DataEntry(String player, DataType type, Location loc, String data) {
    	setInfo(player, type, loc);
    	setData(data);
    }

	public void setDataId(int dataId) {
		this.dataId = dataId;
	}
    public int getDataId() {
		return dataId;
	}

	public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
    public String getPlayer() {
        return player;
    }

    public void setType(DataType type) {
    	this.type = type;
    }
    public DataType getType() {
    	return type;
    }

    public void setWorld(String world) {
        this.world = world;
    }
    public String getWorld() {
        return world;
    }

    public void setX(double x) {
        this.x = x;
    }
    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }
    public double getY() {
        return y;
    }

    public void setZ(double z) {
    	this.z = z;
    }
    public double getZ() {
    	return z;
    }

    public void setData(String data) {
    	this.data = data;
    }

    public void setUndoState(BlockState state) {
    	if (state instanceof InventoryHolder)
    		undo = new UndoChest(state);
    	else if (state instanceof Sign)
    		undo = new UndoSign(state);
    	else
    		undo = new UndoBlock(state);
    }

    public UndoBlock getUndo() {
    	return undo;
    }

    /**
     * Returns the entry data in a visually attractive and readable way for an in-game user to read
     * Extending classes can add colours, customise layout etc.
     * @return
     */
    public String getStringData() {
    	return data;
    }

	/**
	 * Returns the entry data ready for storage in the database
	 * Extending classes can override this method and format the data as they wish
	 * @return string containing data to be stored
	 */
	public String getSqlData() {
		return data;
	}

	/**
	 * Rolls back the data entry on the specified block
	 * Default is to return false, however extending classes can override this and do their own thing
	 * @param block
	 * @return true if rollback is performed, false if it isn't
	 */
	public boolean rollback(Block block) {
		return false;
	}

	/**
	 * Performs a local rollback for the specified player only
	 * Default is to return false, and most extending classes will not override this
	 * If overriding, the method should use Player.sendBlockChange() for sending fake changes
	 * @param block
	 * @param player
	 * @return true if rollback is performed, false if it isn't
	 */
	public boolean rollbackPlayer(Block block, Player player) {
		return false;
	}

	/**
	 * Rebuilds the entry (reapplies it)
	 * Extending classes can implement this method to do custom things
	 * @param block
	 * @return true if rebuild is performed, false it if isn't
	 */
	public boolean rebuild(Block block) {
		return false;
	}

	public void undo() {
		this.undo.undo();
	}

	/**
	 * Parses the inputted action into the DataEntry instance
	 * @param player
	 * @param type
	 * @param loc
     */
	public void setInfo(Player player, DataType type, Location loc) {
		setInfo(player.getName(), type, loc);
	}
	
	public void setInfo(String player, DataType type, Location loc) {
		loc = Util.getSimpleLocation(loc);
	    setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		setPlayer(player);
		setType(type);
		setWorld(loc.getWorld().getName());
		setX(loc.getX());
		setY(loc.getY());
		setZ(loc.getZ());
	}

}
