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
import uk.co.oliwali.HawkEye.blocks.SignBlock;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.util.Config;

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

				if (type == Material.AIR || Config.BlockFilter.contains(type.getId())) return;

				HawkBlock hb = HawkBlockType.getHawkBlock(type.getId());

				block = hb.getCorrectBlock(block);

				hb.logAttachedBlocks(block, player, DataType.SUPER_PICKAXE);

				if (hb instanceof SignBlock && Config.isLogged(DataType.SIGN_BREAK))
					DataManager.addEntry(new SignEntry(player, DataType.SIGN_BREAK, block));
				
				else DataManager.addEntry(new BlockEntry(player, DataType.SUPER_PICKAXE, block));
			}
		}
	}
}