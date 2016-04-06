package uk.co.oliwali.HawkEye.util;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import uk.co.oliwali.HawkEye.blocks.HawkBlockType;

/**
 * Contains utilities for manipulating blocks without losing data
 * @author oliverw92
 */
public class BlockUtil {

	public static final BlockFace[] faces = new BlockFace[] {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
	
	/**
	 * Gets the block in 'string form'.
	 * e.g. blockid:datavalue
	 * @param block BlockState of the block you wish to convert
	 * @return string representing the block
	 */
	public static String getBlockString(Block block) {
		return getBlockString(block.getState());
	}

	public static String getBlockString(BlockState block) {
		if (block.getRawData() != 0)
			return block.getTypeId() + ":" + block.getRawData();
		return Integer.toString(block.getTypeId());
	}

	/**
	 * Same as getBlockString() except for ItemStack
	 * @param stack ItemStack you wish to convert
	 * @return string representing the item
	 */
	public static String getItemString(ItemStack stack) {
		short data = stack.getDurability();
		int type = stack.getTypeId();

		if (data > 0)
			return type + ":" + data;

		return Integer.toString(type);
	}

	public static ItemStack getItemFromString(String data) {
		String[] bits = data.split(":");

		if (bits.length > 1)
			return new ItemStack(Integer.parseInt(bits[0]), 1, Short.parseShort(bits[1]));

		return new ItemStack(Integer.parseInt(bits[0]));
	}

	public static String formatItemStack(ItemStack itemStack) {
		StringBuilder sb = new StringBuilder();

		sb.append("x").append(itemStack.getAmount()).append(" ");

		sb.append(WordUtils.capitalizeFully(itemStack.getType().name()));

		if (itemStack.getDurability() > 0 && itemStack.getType().getMaxDurability() == 0) {
			sb.append(":").append(itemStack.getDurability());
		}

		if (itemStack.getEnchantments().size() > 0) {
			sb.append(" (x").append(itemStack.getEnchantments().size()).append(" Enchants)");
		}

		return sb.toString();
	}

	/**
	 * Converts an item string into an ItemStack
	 * @param item item string representing the material and data
	 * @param amount
	 * @return an ItemStack
	 */
	public static ItemStack itemStringToStack(String item, Integer amount) {
		String[] itemArr = item.split(":");
		if (itemArr.length > 1)
			return new ItemStack(Integer.parseInt(itemArr[0]), amount, (itemArr[1].length() <= 3 ? Byte.parseByte(itemArr[1]): ((short) Integer.parseInt(itemArr[1]))));
		return new ItemStack(Integer.parseInt(itemArr[0]), amount);
	}

	/**
	 * Returns the name of the block, with its data if applicable
	 * @param blockData
	 * @return
	 */
	public static String getBlockStringName(String blockData) {
		String[] blockArr = blockData.split(":");
		if (!Util.isInteger(blockArr[0])) return blockData;
		if (blockArr.length > 1)
			return Material.getMaterial(Integer.parseInt(blockArr[0])).name() + ":" + blockArr[1];
		else return Material.getMaterial(Integer.parseInt(blockArr[0])).name();
	}

	/**
	 * Sets the block type and data to the inputted block string
	 * @param block Block to be changed
	 * @param blockData string form of a block
	 */
	public static void setBlockString(Block block, String blockData) {
		String[] blockArr = blockData.split(":");
		if (!Util.isInteger(blockArr[0])) return;
		int type = Integer.parseInt(blockArr[0]);
		int data = (blockArr.length > 1) ? Integer.parseInt(blockArr[1]) : 0;
		
		HawkBlockType.getHawkBlock(type).Restore(block, type, data);
	}

	/**
	 * Returns ID section of a block string
	 * @param string
	 * @return int ID
	 */
	public static int getIdFromString(String string) {
		if (!Util.isInteger(string.split(":")[0])) return 0;
		return Integer.parseInt(string.split(":")[0]);
	}

	/**
	 * Returns data section of a block string
	 * @param string
	 * @return int data
	 */
	public static byte getDataFromString(String string) {
		if (string.split(":").length == 1) return 0;
		return (byte)Integer.parseInt(string.split(":")[1]);
	}

	/**
	 * Returns whether or not the item is attached
	 */
	public static boolean isAttached(Block base, Block attached) {
		MaterialData bs = attached.getState().getData();

		return !(bs instanceof Attachable) || attached.getType() == Material.VINE
				|| attached.getRelative(((Attachable) bs).getAttachedFace()).equals(base);

	}
}
