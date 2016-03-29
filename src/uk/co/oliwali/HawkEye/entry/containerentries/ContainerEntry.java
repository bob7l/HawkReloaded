package uk.co.oliwali.HawkEye.entry.containerentries;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.itemserializer.ItemSerializer;
import uk.co.oliwali.HawkEye.util.BlockUtil;
import uk.co.oliwali.HawkEye.util.SerializeUtil;

import java.sql.Timestamp;

/**
 * @author bob7l
 */
public abstract class ContainerEntry extends DataEntry {

    private static ItemSerializer serializer = new ItemSerializer();

    public static ItemSerializer getSerializer() {
        return serializer;
    }

    public ContainerEntry(String player, Timestamp timestamp, int dataId, DataType type, String data, String world, int x, int y, int z) {
        super(player, timestamp, dataId, type, data, world, x, y, z);
    }

    public ContainerEntry(String player, DataType type, Location loc, String serializedData) {
        super(player, type, loc, serializedData);
    }

    @Override
    public String getStringData() {
        StringBuilder sb = new StringBuilder();

        for (String str : SerializeUtil.unJoin(data)) {
            if (sb.length() > 0)
                sb.append(", ");

            sb.append(BlockUtil.formatItemStack(serializer.buildItemFromString(str)));

        }

        return sb.toString();
    }


    protected abstract void handleRollback(Inventory inv, ItemStack item);

    protected abstract void handleRebuild(Inventory inv, ItemStack item);

    @Override
    public boolean rollback(Block block) {
        BlockState blockState = block.getState();

        if (!(blockState instanceof InventoryHolder)) return false;

        Inventory inv = ((InventoryHolder) blockState).getInventory();

        for (String str : SerializeUtil.unJoin(data)) {
            handleRollback(inv, serializer.buildItemFromString(str));
        }

        return true;
    }

    @Override
    public boolean rebuild(Block block) {
        BlockState blockState = block.getState();

        if (!(blockState instanceof InventoryHolder)) return false;

        Inventory inv = ((InventoryHolder) blockState).getInventory();

        for (String str : SerializeUtil.unJoin(data)) {
            handleRebuild(inv, serializer.buildItemFromString(str));
        }

        return true;
    }
}
