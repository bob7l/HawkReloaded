package uk.co.oliwali.HawkEye.entry;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.itemserializer.ItemSerializer;
import uk.co.oliwali.HawkEye.util.BlockUtil;
import uk.co.oliwali.HawkEye.util.EntityUtil;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author bob7l
 */
public class ItemFrameModifyEntry extends DataEntry {

    private static ItemSerializer serializer = new ItemSerializer();

    public ItemFrameModifyEntry(String player, Timestamp timestamp, int dataId, DataType type, String data, String world, int x, int y, int z) {
        super(player, timestamp, dataId, type, data, world, x, y, z);
    }

    public ItemFrameModifyEntry(String player, DataType type, Location loc, ItemStack item) {
        super(player, type, loc, serializer.serializeItem(item));
    }

    @Override
    public String getStringData() {
        return BlockUtil.formatItemStack(serializer.buildItemFromString(data));
    }

    @Override
    public boolean rollback(Block block) {

        List<Entity> entities = EntityUtil.getEntitiesAtLoc(block.getLocation());

        for (Entity e : entities) {
            if (e instanceof ItemFrame) {
                ItemStack item = serializer.buildItemFromString(data);
                ItemFrame it = (ItemFrame) e;

                if (type == DataType.FRAME_INSERT) {
                    if (it.getItem().equals(item))
                        it.setItem(null);
                    else
                        return false;
                } else {
                    it.setItem(item);
                }

                return true;
            }
        }

        return false;
    }

}