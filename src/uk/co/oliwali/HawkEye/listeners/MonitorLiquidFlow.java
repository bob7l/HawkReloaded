package uk.co.oliwali.HawkEye.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.material.MaterialData;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MonitorLiquidFlow extends HawkEyeListener {

	private List<Integer> fluidBlocks = Arrays.asList(0, 27, 28, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 66, 69, 70, 75, 76, 78, 93, 94);
	private HashMap<Location, String> playerCache = new HashMap<Location, String>(10);
	private int cacheRunTime = 10;
	private int timerId = -1;

	/**
	 * Clears the Player cache when it's been 10 seconds after a waterflow event
	 * Every time the event fires, the timer resets to allow the water to be tracked
	 */
	public void startCacheCleaner() {
		if (DataType.PLAYER_LAVA_FLOW.isLogged() || DataType.PLAYER_WATER_FLOW.isLogged()) {
			Bukkit.getScheduler().cancelTask(timerId);
			timerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(HawkEye.instance, new Runnable() {
				@Override
				public void run() {
					cacheRunTime--;
					if (cacheRunTime == 0) {
						playerCache.clear();
					}
				}
			}, 20L, 20L);
		}
	}

	/**
	 * Resets cache timer and
	 * adds the new location
	 */
	public void addToCache(Location l, String p) {
		cacheRunTime = 10; //Reset cache timer
		playerCache.put(l, p); //Add location to cache
	}

	@HawkEvent(dataType = {DataType.PLAYER_LAVA_FLOW, DataType.PLAYER_WATER_FLOW})
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Material bucket = event.getBucket();
		Location loc = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();

		if ((bucket == Material.WATER_BUCKET && DataType.PLAYER_WATER_FLOW.isLogged()) || (bucket == Material.LAVA_BUCKET && DataType.PLAYER_LAVA_FLOW.isLogged())) {
			playerCache.put(loc, event.getPlayer().getName());
		}
	}

	@HawkEvent(dataType = {DataType.PLAYER_LAVA_FLOW, DataType.PLAYER_WATER_FLOW})
	public void onPlayerBlockFromTo(BlockFromToEvent event) {

		//Only interested in liquids flowing
		if (!event.getBlock().isLiquid()) return;

		Location loc = event.getToBlock().getLocation();
		BlockState from = event.getBlock().getState();
		BlockState to = event.getToBlock().getState();

		if (from.getType() == to.getType()) return;

		Location fromloc = from.getLocation();

		String player = playerCache.get(fromloc);

		if (player == null) return; //This is basically what containsKey does, but is 10x faster :)

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
			DataManager.addEntry(new BlockChangeEntry(player, DataType.PLAYER_LAVA_FLOW, loc, to, from));
			addToCache(loc, player);
		}

		//Water
		else if (from.getTypeId() == 8 || from.getTypeId() == 9) {

			//Normal block
			if (fluidBlocks.contains(to.getTypeId())) {
				data.setData((byte)(from.getRawData() + 1));
				from.setData(data);
				DataManager.addEntry(new BlockChangeEntry(player, DataType.PLAYER_WATER_FLOW, loc, to, from));
				addToCache(loc, player);
			}
			//If we are flowing over lava, cobble or obsidian will form
			BlockState lower = event.getToBlock().getRelative(BlockFace.DOWN).getState();
			if (lower.getTypeId() == 10 || lower.getTypeId() == 11) {
				from.setTypeId(lower.getData().getData() == 0?49:4);
				loc.setY(loc.getY() - 1);
				DataManager.addEntry(new BlockChangeEntry(player, DataType.PLAYER_WATER_FLOW, loc, lower, from));
				addToCache(loc, player);
			}
		}
	}

	@HawkEvent(dataType = {DataType.LAVA_FLOW, DataType.WATER_FLOW})
	public void onBlockFromTo(BlockFromToEvent event) {

		//Only interested in liquids flowing
		if (!event.getBlock().isLiquid()) return;

		Location loc = event.getToBlock().getLocation();
		BlockState from = event.getBlock().getState();
		BlockState to = event.getToBlock().getState();

		if (from.getType() == to.getType()) return;

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
}
