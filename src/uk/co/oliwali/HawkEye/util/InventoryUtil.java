package uk.co.oliwali.HawkEye.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.ContainerEntry;

public class InventoryUtil {

	public static HashMap<String,Integer> compressInventory(ItemStack[] inventory) {
		HashMap<String,Integer> items = new HashMap<String,Integer>();
		for (ItemStack item : inventory) {
			if (item == null) continue;
			String enchantments = "";
			Map<Enchantment, Integer> enchants = item.getEnchantments();
			if (!enchants.isEmpty()) {
				for (Entry<Enchantment, Integer> entry : enchants.entrySet()) {
					enchantments = enchantments + "-" + Enchantment.getByName(entry.getKey().getName()).getId() + "x" + entry.getValue();
				}
			}
			String iString = BlockUtil.getItemString(item) + enchantments;
			if (items.containsKey(iString)) items.put(iString, items.get(iString) + item.getAmount());
			else items.put(iString, item.getAmount());
		}
		return items;
	}

	public static ItemStack uncompressItem(String data) {
		data = data.substring(1);
		String[] item = data.split("~");
		String[] enchants = item[0].split("-");
		String[] info = enchants[0].split(":");
		ItemStack stack = null;
		if (info.length == 1) {
			stack = BlockUtil.itemStringToStack(info[0], Integer.parseInt(item[1]));
		} else {
			stack = BlockUtil.itemStringToStack(info[0] + ":" + info[1], Integer.parseInt(item[1]));
		}
		if (enchants.length > 0) {
			for (String s : enchants) {
				String[] types = s.split("x");
				if (types.length != 1) {
					Enchantment en = (Util.isInteger(types[0])?Enchantment.getById(Integer.parseInt(types[0])):Enchantment.getByName(types[0]));
					if (en != null) {
						stack.addUnsafeEnchantment(en, Integer.parseInt(types[1]));
					}
				}
			}
		}
		return stack;
	}

	public static String dataToString(String data) {
		StringBuffer type = new StringBuffer();
		for (String changes : data.split("@")) {
			String[] item = changes.split("~");
			String[] enchants = item[0].substring(1).split("-");
			String c = changes.startsWith("+")?"&a":"&4";
			String ench = "";
			if (enchants.length != 1) {
				ench = "*Enchant*";
			}
			type.append(", " + c+item[1] + "x " + BlockUtil.getBlockStringName(enchants[0]) + ench);
		}
		return type.toString().substring(2);
	}

	/**
	 * Checks if the containers content was removed
	 * @param InventoryHolder - the chest we're checking
	 * @param HashMap - OLD inventory
	 * @param HashMap - NEW inventory
	 * @return updated inventory
	 */
	public static String compareInvs(InventoryHolder holder, HashMap<String,Integer> map1, HashMap<String,Integer> map2) {
		HashMap<String,Integer> items1 = map1;
		HashMap<String,Integer> items = map2;
		if (items1 == null && items == null) return null;
		ArrayList<String> ses = new ArrayList<String>();
		String ts = "";
		for (Entry<String, Integer> entry : items.entrySet()) {
			int count = entry.getValue();
			String key = entry.getKey();
			if (items1.containsKey(key)) {
				int c = items1.get(key);
				if (count < c) {
					ses.add("-" + key + "~" + (c - count));
				} else if (count > c) {
					ses.add("+" + key + "~" + (count - c));
				}
				items1.remove(key);
			} else {
				ses.add("+" + key + "~" + count);
			}
		} 
		for (Entry<String, Integer> entry : items1.entrySet()) {
			ses.add("-" + entry.getKey() + "~" + entry.getValue());
		}
		if (!ses.isEmpty()) {
			for (String s : ses) {
				if (ts.length() < 1) ts = s;
				else ts = ts + "@" + s;
			}
			return ts.equals("@")?null:ts;
		}
		return null;
	}

	public static void handleHolderRemoval(String remover, BlockState state) {
		InventoryHolder holder = (InventoryHolder) state;
		if (InventoryUtil.isHolderValid(holder)) {
			String data = InventoryUtil.compareInvs(holder, InventoryUtil.compressInventory(holder.getInventory().getContents()), new HashMap<String, Integer>());
			if (data == null) return;
			DataManager.addEntry(new ContainerEntry(remover, InventoryUtil.getHolderLoc(holder), data));
		}
	}

	public static Location getHolderLoc(InventoryHolder holder) {
		if (holder instanceof Chest) return ((Chest)holder).getLocation();
		if (holder instanceof DoubleChest) return ((DoubleChest)holder).getLocation().getBlock().getLocation(); //Need the block location
		if (holder instanceof Furnace) return ((Furnace)holder).getLocation();
		if (holder instanceof Dispenser) return ((Dispenser)holder).getLocation();
		return null;
	}

	public static boolean isHolderValid(InventoryHolder holder) {
		if (holder instanceof Chest) return Config.logChest;
		if (holder instanceof DoubleChest) return Config.logDoubleChest;
		if (holder instanceof Furnace) return Config.logFurnace;
		if (holder instanceof Dispenser)return Config.logDispenser;
		return false;
	}


	/**
	 * Updates the container-transaction string from an older hawkeye
	 * @param string - old inventory
	 * @return updated string
	 */
	public static String updateInv(String old) {
		StringBuffer ns = new StringBuffer();
		String[] sides = old.split("@");
		if (sides.length == 1) {
			for (String s : sides[0].split("&")) {
				ns.append("@" + "+" + s);
			}
		} else if (sides[0].equals("")) {
			for (String s : sides[1].split("&")) {
				ns.append("@" + "-" + s);
			}
		} else {
			for (String s : sides[0].split("&")) {
				ns.append("@" + "+" + s);
			}
			for (String s : sides[1].split("&")) {
				ns.append("@" + "-" + s);
			}
		}
		return ns.toString().replace(",", "~").substring(1);
	}
}
