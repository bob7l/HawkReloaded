package uk.co.oliwali.HawkEye.util;

import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.entry.HangingEntry;

public class EntityUtil {

	public static int getFace(BlockFace block) {
		switch(block) {
		case SOUTH: return 0;
		case WEST: return 1;
		case NORTH: return 2;
		case EAST: return 3;
		default:
			return 0;
		}
	}

	public static BlockFace getFaceFromInt(int block) {
		switch(block) {
		case 0: return BlockFace.SOUTH;
		case 1: return BlockFace.WEST;
		case 2: return BlockFace.NORTH;
		case 3: return BlockFace.EAST;
		default:
			return BlockFace.SOUTH;
		}
	}

	public static String getStringName(String data) {
		String[] args = data.split(":");
		if (args[0].equals("389")) {
			Material mat = Material.getMaterial(Integer.parseInt(args[2]));
			return "ItemFrame" + (mat.equals(Material.AIR) ? "" : " with " + mat.toString());
		}
		return "Painting";
	}

	public static void setBlockString(Block b, String blockData) {
		String[] args = blockData.split(":");
		int type = Integer.parseInt(args[0]);
		int faceint = Integer.parseInt(args[1]);
		BlockFace face = getFaceFromInt(faceint);
		spawnFrame(b, face, Integer.parseInt(args[2]), (type == 389) ? true : false);
	}

	public static void spawnFrame(Block l, BlockFace face, int stack, boolean isFrame) {

		Block spawn = l.getRelative(face.getOppositeFace());

		BlockState bs = null;
		BlockState north = null;
		BlockState south = null;
		BlockState east = null;
		BlockState west = null;

		if (!(spawn.getType().isSolid())) {
			bs = spawn.getState();
			spawn.setType(Material.STONE);
		}

		Block b = spawn.getRelative(BlockFace.NORTH);
		if (face != BlockFace.NORTH && b.getType() == Material.AIR) {
			north = b.getState();
			b.setType(Material.STONE);
		} else if (face == BlockFace.NORTH && b.getType() != Material.AIR) {
			north = b.getState();
			b.setType(Material.AIR);
		}

		b = spawn.getRelative(BlockFace.EAST);
		if (face != BlockFace.EAST && b.getType() == Material.AIR) {
			east = b.getState();
			b.setType(Material.STONE);
		} else if (face == BlockFace.EAST && b.getType() != Material.AIR) {
			east = b.getState();
			b.setType(Material.AIR);
		}

		b = spawn.getRelative(BlockFace.SOUTH);
		if (face != BlockFace.SOUTH && b.getType() == Material.AIR) {
			south = b.getState();
			b.setType(Material.STONE);
		} else if (face == BlockFace.SOUTH && b.getType() != Material.AIR) {
			south = b.getState();
			b.setType(Material.AIR);
		}

		b = spawn.getRelative(BlockFace.WEST);
		if (face != BlockFace.WEST && b.getType() == Material.AIR) {
			west = b.getState();
			b.setType(Material.STONE);
		} else if (face == BlockFace.WEST && b.getType() != Material.AIR) {
			west = b.getState();
			b.setType(Material.AIR);
		}
		ItemFrame itemframe = null;
		Painting painting = null;
		try {
			if (isFrame) {
				itemframe = spawn.getWorld().spawn(spawn.getLocation(), ItemFrame.class);
				itemframe.setFacingDirection(face.getOppositeFace(), true);
				itemframe.setItem(new ItemStack(stack));
			} else {
				painting = spawn.getWorld().spawn(spawn.getLocation(), Painting.class);
				painting.setFacingDirection(face.getOppositeFace(), true);
				painting.setArt(Art.getById(stack));
			}
		} catch(IllegalArgumentException ex) {
			//Do nothing
		} finally {
			if (bs != null) bs.update(true);
			if (north != null) north.update(true);
			if (east != null) east.update(true);
			if (south != null) south.update(true);
			if (west != null) west.update(true);
		}
	}

	public static void setEntityString(Block b, String data) {
		EntityType type = EntityType.fromName(data);
		Location loc = b.getLocation();
		try {
			loc.getWorld().spawnEntity(loc, type);
		} catch (Exception e) {
			Util.warning("Unable to spawn " + data + " at: " + loc.toString());
		}
	}

	public static String entityToString(Entity e) {
		if (e == null || e.getType() == null) return "Environment";
		if (e instanceof Player) return ((Player)e).getName();
		return e.getType().name();
	}
	
	public static HangingEntry getHangingEntry(DataType type, Entity e, String remover) {
		
		if (e instanceof ItemFrame) {
			ItemFrame frame = (ItemFrame) e;
			return new HangingEntry(remover, type, e.getLocation().getBlock().getLocation(), 389, getFace(frame.getAttachedFace()), frame.getItem().getTypeId());
		} else if (e instanceof Painting) {
			Painting paint = (Painting) e;
			return new HangingEntry(remover, type, e.getLocation().getBlock().getLocation(), 321, getFace(paint.getAttachedFace()), paint.getArt().getId());
		}
		return null;
	}
}