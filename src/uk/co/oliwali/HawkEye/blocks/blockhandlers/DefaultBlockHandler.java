package uk.co.oliwali.HawkEye.blocks.blockhandlers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.blocks.BlockHandlerContainer;
import uk.co.oliwali.HawkEye.database.Consumer;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.util.BlockUtil;

public class DefaultBlockHandler implements BlockHandler {

    protected BlockHandlerContainer hawkBlockContainer;

    public DefaultBlockHandler(BlockHandlerContainer hawkBlockContainer) {
        this.hawkBlockContainer = hawkBlockContainer;
    }

    @Override
    public void restore(Block b, int id, int data) {
        b.setTypeIdAndData(id, ((byte) data), false);
    }

    @Override
    public void logAttachedBlocks(Consumer consumer, Block b, Player p, DataType type) {
        Block topb = b.getRelative(BlockFace.UP);
        BlockHandler hb = hawkBlockContainer.getBlockHandler(topb.getTypeId());

        if (hb.isTopBlock()) {

            hb.logAttachedBlocks(consumer, topb, p, type);

            if (hb instanceof SignBlockHandler && DataType.SIGN_BREAK.isLogged())
                consumer.addEntry(new SignEntry(p, DataType.SIGN_BREAK, hb.getCorrectBlock(topb)));
            else
                consumer.addEntry(new BlockEntry(p, type, hb.getCorrectBlock(topb)));
        }

        for (BlockFace face : BlockUtil.faces) {

            Block attch = b.getRelative(face);
            hb = hawkBlockContainer.getBlockHandler(attch.getTypeId());

            if (hb.isAttached() && BlockUtil.isAttached(b, attch)) {

                hb.logAttachedBlocks(consumer, attch, p, type);

                if (attch.getType() == Material.WALL_SIGN && DataType.SIGN_BREAK.isLogged())
                    consumer.addEntry(new SignEntry(p, DataType.SIGN_BREAK, hb.getCorrectBlock(attch)));

                else
                    consumer.addEntry(new BlockEntry(p, type, hb.getCorrectBlock(attch)));
            }
        }
    }

    @Override
    public Block getCorrectBlock(Block b) {
        return b;
    }

    @Override
    public boolean isTopBlock() {
        return false;
    }

    @Override
    public boolean isAttached() {
        return false;
    }
}
