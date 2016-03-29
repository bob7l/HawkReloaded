package uk.co.oliwali.HawkEye.entry.containerentries;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import uk.co.oliwali.HawkEye.DataType;

import java.sql.Timestamp;

/**
 * @author bob7l
 */
public class ContainerInsert extends ContainerEntry {

    public ContainerInsert(String player, Timestamp timestamp, int dataId, DataType type, String data, String world, int x, int y, int z) {
        super(player, timestamp, dataId, type, data, world, x, y, z);
    }

    public ContainerInsert(String player, DataType type, Location loc, String serializedData) {
        super(player, type, loc, serializedData);
    }

    @Override
    protected void handleRollback(Inventory inv, ItemStack item) {
        inv.removeItem(item);
    }

    @Override
    protected void handleRebuild(Inventory inv, ItemStack item) {
        inv.addItem(item);
    }

}
