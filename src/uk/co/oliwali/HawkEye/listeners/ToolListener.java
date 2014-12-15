package uk.co.oliwali.HawkEye.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import uk.co.oliwali.HawkEye.SessionManager;
import uk.co.oliwali.HawkEye.ToolManager;
import uk.co.oliwali.HawkEye.util.Config;

/**
 * Block listener class for HawkEye Tools
 * @author oliverw92
 */
public class ToolListener implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (event.getItemInHand().getType() == Config.ToolBlock && SessionManager.getSession(player).isUsingTool()) {
			ToolManager.toolSearch(player, event.getBlock());
			if (player.getGameMode() == GameMode.SURVIVAL)
				player.updateInventory();
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.LEFT_CLICK_BLOCK && player.getItemInHand().getType() == Config.ToolBlock && SessionManager.getSession(player).isUsingTool()) {
			ToolManager.toolSearch(player, event.getClickedBlock());
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDisconnect(PlayerQuitEvent event) {
		SessionManager.removeSession(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDisconnect(PlayerKickEvent event) {
		SessionManager.removeSession(event.getPlayer());
	}

}
