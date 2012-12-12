package uk.co.oliwali.HawkEye.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.entry.SimpleRollbackEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Player listener class for HawkEye
 * @author oliverw92
 */
public class MonitorPlayerListener extends HawkEyeListener {

	public MonitorPlayerListener(HawkEye HawkEye) {
		super(HawkEye);
	}

	@HawkEvent(dataType = DataType.CHAT)
	 public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		//Check for inventory close
		HawkEye.containerManager.checkInventoryClose(event.getPlayer());
		DataManager.addEntry(new DataEntry(player, DataType.CHAT, player.getLocation(), event.getMessage()));
	}

	@HawkEvent(dataType = DataType.COMMAND)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		HawkEye.containerManager.checkInventoryClose(player);
		if (Config.CommandFilter.contains(event.getMessage().split(" ")[0])) return;
		DataManager.addEntry(new DataEntry(player, DataType.COMMAND, player.getLocation(), event.getMessage()));
	}

	@HawkEvent(dataType = DataType.JOIN)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Location loc  = player.getLocation();
		DataManager.addEntry(new DataEntry(player, DataType.JOIN, loc, Config.LogIpAddresses?player.getAddress().getAddress().getHostAddress().toString():""));
	}

	@HawkEvent(dataType = DataType.QUIT)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Location loc  = player.getLocation();

		HawkEye.containerManager.checkInventoryClose(player);

		String ip = "";
		try {
			ip = player.getAddress().getAddress().getHostAddress().toString();
		} catch (Exception e) { }

		DataManager.addEntry(new DataEntry(player, DataType.QUIT, loc, Config.LogIpAddresses?ip:""));
	}

	@HawkEvent(dataType = DataType.TELEPORT)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		//Check for inventory close
		HawkEye.containerManager.checkInventoryClose(event.getPlayer());
		Location from = event.getFrom();
		Location to   = event.getTo();
		if (Util.distance(from, to) > 5)
			DataManager.addEntry(new DataEntry(event.getPlayer(), DataType.TELEPORT, from, to.getWorld().getName() + ": " + to.getX() + ", " + to.getY() + ", " + to.getZ()));
	}

	/**
	 * Handles several actions:
	 * OPEN_CHEST, DOOR_INTERACT, LEVER, STONE_BUTTON, FLINT_AND_STEEL, LAVA_BUCKET, WATER_BUCKET
	 */
	@SuppressWarnings("incomplete-switch")
	@HawkEvent(dataType = {DataType.OPEN_CONTAINER, DataType.DOOR_INTERACT, DataType.LEVER, DataType.STONE_BUTTON, DataType.LAVA_BUCKET, DataType.WATER_BUCKET, DataType.SPAWNMOB_EGG})
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		//Check for inventory close
		HawkEye.containerManager.checkInventoryClose(player);


		if (block != null) {

			Location loc = block.getLocation();

			switch (block.getType()) {
				case FURNACE:
				case DISPENSER:
				case CHEST:
				case ANVIL:
				case BEACON:
				case BREWING_STAND:
				case ENDER_CHEST:
					if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
						//Call container manager for inventory open
						HawkEye.containerManager.checkInventoryOpen(player, block);
						DataManager.addEntry(new DataEntry(player, DataType.OPEN_CONTAINER, loc, Integer.toString(block.getTypeId())));
					}
					break;
				case WOODEN_DOOR:
				case TRAP_DOOR:
				case FENCE_GATE:
					DataManager.addEntry(new DataEntry(player, DataType.DOOR_INTERACT, loc, ""));
					break;
				case LEVER:
					DataManager.addEntry(new DataEntry(player, DataType.LEVER, loc, ""));
					break;
				case STONE_BUTTON:
					DataManager.addEntry(new DataEntry(player, DataType.STONE_BUTTON, loc, ""));
					break;
			}

			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				loc = block.getRelative(event.getBlockFace()).getLocation();
		        Location locs = block.getLocation();
				switch (player.getItemInHand().getType()) {
					case FLINT_AND_STEEL:
						DataManager.addEntry(new SimpleRollbackEntry(player, DataType.FLINT_AND_STEEL, loc, ""));
						break;
					case LAVA_BUCKET:
						DataManager.addEntry(new SimpleRollbackEntry(player, DataType.LAVA_BUCKET, loc, ""));
						break;
					case WATER_BUCKET:
						DataManager.addEntry(new SimpleRollbackEntry(player, DataType.WATER_BUCKET, loc, ""));
						break;
					case MONSTER_EGG:
						DataManager.addEntry(new DataEntry(player, DataType.SPAWNMOB_EGG, locs, ""));
						break;
				}
			}
		}
	}

	@HawkEvent(dataType = DataType.ITEM_DROP)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		ItemStack stack = event.getItemDrop().getItemStack();
		String data = null;
		if (stack.getDurability() != 0)
			data = stack.getAmount() + "x " + stack.getTypeId() + ":" + stack.getData().getData();
		else
			data = stack.getAmount() + "x " + stack.getTypeId();
		DataManager.addEntry(new DataEntry(player, DataType.ITEM_DROP, player.getLocation().getBlock().getLocation(), data));
	}

	@HawkEvent(dataType = DataType.ITEM_PICKUP)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		ItemStack stack = event.getItem().getItemStack();
		String data = null;
		if (stack.getDurability() != 0)
			data = stack.getAmount() + "x " + stack.getTypeId() + ":" + stack.getData().getData();
		else
			data = stack.getAmount() + "x " + stack.getTypeId();
		DataManager.addEntry(new DataEntry(player, DataType.ITEM_PICKUP, player.getLocation().getBlock().getLocation(), data));
	}
}
