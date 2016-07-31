package uk.co.oliwali.HawkEye.WorldEdit;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.logging.AbstractLoggingExtent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.Consumer;
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.SignEntry;

public class HawkSession extends AbstractLoggingExtent {

	private final Consumer consumer;

	private final Actor player;

	private final World world;


	public HawkSession(Consumer consumer, Actor player, com.sk89q.worldedit.world.World worldedit_world, Extent extent) {
		super(extent);
		this.consumer = consumer;
		this.player = player;
		this.world = ((BukkitWorld) worldedit_world).getWorld();
	}


	@Override
	protected void onBlockChange(Vector v, BaseBlock block) {
		BlockState bs = null;
		int b = world.getBlockTypeIdAt(v.getBlockX(), v.getBlockY(), v.getBlockZ());
		int bdata = world.getBlockAt(v.getBlockX(), v.getBlockY(), v.getBlockZ()).getData();

		if (b == 63 || b == 68) {
			bs = world.getBlockAt(v.getBlockX(), v.getBlockY(), v.getBlockZ()).getState();
		}

		Location loc = new Location(world, v.getBlockX(), v.getBlockY(), v.getBlockZ());

		if (block.getType() != 0) {
			if (block.getType() == b && block.getData() == bdata) 
				return;

			consumer.addEntry(new BlockChangeEntry(player.getName(), DataType.WORLDEDIT_PLACE, loc, b, bdata, block.getType(), block.getData()));
		} else {
			if ((b == 63 || b == 68) && DataType.SIGN_BREAK.isLogged()) {
				consumer.addEntry(new SignEntry(player.getName(), DataType.SIGN_BREAK, bs));
			} else if (b != 0)
				consumer.addEntry(new BlockEntry(player.getName(), DataType.WORLDEDIT_BREAK, b, bdata, loc));
		}
	}
}
