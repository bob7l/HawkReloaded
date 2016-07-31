package uk.co.oliwali.HawkEye.listeners;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.blocks.BlockHandlerContainer;
import uk.co.oliwali.HawkEye.blocks.blockhandlers.BlockHandler;
import uk.co.oliwali.HawkEye.blocks.blockhandlers.SignBlockHandler;
import uk.co.oliwali.HawkEye.database.Consumer;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.util.Config;

/**
 * WorldEdit listener
 * Use EventHandler for WorldEdit Actions priorities
 * @author bob7l
 */

public class MonitorWorldEditListener extends HawkEyeListener {

	private WorldEditPlugin we;

	private BlockHandlerContainer blockHandlerContainer;

	public MonitorWorldEditListener(Consumer consumer, BlockHandlerContainer blockHandlerContainer) {
		super(consumer);
		this.blockHandlerContainer = blockHandlerContainer;
		this.we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
	}

	@HawkEvent(dataType = {DataType.SUPER_PICKAXE})
	public void onWESuperPickaxe(PlayerInteractEvent event) {

		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			Player player = event.getPlayer();

			if (we.wrapPlayer(player).isHoldingPickAxe() && (we.getSession(player).hasSuperPickAxe())) {
				Block block = event.getClickedBlock();
				Material type = block.getType();

				if (type == Material.AIR || Config.BlockFilter.contains(type.getId())) return;

				BlockHandler hb = blockHandlerContainer.getBlockHandler(type.getId());

				block = hb.getCorrectBlock(block);

				hb.logAttachedBlocks(consumer, block, player, DataType.SUPER_PICKAXE);

				if (hb instanceof SignBlockHandler && DataType.SIGN_BREAK.isLogged())
					consumer.addEntry(new SignEntry(player, DataType.SIGN_BREAK, block));
				
				else consumer.addEntry(new BlockEntry(player, DataType.SUPER_PICKAXE, block));
			}
		}
	}
}