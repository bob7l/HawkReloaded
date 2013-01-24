package uk.co.oliwali.HawkEye.WorldEdit;

import org.bukkit.Location;
import org.bukkit.World;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;
import uk.co.oliwali.HawkEye.entry.BlockEntry;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bags.BlockBag;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;

public class HawkSession extends EditSession {

	private LocalPlayer player;

	public HawkSession(LocalWorld world, int maxBlocks, LocalPlayer player) {
		super(world, maxBlocks);
		this.player = player;
	}

	public HawkSession(LocalWorld world, int maxBlocks, BlockBag blockBag, LocalPlayer player) {
		super(world, maxBlocks, blockBag);
		this.player = player;
	}

	@Override
	public boolean rawSetBlock(Vector v, BaseBlock block) {
		World world = ((BukkitWorld) player.getWorld()).getWorld();
		int b = world.getBlockTypeIdAt(v.getBlockX(), v.getBlockY(), v.getBlockZ());
		int bdata = world.getBlockAt(v.getBlockX(), v.getBlockY(), v.getBlockZ()).getData();

		if (super.rawSetBlock(v, block)) {
			Location loc = new Location(world, v.getBlockX(), v.getBlockY(), v.getBlockZ());

			if (block.getType() != 0) {
				DataManager.addEntry(new BlockChangeEntry(player.getName(), DataType.WORLDEDIT_PLACE, loc, b, bdata, block.getType(), block.getData()));
			} else {
				DataManager.addEntry(new BlockEntry(player.getName(), DataType.WORLDEDIT_BREAK, b, bdata, loc));
			}
		}
		return super.rawSetBlock(v, block);
	}
}