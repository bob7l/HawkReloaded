package uk.co.oliwali.HawkEye.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.blocks.HawkBlock;
import uk.co.oliwali.HawkEye.blocks.HawkBlockType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.util.BlockUtil;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.InventoryUtil;

/**
 * WorldEdit listener
 * Use EventHandler for WorldEdit Actions priorities
 * @author bob7l
 */

public class MonitorWorldEditListener implements Listener {

	WorldEditPlugin we;

	public MonitorWorldEditListener() {
		this.we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onWESuperPickaxe(PlayerInteractEvent event) {

		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			Player player = event.getPlayer();

			if (we.wrapPlayer(player).isHoldingPickAxe() && (we.getSession(player).hasSuperPickAxe())) {
				Block block = event.getClickedBlock();
				Material type = block.getType();

				if (Config.BlockFilter.contains(type.getId())) return;

				if (type == Material.WALL_SIGN || type == Material.SIGN_POST)
					DataManager.addEntry(new SignEntry(player, DataType.SIGN_BREAK, block));
				else {
					if (BlockUtil.isInventoryHolder(type.getId()) && Config.isLogged(DataType.CONTAINER_TRANSACTION)) {
						InventoryUtil.handleHolderRemoval(player.getName(), block.getState());
					}
					HawkBlock hb = HawkBlockType.getHawkBlock(type.getId());

					block = hb.getCorrectBlock(block);

					hb.logAttachedBlocks(block, player, DataType.SUPER_PICKAXE);

					DataManager.addEntry(new BlockEntry(player, DataType.SUPER_PICKAXE, block));
				}
				DataManager.addEntry(new BlockEntry(player, DataType.SUPER_PICKAXE, event.getClickedBlock()));
			}
		}
	}
}