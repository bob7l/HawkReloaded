package uk.co.oliwali.HawkEye.blocks.blockhandlers;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.blocks.BlockHandlerContainer;
import uk.co.oliwali.HawkEye.database.Consumer;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.util.BlockUtil;

public class LeafBlockHandler extends DefaultBlockHandler {

    public LeafBlockHandler(BlockHandlerContainer hawkBlockType) {
        super(hawkBlockType);
    }

    @Override
    public void logAttachedBlocks(Consumer consumer, Block b, Player p, DataType type) {
        for (BlockFace face : BlockUtil.faces) {

            Block attch = b.getRelative(face);
            BlockHandler hb = hawkBlockContainer.getBlockHandler(attch.getTypeId());

            if (hb.isAttached()) {
                hb.logAttachedBlocks(consumer, attch, p, type);

                if (hb instanceof SignBlockHandler && DataType.SIGN_BREAK.isLogged())
                    consumer.addEntry(new SignEntry(p, DataType.SIGN_BREAK, hb.getCorrectBlock(attch)));
                else if (hb instanceof VineBlockHandler)
                    consumer.addEntry(new BlockEntry(p, type, hb.getCorrectBlock(attch)));
            }
        }
    }
}