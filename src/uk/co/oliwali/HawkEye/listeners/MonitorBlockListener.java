package uk.co.oliwali.HawkEye.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.blocks.BlockHandlerContainer;
import uk.co.oliwali.HawkEye.blocks.blockhandlers.BlockHandler;
import uk.co.oliwali.HawkEye.blocks.blockhandlers.SignBlockHandler;
import uk.co.oliwali.HawkEye.database.Consumer;
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.entry.SimpleRollbackEntry;
import uk.co.oliwali.HawkEye.util.Config;

/**
 * Block listener class for HawkEye
 *
 * @author oliverw92
 */
public class MonitorBlockListener extends HawkEyeListener {

    private BlockHandlerContainer blockHandlerContainer;

    public MonitorBlockListener(Consumer consumer, BlockHandlerContainer blockHandlerContainer) {
        super(consumer);
        this.blockHandlerContainer = blockHandlerContainer;
    }

    @HawkEvent(dataType = DataType.BLOCK_BREAK)
    public void onBlockBreak(BlockBreakEvent event) {

        Block block = event.getBlock();
        Player player = event.getPlayer();
        Material type = block.getType();

        if (type == Material.AIR || Config.BlockFilter.contains(type.getId())) return;

        BlockHandler hb = blockHandlerContainer.getBlockHandler(type.getId());

        block = hb.getCorrectBlock(block);

        hb.logAttachedBlocks(consumer, block, player, DataType.BLOCK_BREAK);

        if (hb instanceof SignBlockHandler && DataType.SIGN_BREAK.isLogged())
            consumer.addEntry(new SignEntry(player, DataType.SIGN_BREAK, block));
        else
            consumer.addEntry(new BlockEntry(player, DataType.BLOCK_BREAK, block));
    }

    @HawkEvent(dataType = DataType.BLOCK_PLACE)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block b = event.getBlock();

        if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST || Config.BlockFilter.contains(b.getTypeId()))
            return;

        consumer.addEntry(new BlockChangeEntry(event.getPlayer(), (b.getType().equals(Material.FIRE)) ? DataType.FLINT_AND_STEEL : DataType.BLOCK_PLACE, b.getLocation(), event.getBlockReplacedState(), b.getState()));
    }

    @HawkEvent(dataType = DataType.SIGN_PLACE)
    public void onSignChange(SignChangeEvent event) {
        consumer.addEntry(new SignEntry(event.getPlayer().getName(), DataType.SIGN_PLACE, event.getBlock(), event.getLines()));
    }

    @HawkEvent(dataType = DataType.BLOCK_FORM)
    public void onBlockForm(BlockFormEvent event) {
        consumer.addEntry(new BlockChangeEntry(ENVIRONMENT, DataType.BLOCK_FORM, event.getBlock().getLocation(), event.getBlock().getState(), event.getNewState()));
    }

    @HawkEvent(dataType = DataType.BLOCK_FADE)
    public void onBlockFade(BlockFadeEvent event) {
        consumer.addEntry(new BlockChangeEntry(ENVIRONMENT, DataType.BLOCK_FADE, event.getBlock().getLocation(), event.getBlock().getState(), event.getNewState()));
    }

    @HawkEvent(dataType = DataType.BLOCK_BURN)
    public void onBlockBurn(BlockBurnEvent event) {
        consumer.addEntry(new BlockEntry(ENVIRONMENT, DataType.BLOCK_BURN, event.getBlock()));
    }

    @HawkEvent(dataType = DataType.LEAF_DECAY)
    public void onLeavesDecay(LeavesDecayEvent event) {
        Block block = event.getBlock();

        if (block != null) {
            consumer.addEntry(new BlockEntry(ENVIRONMENT, DataType.LEAF_DECAY, event.getBlock()));
        }
    }

    @HawkEvent(dataType = DataType.BLOCK_IGNITE)
    public void onBlockIgnite(BlockIgniteEvent event) {
        IgniteCause ig = event.getCause();

        if (ig != IgniteCause.FLINT_AND_STEEL) {

            Location loc = event.getBlock().getLocation();

            consumer.addEntry(new SimpleRollbackEntry(ENVIRONMENT, DataType.BLOCK_IGNITE, loc, ig.name()));
        }
    }

}
