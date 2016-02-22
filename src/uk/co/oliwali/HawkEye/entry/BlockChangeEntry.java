package uk.co.oliwali.HawkEye.entry;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.BlockUtil;

import java.sql.Timestamp;

/**
 * Represents a block change entry - one block changing to another
 *
 * @author oliverw92
 */
public class BlockChangeEntry extends DataEntry {


    private String from = null;
    private String to = null;

    public BlockChangeEntry(int playerId, Timestamp timestamp, int dataId, int typeId, String data, int worldId, int x, int y, int z) {
        super(playerId, timestamp, dataId, typeId, worldId, x, y, z);

        String[] info = data.split("-");

        from = info[0];
        to = info[1];
    }

    public BlockChangeEntry(Player player, DataType type, Location loc, BlockState from, BlockState to) {
        this(player, type, loc, BlockUtil.getBlockString(from), BlockUtil.getBlockString(to));
    }

    public BlockChangeEntry(Player player, DataType type, Location loc, BlockState from, int id) {
        this(player, type, loc, BlockUtil.getBlockString(from), String.valueOf(id));
    }

    public BlockChangeEntry(String player, DataType type, Location loc, BlockState from, BlockState to) {
        this(player, type, loc, BlockUtil.getBlockString(from), BlockUtil.getBlockString(to));
    }

    public BlockChangeEntry(Player player, DataType type, Location loc, String from, String to) {
        this(player.getName(), type, loc, from, to);
    }

    public BlockChangeEntry(String player, DataType type, Location loc, int blockfrom, int blockfromdata, int blockto, int blockdatato) {
        this(player, type, loc,
                (blockfromdata > 0 ? blockfrom + ":" + blockfromdata : Integer.toString(blockfrom)),
                (blockdatato > 0 ? blockto + ":" + blockdatato : Integer.toString(blockto)));
    }

    public BlockChangeEntry(String player, DataType type, Location loc, BlockState from, String to) {
        this(player, type, loc, BlockUtil.getBlockString(from), to);
    }

    public BlockChangeEntry(String player, DataType type, Location loc, String from, String to) {
        super(player, type, loc);
        this.from = from;
        this.to = to;
    }

    @Override
    public String getStringData() {
        if (from.startsWith("0")) return BlockUtil.getBlockStringName(to);
        return BlockUtil.getBlockStringName(from) + " changed to " + BlockUtil.getBlockStringName(to);
    }

    @Override
    public String getSqlData() {
        return from + "-" + to;
    }

    @Override
    public boolean rollback(Block block) {
        BlockUtil.setBlockString(block, from);
        return true;
    }

    @Override
    public boolean rollbackPlayer(Block block, Player player) {
        player.sendBlockChange(block.getLocation(), BlockUtil.getIdFromString(from), BlockUtil.getDataFromString(from));
        return true;
    }

    @Override
    public boolean rebuild(Block block) {
        if (to == null) return false;
        else BlockUtil.setBlockString(block, to);
        return true;
    }

}