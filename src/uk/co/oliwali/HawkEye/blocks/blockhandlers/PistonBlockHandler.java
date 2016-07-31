package uk.co.oliwali.HawkEye.blocks.blockhandlers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.Consumer;

public class PistonBlockHandler implements BlockHandler {

    @Override
    public void restore(Block b, int id, int data) {
        if (id == 34)
            b = b.getRelative(getPistonFromExtension(data));

        switch (data) { //Check data just to be sure they will be placed correctly!
            case 10:
            case 2:
                b.setTypeIdAndData(id, ((byte) 2), true);
                break;
            case 4:
            case 12:
                b.setTypeIdAndData(id, ((byte) 4), true);
                break;
            case 3:
            case 11:
                b.setTypeIdAndData(id, ((byte) 3), true);
                break;
            case 13:
            case 5:
                b.setTypeIdAndData(id, ((byte) 5), true);
                break;
            case 8:
            case 0:
                b.setTypeIdAndData(id, ((byte) 0), true);
                break;
            case 1:
            case 9:
                b.setTypeIdAndData(id, ((byte) 1), true);
                break;
        }
    }

    @Override
    public void logAttachedBlocks(Consumer consumer, Block b, Player p, DataType type) {}

    @Override
    public Block getCorrectBlock(Block b) {
        if (b.getType() == Material.PISTON_EXTENSION) {
            return b.getRelative(getPistonFromExtension(b.getData()));
        }
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

    private BlockFace getPistonFromExtension(int data) {
        switch (data) {
            case 10:
            case 2:
                return BlockFace.SOUTH;
            case 4:
            case 12:
                return BlockFace.EAST;
            case 3:
            case 11:
                return BlockFace.NORTH;
            case 13:
            case 5:
                return BlockFace.WEST;
            case 8:
            case 0:
                return BlockFace.UP;
            case 1:
            case 9:
                return BlockFace.DOWN;

            default:
                return BlockFace.EAST;
        }
    }
}