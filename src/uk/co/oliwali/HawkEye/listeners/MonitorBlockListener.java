package uk.co.oliwali.HawkEye.listeners;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.MaterialData;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.entry.SimpleRollbackEntry;
import uk.co.oliwali.HawkEye.util.BlockUtil;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.InventoryUtil;

/**
 * Block listener class for HawkEye
 * @author oliverw92
 */
public class MonitorBlockListener extends HawkEyeListener {

	private BlockFace[] faces = new BlockFace[] {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
	private List<Integer> fluidBlocks = Arrays.asList(0, 27, 28, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 66, 69, 70, 75, 76, 78, 93, 94);

	public MonitorBlockListener(HawkEye HawkEye) {
		super(HawkEye);
	}

	@HawkEvent(dataType = DataType.BLOCK_BREAK)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		Material type = block.getType();

		if (Config.BlockFilter.contains(block.getTypeId())) return;
		if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)
			DataManager.addEntry(new SignEntry(player, DataType.SIGN_BREAK, block));
		else {
			if (BlockUtil.isInventoryHolder(type.getId()) && Config.isLogged(DataType.CONTAINER_TRANSACTION)) {
				InventoryUtil.handleHolderRemoval(player.getName(), block.getState());
			}
			if (type.equals(Material.WOODEN_DOOR) || type.equals(Material.IRON_DOOR_BLOCK)) {
				//If the data is 8, this is the top half!
				if (block.getData() == (byte)8 || block.getData() == (byte)9) { 
					block = block.getRelative(BlockFace.DOWN);
				}
				DataManager.addEntry(new BlockEntry(player, DataType.BLOCK_BREAK, block));
				return;
			}

			if (type.equals(Material.BED_BLOCK)) {
				if (block.getData() > 7) {
					block = block.getRelative(BlockUtil.getBedFace(block));
				}
			}

			for(BlockFace face: faces) {
				Block b = block.getRelative(face);
				if (BlockUtil.isItemAttached(b.getTypeId())) {
					DataManager.addEntry(new BlockEntry(player, DataType.BLOCK_BREAK, b));
				}
			}

			Block topblock = block.getRelative(BlockFace.UP);

			if (BlockUtil.itemOnTop(topblock.getTypeId())) {
				DataManager.addEntry(new BlockEntry(player, DataType.BLOCK_BREAK, topblock));
			}
			DataManager.addEntry(new BlockEntry(player, DataType.BLOCK_BREAK, block));

			if (type.equals(Material.SUGAR_CANE_BLOCK) || topblock.getType().equals(Material.SUGAR_CANE_BLOCK)) {
				while(topblock.getType().equals(Material.SUGAR_CANE_BLOCK)) {
					DataManager.addEntry(new BlockEntry(player, DataType.BLOCK_BREAK, topblock));
					topblock = topblock.getRelative(BlockFace.UP);
				}
			}
		}
	}

	@HawkEvent(dataType = DataType.BLOCK_PLACE)
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST || Config.BlockFilter.contains(block.getTypeId())) return;

		// Temporary Stair Fix (Delays the storing of the block until the actual data has been applied to the block)
		final BlockPlaceEvent finalEvent = event;
		HawkEye.server.getScheduler().scheduleSyncDelayedTask(HawkEye.instance, new Runnable() {
			@Override
			public void run() {
				Block b = finalEvent.getBlock();
				DataManager.addEntry(new BlockChangeEntry(finalEvent.getPlayer(), (b.getType().equals(Material.FIRE)) ? DataType.FLINT_AND_STEEL : DataType.BLOCK_PLACE, b.getLocation(), finalEvent.getBlockReplacedState(), b.getState()));
			}
		}, 1L);
		return;
	}
	
	@HawkEvent(dataType = DataType.SIGN_PLACE)
	public void onSignChange(SignChangeEvent event) {
		DataManager.addEntry(new SignEntry(event.getPlayer().getName(), DataType.SIGN_PLACE, event.getBlock(), event.getLines()));
	}

	@HawkEvent(dataType = DataType.BLOCK_FORM)
	public void onBlockForm(BlockFormEvent event) {
		DataManager.addEntry(new BlockChangeEntry("Environment", DataType.BLOCK_FORM, event.getBlock().getLocation(), event.getBlock().getState(), event.getNewState()));
	}

	@HawkEvent(dataType = DataType.BLOCK_FADE)
	public void onBlockFade(BlockFadeEvent event) {
		DataManager.addEntry(new BlockChangeEntry("Environment", DataType.BLOCK_FADE, event.getBlock().getLocation(), event.getBlock().getState(), event.getNewState()));
	}

	@HawkEvent(dataType = DataType.BLOCK_BURN)
	public void onBlockBurn(BlockBurnEvent event) {
		DataManager.addEntry(new BlockEntry("Environment", DataType.BLOCK_BURN, event.getBlock()));
	}

	@HawkEvent(dataType = {DataType.LAVA_FLOW, DataType.WATER_FLOW})
	public void onBlockFromTo(BlockFromToEvent event) {

		//Only interested in liquids flowing
		if (!event.getBlock().isLiquid()) return;

		Location loc = event.getToBlock().getLocation();
		BlockState from = event.getBlock().getState();
		BlockState to = event.getToBlock().getState();
		MaterialData data = from.getData();
		
		//Lava
		if (from.getTypeId() == 10 || from.getTypeId() == 11) {

			//Flowing into a normal block
			if (fluidBlocks.contains(to.getTypeId())) {
				data.setData((byte)(from.getRawData() + 1));
				from.setData(data);
			}

			//Flowing into water
			else if (to.getTypeId() == 8 || to.getTypeId() == 9) {
				from.setTypeId(event.getFace() == BlockFace.DOWN?10:4);
				data.setData((byte)0);
				from.setData(data);
			}
			DataManager.addEntry(new BlockChangeEntry("Environment", DataType.LAVA_FLOW, loc, to, from));

		}

		//Water
		else if (from.getTypeId() == 8 || from.getTypeId() == 9) {

			//Normal block
			if (fluidBlocks.contains(to.getTypeId())) {
				data.setData((byte)(from.getRawData() + 1));
				from.setData(data);
				DataManager.addEntry(new BlockChangeEntry("Environment", DataType.WATER_FLOW, loc, to, from));
			}

			//If we are flowing over lava, cobble or obsidian will form
			BlockState lower = event.getToBlock().getRelative(BlockFace.DOWN).getState();
			if (lower.getTypeId() == 10 || lower.getTypeId() == 11) {
				from.setTypeId(lower.getData().getData() == 0?49:4);
				loc.setY(loc.getY() - 1);
				DataManager.addEntry(new BlockChangeEntry("Environment", DataType.WATER_FLOW, loc, lower, from));
			}

		}
		
	}
	
	@HawkEvent(dataType = DataType.LEAF_DECAY)
	public void onLeavesDecay(LeavesDecayEvent event) {
		Block block = event.getBlock();
		if (block == null) return;
		DataManager.addEntry(new BlockEntry("Environment", DataType.LEAF_DECAY, event.getBlock()));
	}
	
	@HawkEvent(dataType = DataType.BLOCK_IGNITE)
	public void onBlockIgnite(BlockIgniteEvent event) {
		IgniteCause ig = event.getCause();
		Location loc = event.getBlock().getLocation();
		if (ig.equals(IgniteCause.FLINT_AND_STEEL)) return;
		DataManager.addEntry(new SimpleRollbackEntry("Environment", DataType.BLOCK_IGNITE, loc, ig.name()));
	}
}
