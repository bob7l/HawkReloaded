package uk.co.oliwali.HawkEye.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.blocks.HawkBlockType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.entry.containerentries.ContainerEntry;
import uk.co.oliwali.HawkEye.entry.containerentries.ContainerExtract;
import uk.co.oliwali.HawkEye.entry.containerentries.ContainerInsert;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.InventoryUtil;
import uk.co.oliwali.HawkEye.util.Util;

import java.util.HashMap;
import java.util.List;

/**
 * Player listener class for HawkEye
 *
 * @author oliverw92
 */
public class MonitorPlayerListener extends HawkEyeListener {

    private HashMap<String, List<ItemStack>> invTransactions = new HashMap<>();

    @HawkEvent(dataType = DataType.CHAT)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        DataManager.addEntry(new DataEntry(player, DataType.CHAT, player.getLocation(), event.getMessage()));
    }

    @HawkEvent(dataType = DataType.COMMAND)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (Config.CommandFilter.contains(event.getMessage().split(" ")[0])) return;
        DataManager.addEntry(new DataEntry(player, DataType.COMMAND, player.getLocation(), event.getMessage()));
    }

    @HawkEvent(dataType = DataType.JOIN)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        DataManager.addEntry(new DataEntry(player, DataType.JOIN, loc, Config.LogIpAddresses ? player.getAddress().getAddress().getHostAddress() : ""));
    }

    @HawkEvent(dataType = DataType.QUIT)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        String ip = "";
        try {
            ip = player.getAddress().getAddress().getHostAddress();
        } catch (Exception e) {
        }

        DataManager.addEntry(new DataEntry(player, DataType.QUIT, loc, Config.LogIpAddresses ? ip : ""));
    }

    @HawkEvent(dataType = DataType.TELEPORT)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (Util.distance(from, to) > 5)
            DataManager.addEntry(new DataEntry(event.getPlayer(), DataType.TELEPORT, from, to.getWorld().getName() + ": " + to.getX() + ", " + to.getY() + ", " + to.getZ()));
    }

    /**
     * Handles several actions:
     * OPEN_CHEST, DOOR_INTERACT, LEVER, STONE_BUTTON, FLINT_AND_STEEL, LAVA_BUCKET, WATER_BUCKET
     */

    @HawkEvent(dataType = {DataType.OPEN_CONTAINER, DataType.DOOR_INTERACT, DataType.LEVER, DataType.STONE_BUTTON, DataType.SPAWNMOB_EGG, DataType.CROP_TRAMPLE})
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();


        if (block != null) {

            Location loc = block.getLocation();

            switch (block.getType()) {
                case SOIL:
                    if (event.getAction() == Action.PHYSICAL) {
                        Block rel = block.getRelative(BlockFace.UP);

                        if (HawkBlockType.getHawkBlock(rel.getTypeId()).equals(HawkBlockType.plant)) {
                            DataManager.addEntry(new BlockEntry(player, DataType.CROP_TRAMPLE, rel));
                        }
                    }
                    break;
                case FURNACE:
                case DISPENSER:
                case CHEST:
                case ANVIL:
                case BEACON:
                case BREWING_STAND:
                case ENDER_CHEST:
                    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
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
                default:
                    return;
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location locs = block.getLocation();
                if (player.getItemInHand().getType().equals(Material.MONSTER_EGG)) {
                    DataManager.addEntry(new DataEntry(player, DataType.SPAWNMOB_EGG, locs, ""));
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

    @HawkEvent(dataType = {DataType.LAVA_BUCKET, DataType.WATER_BUCKET})
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Location loc = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
        DataType type = (event.getBucket().equals(Material.WATER_BUCKET) ? DataType.WATER_BUCKET : DataType.LAVA_BUCKET);

        DataManager.addEntry(new BlockChangeEntry(event.getPlayer(), type, loc, loc.getBlock().getState(), event.getBucket().getId()));
    }

    @HawkEvent(dataType = {DataType.CONTAINER_EXTRACT, DataType.CONTAINER_INSERT})
    public void onInventoryClose(InventoryCloseEvent event) {

        String player = event.getPlayer().getName();
        InventoryHolder holder = event.getInventory().getHolder();

        if (InventoryUtil.isHolderValid(holder) && invTransactions.containsKey(player)) {

            List<ItemStack> oldInv = invTransactions.get(player);

            if (oldInv != null) {
                invTransactions.remove(player);

                List<ItemStack>[] dif = InventoryUtil.getDifference(oldInv, InventoryUtil.compressInventory(holder.getInventory().getContents()));

                if (dif[0].size() > 0 && DataType.CONTAINER_EXTRACT.isLogged()) {
                    for (String str : InventoryUtil.serializeInventory(ContainerEntry.getSerializer(), dif[0]))
                        DataManager.addEntry(new ContainerExtract(player, DataType.CONTAINER_EXTRACT, InventoryUtil.getHolderLoc(holder), str));
                }

                if (dif[1].size() > 0 && DataType.CONTAINER_INSERT.isLogged()) {
                    for (String str : InventoryUtil.serializeInventory(ContainerEntry.getSerializer(), dif[1]))
                        DataManager.addEntry(new ContainerInsert(player, DataType.CONTAINER_INSERT, InventoryUtil.getHolderLoc(holder), str));
                }

            }
        }
    }

    @HawkEvent(dataType = {DataType.CONTAINER_EXTRACT, DataType.CONTAINER_INSERT})
    public void onInventoryOpen(InventoryOpenEvent event) {
        String player = event.getPlayer().getName();
        InventoryHolder holder = event.getInventory().getHolder();

        if (InventoryUtil.isHolderValid(holder)) {
            invTransactions.put(player, InventoryUtil.compressInventory(holder.getInventory().getContents()));
        }
    }

}
