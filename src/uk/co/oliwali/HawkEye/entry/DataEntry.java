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

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Represents a HawkEye database entry
 * This class can be extended and overriden by sub-entry classes to allow customisation of rollbacks etc
 *
 * @author oliverw92
 */
public class DataEntry {

    protected DataType type;

    protected String data;

    private int dataId;

    private Timestamp timestamp;

    private String player;

    private String world;

    private double x;
    private double y;
    private double z;

    private UndoBlock undo;

    public DataEntry() { }

    public DataEntry(Player player, DataType type, Location loc) {
        this(player.getName(), type, loc);
    }

    public DataEntry(Player player, DataType type, Location loc, String data) {
        this(player.getName(), type, loc, data);
    }

    public DataEntry(String player, DataType type, Location loc, String data) {
        this(player, type, loc);
        this.data = data;
    }

    public DataEntry(String player, DataType type, Location loc) {
        this(player, new Timestamp(Calendar.getInstance().getTimeInMillis()), type, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public DataEntry(int playerId, Timestamp timestamp, int dataId, int typeId, String data, int worldId, int x, int y, int z) {
        this(playerId, timestamp, dataId, typeId, worldId, x, y, z);
        this.data = data;
    }

    public DataEntry(int playerId, Timestamp timestamp, int dataId, int typeId, int worldId, int x, int y, int z) {
        // TODO: Optimize DataType.fromId(), DataManager.getPlayer(), DataManager.getWorld();
        this(DataManager.getPlayer(playerId), timestamp, DataType.fromId(typeId), DataManager.getWorld(worldId), x, y, z);
        this.dataId = dataId;
    }

    public DataEntry(String player, Timestamp timestamp, DataType type, String world, int x, int y, int z) {
        this.player = player;
        this.timestamp = timestamp;
        this.type = type;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getDataId() {
        return dataId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
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
     *
     * @return
     */
    public String getStringData() {
        return data;
    }

    /**
     * Returns the entry data ready for storage in the database
     * Extending classes can override this method and format the data as they wish
     *
     * @return string containing data to be stored
     */
    public String getSqlData() {
        return data;
    }

    /**
     * Rolls back the data entry on the specified block
     * Default is to return false, however extending classes can override this and do their own thing
     *
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
     *
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
     *
     * @param block
     * @return true if rebuild is performed, false it if isn't
     */
    public boolean rebuild(Block block) {
        return false;
    }

    public void undo() {
        this.undo.undo();
    }

}
