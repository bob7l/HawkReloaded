package uk.co.oliwali.HawkEye.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.blocks.HawkBlock;
import uk.co.oliwali.HawkEye.blocks.HawkBlockType;
import uk.co.oliwali.HawkEye.blocks.SignBlock;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.ContainerEntry;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.entry.SimpleRollbackEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.InventoryUtil;

/**
 * Block listener class for HawkEye
 * @author oliverw92
 */
public class MonitorBlockListener extends HawkEyeListener {
	
	public MonitorBlockListener(HawkEye HawkEye) {
		super(HawkEye);
	}

	@HawkEvent(dataType = DataType.BLOCK_BREAK)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		Material type = block.getType();

		if (type == Material.AIR || Config.BlockFilter.contains(type.getId())) return;

		HawkBlock hb = HawkBlockType.getHawkBlock(type.getId());

		block = hb.getCorrectBlock(block);

		hb.logAttachedBlocks(block, player, DataType.BLOCK_BREAK);

		if (hb instanceof SignBlock && DataType.SIGN_BREAK.isLogged())
			DataManager.addEntry(new SignEntry(player, DataType.SIGN_BREAK, block));

		else DataManager.addEntry(new BlockEntry(player, DataType.BLOCK_BREAK, block));
	}

	@HawkEvent(dataType = DataType.BLOCK_PLACE)
	public void onBlockPlace(BlockPlaceEvent event) {
		Block b = event.getBlock();
		
		if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST || Config.BlockFilter.contains(b.getTypeId())) return;

		DataManager.addEntry(new BlockChangeEntry(event.getPlayer(), (b.getType().equals(Material.FIRE)) ? DataType.FLINT_AND_STEEL : DataType.BLOCK_PLACE, b.getLocation(), event.getBlockReplacedState(), b.getState()));
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

	@HawkEvent(dataType = DataType.CONTAINER_TRANSACTION) 
	public void onHopperTransfer(InventoryMoveItemEvent event) {
		
		if (!Config.LogHopper) return;
		
		InventoryHolder from =  event.getSource().getHolder();
		InventoryHolder to = event.getDestination().getHolder();
		ItemStack item = event.getItem();

		if (!(from instanceof Hopper) && InventoryUtil.isHolderValid(from)) { //Items being removed from inventory block
			DataManager.addEntry(new ContainerEntry("Environment", InventoryUtil.getHolderLoc(from), InventoryUtil.compressItem(item, false)));
		}

		if (!(to instanceof Hopper) && InventoryUtil.isHolderValid(to)) { //Items being added to inventory block
			DataManager.addEntry(new ContainerEntry("Environment", InventoryUtil.getHolderLoc(to), InventoryUtil.compressItem(item, true)));
		}
		
	}
}
