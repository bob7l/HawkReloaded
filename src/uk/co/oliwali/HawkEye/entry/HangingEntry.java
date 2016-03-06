package uk.co.oliwali.HawkEye.entry;

import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.itemserializer.ItemSerializer;
import uk.co.oliwali.HawkEye.util.BlockUtil;
import uk.co.oliwali.HawkEye.util.EntityUtil;

import java.sql.Timestamp;
/**
 * Represents a hanging-type entry in the database
 * Rollbacks will set the block to the data value
 * @author bob7l
 */
public class HangingEntry extends DataEntry {

    private static ItemSerializer serializer = new ItemSerializer();

	public HangingEntry(String player, Timestamp timestamp, int dataId, DataType type, String data, String world, int x, int y, int z) {
		super(player, timestamp, dataId, type, data, world, x, y, z);
	}

	public HangingEntry() { }

	public HangingEntry(String player, DataType type, Location loc, int typeId, int faceId, ItemStack item) {
		this(player, type, loc, typeId, faceId, serializer.serializeItem(item));
	}

	public HangingEntry(String player, DataType type, Location loc, int typeId, int faceId, String extra) {
		super(player, type, loc, (typeId + ":" + faceId + ":" + extra) );
	}


	@Override
	public String getStringData() {
        String[] args = data.split(":", 3);

        if (args[0].equals("389")) {
            ItemStack item = serializer.buildItemFromString(args[2]);
            return "ItemFrame" + (item.getType().equals(Material.AIR) ? "" : " with " + BlockUtil.formatItemStack(item));
        }

        return "Painting";
	}

	@Override
	public boolean rollback(Block block) {
        String[] args = data.split(":", 3);

        BlockFace face = EntityUtil.getFaceFromInt(Integer.parseInt(args[1]));

       // EntityUtil.spawnFrame(block, face, (type == 389)); // 1.7 hack to properly place itemframes, no longer needed in 1.8

		//This can and WILL throw exceptions - I believe it's a bug with spigot's API
		try {
			if (args[0].equals("389")) {
				ItemFrame itemframe = block.getWorld().spawn(block.getLocation(), ItemFrame.class);
				itemframe.setFacingDirection(face.getOppositeFace(), true);
				itemframe.setItem(serializer.buildItemFromString(args[2]));
			} else {
				Painting painting = block.getWorld().spawn(block.getLocation(), Painting.class);
				painting.setFacingDirection(face.getOppositeFace(), true);
				painting.setArt(Art.getById(Integer.parseInt(args[2])));
			}
		} catch (Exception e) {
			return false; //The exception thrown is known, and shouldn't be printed
		}

		return true;
	}

	//Simply return true since we can't sendBlockChange (It's an entity)
	@Override
	public boolean rollbackPlayer(Block block, Player player) {
		return true;
	}

	//Simply return true since we can't rebuild (It's an entity)
	@Override
	public boolean rebuild(Block block) {
		return true;
	}


}